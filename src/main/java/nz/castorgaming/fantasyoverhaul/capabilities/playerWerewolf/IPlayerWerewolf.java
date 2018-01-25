package nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf;

import net.minecraft.entity.player.EntityPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.PlayerCapabilityMaster.QuestState;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;

public interface IPlayerWerewolf {
	public static PlayerWerewolf get(EntityPlayer player) {
		return (PlayerWerewolf) player.getCapability(CapabilityInit.PLAYER_WEREWOLF, null);
	}

	public static void loadProxyData(EntityPlayer player) {
	}

	public void setPlayer(EntityPlayer player);

	public long getLastBoneFind();

	public long getLastHowl();

	public int getWerewolfLevel();

	public int getWolfmanQuestCounter();

	public QuestState getWolfmanQuestState();

	public void increaseWerewolfLevel();

	public void increaseWolfmanQuestCounter();

	public void setLastBoneFind(long serverTime);

	public void setLastHowl(long serverTime);

	public void setWerewolfLevel(int level);

	public void setWolfmanQuestState(QuestState state);

	public boolean storeWolfmanQuestChunk(int x, int z);

}
