package nz.castorgaming.fantasyoverhaul.util.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire.VampirePower;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire.VampireUltimate;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.IPlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.PlayerWerewolf;

public class PacketExtendedPlayerSync implements IMessage {

	private int werewolfLevel, vampireLevel, bloodLevel, ultimate, creatureOrdinal, selected, ultimateCharges, reserveBlood;

	public PacketExtendedPlayerSync() {
	}

	public PacketExtendedPlayerSync(EntityPlayer player) {
		ExtendedPlayer playerEx = IExtendPlayer.get(player);
		PlayerVampire vampire = IPlayerVampire.get(player);
		PlayerWerewolf werewolf = IPlayerWerewolf.get(player);
		werewolfLevel = werewolf.getWerewolfLevel();
		creatureOrdinal = playerEx.getCreatureType().toInt();
		vampireLevel = vampire.getVampireLevel();
		bloodLevel = vampire.getBloodPower();
		selected = vampire.getSelectedVampirePower().toInt();
		ultimate = vampire.getVampireUltimate().toInt();
		ultimateCharges = vampire.getVampireUltimateCharges();
		reserveBlood = vampire.getBloodReserve();
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		werewolfLevel = buffer.readInt();
		creatureOrdinal = buffer.readInt();
		vampireLevel = buffer.readInt();
		bloodLevel = buffer.readInt();
		selected = buffer.readInt();
		ultimate = buffer.readInt();
		ultimateCharges = buffer.readInt();
		reserveBlood = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(this.werewolfLevel);
		buffer.writeInt(this.creatureOrdinal);
		buffer.writeInt(this.vampireLevel);
		buffer.writeInt(this.bloodLevel);
		buffer.writeInt(this.selected);
		buffer.writeInt(this.ultimate);
		buffer.writeInt(this.ultimateCharges);
		buffer.writeInt(this.reserveBlood);
	}

	public static class Handler implements IMessageHandler<PacketExtendedPlayerSync, IMessage> {

		@Override
		public IMessage onMessage(PacketExtendedPlayerSync message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketExtendedPlayerSync message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			ExtendedPlayer extend = IExtendPlayer.get(player);
			PlayerVampire vampire = IPlayerVampire.get(player);
			PlayerWerewolf werewolf = IPlayerWerewolf.get(player);

			werewolf.setWerewolfLevel(message.werewolfLevel);

			extend.setCreatureTypeOrdinal(message.creatureOrdinal);

			vampire.setVampireLevel(message.vampireLevel);
			vampire.setBloodPower(message.bloodLevel);
			vampire.setSelectedVampirePower(VampirePower.fromInt(message.selected), false);
			vampire.setVampireUltimate(VampireUltimate.fromInt(message.ultimate), message.ultimateCharges);
			vampire.setBloodReserve(message.reserveBlood);
		}

	}

}
