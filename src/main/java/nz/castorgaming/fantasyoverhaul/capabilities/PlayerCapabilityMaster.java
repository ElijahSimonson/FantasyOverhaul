package nz.castorgaming.fantasyoverhaul.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketExtendedPlayerSync;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketPlayerStyle;

public class PlayerCapabilityMaster {

	protected EntityPlayer player;

	private boolean getPlayerData = true;

	public EntityPlayer getPlayer() {
		return player;
	}

	public void processSync() {
		if (getPlayerData) {
			getPlayerData = false;
			for (EntityPlayer otherPlayer : player.worldObj.playerEntities) {
				if (otherPlayer != player) {
					Reference.PACKET_HANDLER.sendTo(new PacketPlayerStyle(otherPlayer), player);
				}
			}
		}
	}

	public void scheduleSync() {
		getPlayerData = true;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	public void sync() {
		if (!player.worldObj.isRemote) {
			Reference.PACKET_HANDLER.sendTo(new PacketExtendedPlayerSync(player), player);
		}
	}

	public enum QuestState {
		NOT_STARTED, STARTED, COMPLETE;
		
		public int toInt() {
			return toInt(this);
		}

		public static int toInt(Enum<QuestState> e) {
			return e.ordinal();
		}

		public static QuestState fromInt(int ordinal) {
			return QuestState.values()[ordinal];
		}
	}
}
