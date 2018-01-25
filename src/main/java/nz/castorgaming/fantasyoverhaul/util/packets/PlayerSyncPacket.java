package nz.castorgaming.fantasyoverhaul.util.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.powers.infusions.creature.CreaturePower;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;

public class PlayerSyncPacket implements IMessage {

	private String infusionID;
	private int curEnergy, maxEnergy, creatureCharges, sinkingCurseLevel;
	String creatureID;
	private String brewEffect;
	private long brewTime;

	public PlayerSyncPacket() {
	}

	public PlayerSyncPacket(EntityPlayer player) {
		infusionID = Infusion.getInfusionID(player);
		curEnergy = Infusion.getCurrentPower(player);
		maxEnergy = Infusion.getMaxEnergy(player);
		creatureID = CreaturePower.getCreaturePowerID(player);
		creatureCharges = CreaturePower.getCreaturePowerCharges(player);
		sinkingCurseLevel = Infusion.getSinkingCurseLevel(player);
		NBTTagCompound tags = Infusion.getNBT(player);
		InfusionBrewEffect brew = InfusionBrewEffect.getActiveBrew(player);
		this.brewEffect = brew.getEffectName();
		this.brewTime = 0L;
		long time = InfusionBrewEffect.getActiveBrewStartTime(tags);
		if (brew != null) {
			final long remainingTicks = brew.getDurationTicks() - (TimeUtilities.getServerTimeInTicks() - time);
			if (remainingTicks > 0L) {
				this.brewTime = (int) Math.ceil(remainingTicks / 1200.0);
			}
		}
	}

	@Override
	public void toBytes(final ByteBuf buffer) {
		buffer.writeBytes(this.infusionID.getBytes());
		buffer.writeInt(this.curEnergy);
		buffer.writeInt(this.maxEnergy);
		buffer.writeInt(this.creatureCharges);
		buffer.writeInt(this.sinkingCurseLevel);
		buffer.writeBytes(this.brewEffect.getBytes());
		buffer.writeLong(this.brewTime);
		ByteBufUtils.writeUTF8String(buffer, creatureID);
	}

	@SuppressWarnings({ "unused" })
	@Override
	public void fromBytes(final ByteBuf buffer) {
		byte[] infusionID = null;
		buffer.readBytes(infusionID);
		if (infusionID != null) {
			this.infusionID = infusionID.toString();
		}
		this.curEnergy = buffer.readInt();
		this.maxEnergy = buffer.readInt();
		this.creatureCharges = buffer.readInt();
		this.sinkingCurseLevel = buffer.readInt();
		byte[] brewEffect = null;
		buffer.readBytes(brewEffect);
		if (brewEffect != null) {
			this.brewEffect = brewEffect.toString();
		}

		this.brewTime = buffer.readLong();

		creatureID = ByteBufUtils.readUTF8String(buffer);
	}

	public static class Handler implements IMessageHandler<PlayerSyncPacket, IMessage> {
		@Override
		public IMessage onMessage(final PlayerSyncPacket message, final MessageContext ctx) {
			final EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			if (player != null && message != null) {
				Infusion.setEnergy(player, message.infusionID, message.curEnergy, message.maxEnergy);
				CreaturePower.setCreaturePowerID(player, message.creatureID, message.creatureCharges);
				Infusion.setSinkingCurseLevel(player, message.sinkingCurseLevel);
				if (message.brewEffect != null && !message.brewEffect.isEmpty()) {
					InfusionBrewEffect.setActiveBrewInfo(Infusion.getNBT(player), message.brewEffect, message.brewTime);
				}
			}
			return null;
		}
	}

}
