package nz.castorgaming.fantasyoverhaul.capabilities.playerVampire;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLCommonHandler;
import nz.castorgaming.fantasyoverhaul.capabilities.PlayerCapabilityMaster;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.IPlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityAttackBat;
import nz.castorgaming.fantasyoverhaul.util.Log;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.ChatUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;
import nz.castorgaming.fantasyoverhaul.util.classes.ShapeShift;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketPartialVampirePlayerSync;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketSelectPlayerAbility;

public class PlayerVampire extends PlayerCapabilityMaster implements IPlayerVampire {

	public enum VampirePower {
		NONE(0, 0, 0), DRINK(0, 0, 1), MESMERIZE(50, 0, 2), SPEED(10, 0, 4), BAT(50, 1, 7), ULTIMATE(50, 0, 10);

		private static int[] levels;
		static {
			VampirePower.levels = new int[] { 0, 1, 2, 2, 3, 3, 3, 4, 4, 4, 5 };
		}
		public final int INITIAL_COST;
		public final int UPKEEP_COST;
		public final int LEVEL_CAP;

		private VampirePower(final int initialCost, final int upkeepCost, final int levelCap) {
			INITIAL_COST = initialCost;
			UPKEEP_COST = upkeepCost;
			LEVEL_CAP = levelCap;
		}

		public int toInt() {
			return toInt(this);
		}

		public static int toInt(Enum<VampirePower> e) {
			return e.ordinal();
		}

		public static VampirePower fromInt(int ordinal) {
			return VampirePower.values()[ordinal];
		}

	}

	public enum VampireUltimate {
		NONE, STORM, SWARM, FARM;

		public int toInt() {
			return toInt(this);
		}

		public static int toInt(Enum<VampireUltimate> e) {
			return e.ordinal();
		}

		public static VampireUltimate fromInt(int ordinal) {
			return VampireUltimate.values()[ordinal];
		}
	}

	private static final int MAX_HUMAN_BLOOD = 500;

	public static void loadProxyData(EntityPlayer player) {
		if (player != null) {
			PlayerVampire playerEx = IPlayerVampire.get(player);
			playerEx.sync();
		}
	}

	private int level;
	private int levelCap;
	private int bloodPower;
	private final int BLOOD_RESERVE_MAX = 250;
	private int bloodReserve;
	private int ultimate;
	private int ultimateCharges;
	private VampirePower selectedPower = VampirePower.NONE;
	private int cooldown;
	private int questCounter;;

	private boolean vampVisionActive;

	private int humanBlood;

	private List<Long> visitedChunks = new ArrayList<Long>();

	private boolean resetSleep;
	private int cachedSky;
	private int highlightTicks;

	@Override
	public boolean canIncreaseVampireLevel() {
		return Config.instance().allowVampireQuests && level < levelCap;
	}

	@Override
	public void checkSleep(boolean start) {
		if (start) {
			if (isVampire() && player.isPlayerSleeping() && player.worldObj.isDaytime()) {
				resetSleep = true;
				cachedSky = player.worldObj.getSkylightSubtracted();
				player.worldObj.setSkylightSubtracted(4);
			}
		} else if (resetSleep) {
			resetSleep = false;
			player.worldObj.setSkylightSubtracted(cachedSky);
		}
	}

	@Override
	public boolean decreaseBloodPower(int quantity, boolean exact) {
		if (player.capabilities.isCreativeMode) {
			return true;
		}
		if (bloodPower >= (exact ? quantity : 1)) {
			setBloodPower(bloodPower - quantity);
			return true;
		}
		return false;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		level = nbt.getInteger("VampLevel");
		levelCap = nbt.getInteger("VampLevelCap");
		bloodPower = nbt.getInteger("BloodPower");
		bloodReserve = nbt.getInteger("BloodReserve");
		ultimate = nbt.getInteger("VampireUltimate");
		ultimateCharges = nbt.getInteger("VampUltimateCharges");
		selectedPower = VampirePower.fromInt(nbt.getInteger("VampSelectedPower"));
		cooldown = nbt.getInteger("VampCooldown");
		questCounter = nbt.getInteger("VampQuestCounter");
		vampVisionActive = nbt.getBoolean("VampVisionActive");
		humanBlood = nbt.getInteger("HumanBlood");
		resetSleep = nbt.getBoolean("ResetSleep");
		cachedSky = nbt.getInteger("CachedSky");
		highlightTicks = nbt.getInteger("HighlightTicks");
		if (nbt.hasKey("VisitedChunks")) {
			if (visitedChunks == null) {
				visitedChunks = new ArrayList<Long>();
			}
			NBTTagList list = nbt.getTagList("VisitedChunks", 4);
			for (int i = 0; i < list.tagCount(); i++) {
				long chunk = ((NBTTagLong) list.get(i)).getLong();
				visitedChunks.add(chunk);
			}
		}
	}

