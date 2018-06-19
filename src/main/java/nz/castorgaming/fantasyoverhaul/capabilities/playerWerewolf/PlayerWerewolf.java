package nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import nz.castorgaming.fantasyoverhaul.capabilities.PlayerCapabilityMaster;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.util.classes.ShapeShift;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;

public class PlayerWerewolf extends PlayerCapabilityMaster implements IPlayerWerewolf {

	private int werewolfLevel;
	private QuestState wolfmanQuestState;
	private int wolfmanQuestCounter;
	private List<Long> visitedWerewolfChunks;
	private long lastHowl;
	private long lastBoneFind;

	public PlayerWerewolf() {
		visitedWerewolfChunks = new ArrayList<Long>();
	}

	@Override
	public long getLastBoneFind() {
		return lastBoneFind;
	}

	@Override
	public long getLastHowl() {
		return lastHowl;
	}

	@Override
	public int getWerewolfLevel() {
		return werewolfLevel;
	}

	@Override
	public int getWolfmanQuestCounter() {
		return wolfmanQuestCounter;
	}

	@Override
	public QuestState getWolfmanQuestState() {
		return wolfmanQuestState;
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
		++wolfmanQuestCounter;
		if (wolfmanQuestCounter > 100) {
			wolfmanQuestCounter = 100 ;
		}
		
	}

	@Override
	public void setLastBoneFind(long serverTime) {
		lastBoneFind = serverTime;
	}

	@Override
	public void setLastHowl(long serverTime) {
		lastHowl = serverTime;
	}

	@Override
	public void setWerewolfLevel(int level) {
		if (werewolfLevel != level && level >= 0 && level <= 10) {
			werewolfLevel = level;
			wolfmanQuestState = QuestState.NOT_STARTED;
			wolfmanQuestCounter = 0;
			visitedWerewolfChunks.clear();
			TransformCreatures creatureType = ExtendedPlayer.get(player).getCreatureType();
			if (werewolfLevel == 0 && !player.worldObj.isRemote && (creatureType == TransformCreatures.WOLF || creatureType == TransformCreatures.WOLFMAN)) {
				ShapeShift.INSTANCE.shiftTo(player, TransformCreatures.NONE);
			}
			sync();
		}
	}

	@Override
	public void setWolfmanQuestState(QuestState state) {
		wolfmanQuestState = state;
	}

	@Override
	public boolean storeWolfmanQuestChunk(int x, int z) {
		long location = x << 32 | (z & 0xFFFFFFFFL);
		if (visitedWerewolfChunks.contains(location)) {
			return false;
		}
		visitedWerewolfChunks.add(location);
		return true;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		
		nbt.setInteger("WolfLevel", werewolfLevel);
		nbt.setInteger("WolfQuest", wolfmanQuestState.toInt());
		nbt.setInteger("WolfQuestCount", wolfmanQuestCounter);
		
		if (visitedWerewolfChunks != null && !visitedWerewolfChunks.isEmpty()) {
			NBTTagList visitedChunks = new NBTTagList();
			for (long chunk : visitedWerewolfChunks) {
				NBTTagLong chunkTag = new NBTTagLong(chunk);
				visitedChunks.appendTag(chunkTag);
			}
			nbt.setTag("WolfVisited", visitedChunks);
		}
		nbt.setLong("WolfLastHowl", lastHowl);
		nbt.setLong("WolfLastBone", lastBoneFind);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		werewolfLevel = nbt.getInteger("WolfLevel");
		wolfmanQuestCounter = nbt.getInteger("WolfQuestCount");
		wolfmanQuestState = QuestState.fromInt(nbt.getInteger("WolfQuestStae"));
		lastHowl = nbt.getLong("WolfLastHowl");
		lastBoneFind = nbt.getLong("WolfLastBone");
		
		if (nbt.hasKey("WolfVisited")) {
			if (visitedWerewolfChunks == null) {
				visitedWerewolfChunks = new ArrayList<Long>();
			}
			NBTTagList list = nbt.getTagList("WolfVisited", 4);
			for (int i = 0; i < list.tagCount(); i++) {
				long chunk = ((NBTTagLong) list.get(i)).getLong();
				visitedWerewolfChunks.add(chunk);
			}
		}
	}

}
