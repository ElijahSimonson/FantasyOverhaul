package nz.castorgaming.fantasyoverhaul.capabilities.playerVampire;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire.VampirePower;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire.VampireUltimate;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public interface IPlayerVampire extends INBTSerializable<NBTTagCompound> {

	public void setPlayer(EntityPlayer player);

	public EntityPlayer getPlayer();

	public static PlayerVampire get(EntityPlayer player) {
		return (PlayerVampire) player.getCapability(CapabilityInit.PLAYER_VAMPIRE, null);
	}

	public static void loadProxyData(EntityPlayer player) {
	}

	public static EntityLiving spawnCreature(World world, Class<? extends EntityLiving> creatureType, BlockPos pos,
			int minRange, int maxRange, ParticleEffect effect, SoundEffect sound) {
		return null;
	}

	public boolean canIncreaseVampireLevel();

	public void checkSleep(boolean start);

	public boolean decreaseBloodPower(int quantity, boolean exact);

	public void fillBloodReserve(int quantity);

	public int getBloodPower();

	public int getBloodReserve();

	public int getHumanBlood();

	public int getMaxAvaliablePowerOrdinal();

	public int getMaxBloodPower();

	public VampirePower getSelectedVampirePower();

	public int getVampireLevel();

	public int getVampireQuestCounter();

	public VampireUltimate getVampireUltimate();

	public int getVampireUltimateCharges();

	public void giveHumanBlood(int quantity);

	public boolean hasVampireBook();

	public void increaseBloodPower(int quantity);

	public void increaseBloodPower(int quantity, int maxIncrease);

	public void increaseVampireLevel();

	public void increaseVampireLevelCap(int levelCap);

	public void increaseVampireQuestCounter();

	public boolean isBloodReserveReady();

	public boolean isVampire();

	public boolean isVampireVisionActive();

	public void resetVampireQuestCounter();

	public void setBloodPower(int bloodLevel);

	public void setBloodReserve(int blood);

	public void setHumanBlood(int blood);

	public void setSelectedVampirePower(VampirePower power, boolean syncToServer);

	public void setVampireLevel(int level);

	public void setVampireUltimate(VampireUltimate skill);

	public void setVampireUltimate(VampireUltimate skill, int charges);

	public boolean storeVampireQuestChunk(int x, int z);

	public int takeHumanBlood(int quantity, EntityLivingBase attacker);

	public void tick();

	public void toggleVampireVision();

	public void triggerSelectedVampirePower();

	public void useBloodReserve();
}