	@Override
	public void fillBloodReserve(int quantity) {
		bloodReserve = Math.min(bloodReserve + quantity, BLOOD_RESERVE_MAX);
		sync();
	}

	@Override
	public int getBloodPower() {
		return bloodPower;
	}

	@Override
	public int getBloodReserve() {
		return isVampire() ? bloodReserve : 0;
	}

	@Override
	public int getHumanBlood() {
		return humanBlood;
	}

	@Override
	public int getMaxAvaliablePowerOrdinal() {
		return VampirePower.levels[getVampireLevel()];
	}

	@Override
	public int getMaxBloodPower() {
		return 500
				+ ((IPlayerWerewolf.get(player).getWerewolfLevel() >= 2) ? ((int) Math.floor(getVampireLevel() * 0.5))
						: getVampireLevel()) * 250;
	}

	@Override
	public VampirePower getSelectedVampirePower() {
		return selectedPower;
	}

	@Override
	public int getVampireLevel() {
		return level;
	}

	@Override
	public int getVampireQuestCounter() {
		return questCounter;
	}

	@Override
	public VampireUltimate getVampireUltimate() {
		return VampireUltimate.values()[ultimate];
	}

	@Override
	public int getVampireUltimateCharges() {
		return ultimateCharges;
	}

	@Override
	public void giveHumanBlood(int quantity) {
		if (humanBlood < 500) {
			setHumanBlood(humanBlood + quantity);
		}
	}

