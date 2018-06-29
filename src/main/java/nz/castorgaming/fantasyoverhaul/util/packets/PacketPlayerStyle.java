package nz.castorgaming.fantasyoverhaul.util.packets;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.objects.worlds.WorldProviderDreamWorld;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;

public class PacketPlayerStyle implements IMessage {

	private String username;
	private int grotesqueTicks;
	private int nightmare;
	private boolean ghost;
	private int creatureType;
	private int blood;
	private String playerSkin;

	public PacketPlayerStyle() {
	}

	public PacketPlayerStyle(final EntityPlayer player) {
		final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
		this.username = player.getCommandSenderEntity().getUniqueID().toString();
		this.grotesqueTicks = (nbtPlayer.hasKey("witcheryGrotesque") ? nbtPlayer.getInteger("witcheryGrotesque") : 0);
		this.nightmare = WorldProviderDreamWorld.getPlayerHasNightmare(nbtPlayer);
		this.ghost = WorldProviderDreamWorld.isPlayerGhost(nbtPlayer);
		final ExtendedPlayer playerEx = IExtendPlayer.get(player);
		this.creatureType = playerEx.getCreatureType().toInt();
		this.blood = IPlayerVampire.get(player).getHumanBlood();
		this.playerSkin = playerEx.getOtherPlayerSkin().toString();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, username);
		buf.writeInt(grotesqueTicks);
		buf.writeInt(nightmare);
		buf.writeBoolean(ghost);
		buf.writeInt(creatureType);
		buf.writeInt(blood);
		ByteBufUtils.writeUTF8String(buf, playerSkin);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		username = ByteBufUtils.readUTF8String(buf);
		grotesqueTicks = buf.readInt();
		nightmare = buf.readInt();
		ghost = buf.readBoolean();
		creatureType = buf.readInt();
		blood = buf.readInt();
		playerSkin = ByteBufUtils.readUTF8String(buf);

	}

	public static class Handler implements IMessageHandler<PacketPlayerStyle, IMessage> {

		@Override
		public IMessage onMessage(PacketPlayerStyle message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketPlayerStyle message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			EntityPlayer otherPlayer = player.worldObj.getPlayerEntityByUUID(UUID.fromString(message.username));
			if (otherPlayer != null) {
				final NBTTagCompound nbtOtherPlayer = Infusion.getNBT(otherPlayer);
				if (message.grotesqueTicks > 0) {
					nbtOtherPlayer.setInteger(Reference.INFUSION_GROTESQUE, message.grotesqueTicks);
				} else if (nbtOtherPlayer.hasKey(Reference.INFUSION_GROTESQUE)) {
					nbtOtherPlayer.removeTag(Reference.INFUSION_GROTESQUE);
				}
				WorldProviderDreamWorld.setPlayerHasNightmare(otherPlayer, message.nightmare > 0,
						message.nightmare > 1);
				WorldProviderDreamWorld.playerIsGhost(nbtOtherPlayer, message.ghost);
				final ExtendedPlayer playerEx = IExtendPlayer.get(otherPlayer);
				playerEx.setCreatureTypeOrdinal(message.creatureType);
				PlayerVampire vamp = IPlayerVampire.get(otherPlayer);
				vamp.setHumanBlood(message.blood);
				playerEx.setOtherPlayerSkin(UUID.fromString(message.playerSkin));
			}

		}
	}
}
