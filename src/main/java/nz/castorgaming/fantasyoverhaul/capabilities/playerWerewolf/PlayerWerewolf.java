package nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf;

import java.util.ArrayList;
import java.util.List;

import nz.castorgaming.fantasyoverhaul.capabilities.PlayerCapabilityMaster;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.util.classes.ShapeShift;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;

public class PlayerWerewolf extends PlayerCapabilityMaster implements IPlayerWerewolf {

	private int werewolfLevel;
	private int wolfmanQuestState;
	private int wolfmanQuestCounter;

	private final List<Long> visitedWerewolfChunks;

	public PlayerWerewolf() {
		visitedWerewolfChunks = new ArrayList<Long>();
	}

	@Override
	public long getLastBoneFind() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLastHowl() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWerewolfLevel() {
		return werewolfLevel;
	}

	@Override
	public int getWolfmanQuestCounter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public QuestState getWolfmanQuestState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void increaseWerewolfLevel() {
		if (werewolfLevel < 10) {
			setWerewolfLevel(werewolfLevel + 1);
			ShapeShift.INSTANCE.initCurrentShift(player);
		}
	}

	@Override
	public void increaseWolfmanQuestCounter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLastBoneFind(long serverTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLastHowl(long serverTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWerewolfLevel(int level) {
		if (werewolfLevel != level && level >= 0 && level <= 10) {
			werewolfLevel = level;
			wolfmanQuestState = 0;
			wolfmanQuestCounter = 0;
			visitedWerewolfChunks.clear();
			TransformCreatures creatureType = IExtendPlayer.get(player).getCreatureType();
			if (werewolfLevel == 0 && !player.worldObj.isRemote && (creatureType == TransformCreatures.WOLF || creatureType == TransformCreatures.WOLFMAN)) {
				ShapeShift.INSTANCE.shiftTo(player, TransformCreatures.NONE);
			}
			sync();
		}
	}

	@Override
	public void setWolfmanQuestState(QuestState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean storeWolfmanQuestChunk(int x, int z) {
		// TODO Auto-generated method stub
		return false;
	}

}