	@Override
	public boolean hasVampireBook() {
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack != null && stack.getItem() == ItemInit.BOOK_VAMPIRE) {
				return stack.getItemDamage() < 9;
			}
		}
		return false;
	}

	@Override
	public void increaseBloodPower(int quantity) {
		if (bloodPower < getMaxBloodPower()) {
			setBloodPower(bloodPower + quantity);
			if (Config.instance().allowVampireQuests && getVampireLevel() == 1
					&& getBloodPower() == getMaxBloodPower()) {
				increaseVampireLevel();
			}
		}
	}

	@Override
	public void increaseBloodPower(int quantity, int maxIncrease) {
		if (bloodPower < getMaxBloodPower() && bloodPower < maxIncrease) {
			setBloodPower(Math.min(bloodPower + quantity, maxIncrease));
		}

	}

	@Override
	public void increaseVampireLevel() {
		if (level < 10) {
			setVampireLevel(level + 1);
			if (!player.worldObj.isRemote) {
				ChatUtilities.sendTranslated(TextFormatting.GOLD, player, "Your thirst grows stronger!", new Object[0]);
				SoundEffect.RANDOM_LEVELUP.playOnlyTo(player);
			}
		}

	}

	@Override
	public void increaseVampireLevelCap(int levelCap) {
		if (levelCap > this.levelCap) {
			this.levelCap = Math.max(levelCap, 3);
		}
	}

	@Override
	public void increaseVampireQuestCounter() {
		++questCounter;
		if (questCounter > 10000) {
			questCounter = 10000;
		}
	}

	@Override
	public boolean isBloodReserveReady() {
		return bloodReserve > 0;
	}

	@Override
	public boolean isVampire() {
		return level > 0;
	}

	@Override
	public boolean isVampireVisionActive() {
		return level >= 2 && vampVisionActive;
	}

	@Override
	public void resetVampireQuestCounter() {
		questCounter = 0;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setInteger("VampLevel", level);
		nbt.setInteger("VampLevelCap", levelCap);
		nbt.setInteger("BloodPower", bloodPower);
		nbt.setInteger("BloodReserve", bloodReserve);
		nbt.setInteger("VampireUltimate", ultimate);
		nbt.setInteger("VampUltimateCharges", ultimateCharges);
		nbt.setInteger("VampSelectedPower", VampirePower.toInt(selectedPower));
		nbt.setInteger("VampCooldown", cooldown);
		nbt.setInteger("VampQuestCounter", questCounter);
		nbt.setBoolean("VampVisionActive", vampVisionActive);
		nbt.setInteger("HumanBlood", humanBlood);
		nbt.setBoolean("ResetSleep", resetSleep);
		nbt.setInteger("CachedSky", cachedSky);
		nbt.setInteger("HighlightTicks", highlightTicks);

		if (visitedChunks != null && !visitedChunks.isEmpty()) {
			NBTTagList list = new NBTTagList();
			for (long chunk : visitedChunks) {
				NBTTagLong longTag = new NBTTagLong(chunk);
				list.appendTag(longTag);
			}
			nbt.setTag("VisitedChunks", list);
		}

		return nbt;
	}

	@Override
	public void setBloodPower(int bloodLevel) {
		if (bloodPower != bloodLevel) {
			bloodPower = MathHelper.clamp_int(bloodLevel, 0, getMaxBloodPower());
			sync();
		}
	}

	@Override
	public void setBloodReserve(int blood) {
		bloodReserve = blood;

	}

	@Override
	public void setHumanBlood(int blood) {
		if (humanBlood != blood) {
			humanBlood = MathHelper.clamp_int(blood, 0, MAX_HUMAN_BLOOD);
			if (player.worldObj.isRemote) {
				Reference.PACKET_HANDLER.sendToAll(new PacketPartialVampirePlayerSync(player));
			}
		}
	}

	@Override
	public void setSelectedVampirePower(VampirePower power, boolean syncToServer) {
		if (getSelectedVampirePower() != power) {
			selectedPower = power;
			highlightTicks = ((getSelectedVampirePower() != VampirePower.NONE) ? 100 : 0);
			if (syncToServer && player.worldObj.isRemote) {
				Reference.PACKET_HANDLER.sendToServer(new PacketSelectPlayerAbility(player, false));
			}
		}
	}

	@Override
	public void setVampireLevel(int level) {
		if (this.level != level && level >= 0 && level <= 10) {
			this.level = level;
			questCounter = 0;
			visitedChunks.clear();
			if (level == 0 && !player.worldObj.isRemote) {
				ShapeShift.INSTANCE.shiftTo(player, TransformCreatures.NONE);
			} else {
				ShapeShift.INSTANCE.initCurrentShift(player);
			}
			bloodPower = 0;
			humanBlood = 0;
			ultimate = 0;
			ultimateCharges = 0;
		} else {
			ShapeShift.INSTANCE.initCurrentShift(player);
		}
		selectedPower = VampirePower.NONE;
		if (level == 1) {
			bloodPower = 125;
		}
		if (level > 0) {
			humanBlood = 0;
		}
		sync();
	}

	@Override
	public void setVampireUltimate(VampireUltimate skill) {
		setVampireUltimate(skill, 5);
	}

	@Override
	public void setVampireUltimate(VampireUltimate skill, int charges) {
		ultimate = VampireUltimate.toInt(skill);
		ultimateCharges = charges;
		sync();
	}

	@Override
	public boolean storeVampireQuestChunk(int x, int z) {
		long location = x << 32 | (z & 0xFFFFFFFFL);
		if (visitedChunks.contains(location)) {
			return false;
		}
		visitedChunks.add(location);
		return true;
	}

	@Override
	public int takeHumanBlood(int quantity, EntityLivingBase attacker) {
		if (!player.isPlayerSleeping()) {
			quantity = (int) Math.ceil(0.66f * quantity);
		}

		int remainder = Math.max(humanBlood - quantity, 0);
		int taken = humanBlood - remainder;
		setHumanBlood(remainder);
		if (humanBlood < (int) Math.ceil(250.0)) {
			player.attackEntityFrom(new EntityDamageSource(DamageSource.magic.getDamageType(), attacker), 1.0f);
		} else if (!player.isPlayerSleeping()) {
			player.attackEntityFrom(new EntityDamageSource(DamageSource.magic.getDamageType(), attacker), 0.1f);
		}
		return taken;
	}

	@Override
	public void tick() {
		if (cooldown > 0) {
			cooldown--;
		}
	}

	@Override
	public void toggleVampireVision() {
		vampVisionActive = !vampVisionActive;
		if (!player.worldObj.isRemote) {
			if (!vampVisionActive) {
				player.removePotionEffect(MobEffects.NIGHT_VISION);
			}
		} else {
			player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 400, 0, true, false));
		}
	}

	@Override
	public void useBloodReserve() {
		int temp = bloodReserve;
		if (bloodPower < getMaxBloodPower()) {
			bloodReserve = 0;
			increaseBloodPower(temp);
		}

	}

	@Override
	public void triggerSelectedVampirePower() {
		if (!player.worldObj.isRemote) {
			VampirePower power = getSelectedVampirePower();
			if (cooldown <= 0) {
				cooldown = 10;
				switch (power) {
				case MESMERIZE: {
					if (player.isSneaking()) {
						toggleVampireVision();
						break;
					}
					break;
				}
				case SPEED: {
					if (ExtendedPlayer.get(player).getCreatureType() == TransformCreatures.NONE) {
						PotionEffect effect = player.getActivePotionEffect(MobEffects.SPEED);
						int currentLevel = (int) ((effect == null) ? 0
								: Math.ceil(Math.log(effect.getAmplifier() + 1) / Math.log(2.0)));
						if (level >= 4 && currentLevel <= Math.ceil((level - 3) / 2.0f)) {
							if (decreaseBloodPower(power.INITIAL_COST, true)) {
								SoundEffect.RANDOM_FIZZ.playOnlyTo(player);
								int level = (effect == null) ? 2 : ((effect.getAmplifier() + 1) * 2);
								int duration = (effect == null) ? TimeUtilities.secsToTicks(10)
										: (effect.getDuration() + 60);
								player.addPotionEffect(
										new PotionEffect(MobEffects.SPEED, duration, level - 1, true, false));
								player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration,
										currentLevel + 1, true, false));
							} else {
								SoundEffect.NOTE_SNARE.playOnlyTo(player);
							}
						} else {
							SoundEffect.NOTE_SNARE.playOnlyTo(player);
						}
						break;
					}
					SoundEffect.NOTE_SNARE.playOnlyTo(player);
					break;
				}
				case BAT: {
					if (level < 7) {
						SoundEffect.NOTE_SNARE.playOnlyTo(player);
						break;
					}
					if (ExtendedPlayer.get(player).getCreatureType() == TransformCreatures.NONE) {
						if (decreaseBloodPower(power.INITIAL_COST, true)) {
							SoundEffect.RANDOM_FIZZ.playOnlyTo(player);
							ShapeShift.INSTANCE.shiftTo(player, TransformCreatures.BAT);
							break;
						}
						SoundEffect.NOTE_SNARE.playOnlyTo(player);
						break;
					} else {
						if (ExtendedPlayer.get(player).getCreatureType() == TransformCreatures.BAT) {
							SoundEffect.RANDOM_FIZZ.playOnlyTo(player);
							ShapeShift.INSTANCE.shiftTo(player, TransformCreatures.NONE);
							break;
						}
						SoundEffect.NOTE_SNARE.playOnlyTo(player);
						break;
					}
				}
				case ULTIMATE: {
					if (level >= 10 && ultimateCharges > 0
							&& ExtendedPlayer.get(player).getCreatureType() == TransformCreatures.NONE) {
						switch (getVampireUltimate()) {
						case FARM:
							boolean done = false;
							if (player.dimension != Config.instance().dimensionDreamID) {
								BlockPos coords = player.getBedLocation(player.dimension);
								int dimension = player.dimension;
								World world = player.worldObj;
								if (coords == null) {
									coords = player.getBedLocation(0);
									dimension = 0;
									if (!world.isRemote) {
										world = world.getMinecraftServer().worldServerForDimension(0);
									} else {
										world = FMLCommonHandler.instance().getMinecraftServerInstance()
												.worldServerForDimension(0);
									}

									if (coords == null) {
										coords = world.getSpawnPoint();
										while (world.getBlockState(coords).isNormalCube() && coords.getY() < 255) {
											coords = new BlockPos(coords.getX(), coords.getY() + 1, coords.getZ());
										}
									}

								}
								if (coords != null) {
									coords = Blocks.BED.getBedSpawnPosition(null, world, coords, null);
									if (coords != null) {
										if (dimension == player.dimension && player.getDistanceSq(coords.getX(),
												player.posY, coords.getZ()) <= 36.0) {
											Village village = world.villageCollectionObj
													.getNearestVillage(player.getPosition(), 512);
											if (village != null) {
												BlockPos townPos = village.getCenter();
												if (EntityUtil.teleportToLocationSafely(player.worldObj,
														townPos.getX() + 0.5, townPos.getY() + 1.0,
														townPos.getZ() + 0.5, dimension, player, true)) {
													done = true;
												}
											}
										} else {
											if (EntityUtil.teleportToLocationSafely(player.worldObj,
													coords.getX() + 0.5, coords.getY() + 1.0, coords.getZ() + 0.5,
													dimension, player, true)) {
												done = true;
											}
										}
									}
								}
							}
							if (!done) {
								SoundEffect.NOTE_SNARE.playOnlyTo(player);
								break;
							}
							break;
						case STORM: {
							WorldInfo worldinfo = ((WorldServer) player.worldObj).getWorldInfo();
							if (!worldinfo.isRaining()) {
								int i = (300 + player.worldObj.rand.nextInt(600)) * 20;
								worldinfo.setThunderTime(i);
								worldinfo.setThundering(true);
								worldinfo.setRaining(true);
								worldinfo.setRainTime(i);
								SoundEffect.RANDOM_FIZZ.playOnlyTo(player);
								if (!player.capabilities.isCreativeMode) {
									--ultimateCharges;
									sync();
								}
								break;
							}
							SoundEffect.NOTE_SNARE.playOnlyTo(player);
							break;
						}
						case SWARM: {
							for (int i = 0; i < 15; i++) {
								EntityLiving creature = spawnCreature(player.worldObj, EntityAttackBat.class,
										player.posX, player.posY + 3 + player.worldObj.rand.nextDouble(), player.posZ,
										1, 4, ParticleEffect.SMOKE, SoundEffect.RANDOM_POOF);
								if (creature != null) {
									EntityAttackBat bat = (EntityAttackBat) creature;
									bat.setOwner(player);
									bat.setIsBatHanging(false);
									NBTTagCompound nbtBat = bat.getEntityData();
									nbtBat.setBoolean(Reference.NO_DROPS, true);
								}

							}
							if (!player.capabilities.isCreativeMode) {
								--ultimateCharges;
								sync();
								break;
							}
							break;
						}
						default:
							break;
						}
						break;
					}
					SoundEffect.NOTE_SNARE.playOnlyTo(player);
					break;
				}
				default:
					break;
				}
			} else {
				SoundEffect.NOTE_SNARE.playOnlyTo(player);
			}
		}
	}

	public static EntityLiving spawnCreature(final World world, final Class<? extends EntityLiving> creatureType,
			final double posX, final double posY, final double posZ, final int minRange, final int maxRange,
			final ParticleEffect effect, final SoundEffect effectSound) {
		if (!world.isRemote) {
			final int x = MathHelper.floor_double(posX);
			final int y = MathHelper.floor_double(posY);
			final int z = MathHelper.floor_double(posZ);
			final int activeRadius = maxRange - minRange;
			int ax = world.rand.nextInt(activeRadius * 2 + 1);
			if (ax > activeRadius) {
				ax += minRange * 2;
			}
			final int nx = x - maxRange + ax;
			int az = world.rand.nextInt(activeRadius * 2 + 1);
			if (az > activeRadius) {
				az += minRange * 2;
			}
			int nz;
			int ny;
			for (nz = z - maxRange + az, ny = y; !world.isAirBlock(new BlockPos(nx, ny, nz)) && ny < y + 8; ++ny) {
			}
			while (world.isAirBlock(new BlockPos(nx, ny, nz)) && ny > 0) {
				--ny;
			}
			int hy;
			for (hy = 0; world.isAirBlock(new BlockPos(nx, ny + hy + 1, nz)) && hy < 6; ++hy) {
			}
			Log.instance().debug("Creature: hy: " + hy + " (" + nx + "," + ny + "," + nz + ")");
			if (hy >= 2) {
				try {
					final Constructor<? extends EntityLiving> ctor = creatureType.getConstructor(World.class);
					final EntityLiving creature = ctor.newInstance(world);
					creature.setLocationAndAngles(0.5 + nx, 0.05 + ny + 1.0, 0.5 + nz, 0.0f, 0.0f);
					world.spawnEntityInWorld(creature);
					if (effect != null) {
						effect.send(effectSound, world, 0.5 + nx, 0.05 + ny + 1.0, 0.5 + nz, 1.0, creature.height, 16);
					}
					return creature;
				} catch (NoSuchMethodException ex) {
				} catch (InvocationTargetException ex2) {
				} catch (InstantiationException ex3) {
				} catch (IllegalAccessException ex4) {
				}
			}
		}
		return null;
	}

}
