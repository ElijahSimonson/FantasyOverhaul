package nz.castorgaming.fantasyoverhaul.powers.infusions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityNightmare;
import nz.castorgaming.fantasyoverhaul.powers.infusions.creature.CreaturePower;
import nz.castorgaming.fantasyoverhaul.util.Log;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.ChatUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.packets.PlayerSyncPacket;

public class Infusion extends IForgeRegistryEntry.Impl<Infusion> {

	@SuppressWarnings("unchecked")
	public static final RegistryNamespacedDefaultedByKey<ResourceLocation, Infusion> REGISTRY = (RegistryNamespacedDefaultedByKey<ResourceLocation, Infusion>) GameRegistry
			.findRegistry(Infusion.class);

	private String infusionName;
	protected static final int DEFAULT_CHARGE_COST = 1;

	public static EntityItem dropEntityItemWithRandomChoice(EntityLivingBase entity, ItemStack stack, boolean par2) {
		if (stack == null || entity == null) {
			return null;
		}

		if (stack.stackSize == 0) {
			return null;
		}

		EntityItem entityItem = new EntityItem(entity.worldObj);
		entityItem.setEntityItemStack(stack);
		entityItem.setPosition(entity.posX, entity.posY - 0.30000001192092896 + entity.getEyeHeight(), entity.posZ);

		entityItem.setPickupDelay(40);

		float f = 0.1f;

		if (par2) {
			float f2 = entity.worldObj.rand.nextFloat() * 0.5f;
			float f3 = entity.worldObj.rand.nextFloat() * 3.1415927f * 2.0f;
			entityItem.motionX = -MathHelper.sin(f3) * f2;
			entityItem.motionZ = MathHelper.cos(f3) * f2;
			entityItem.motionY = 0.20000000298023224;
		} else {
			f = 0.3f;
			entityItem.motionX = -MathHelper.sin(entity.rotationYaw / 180.0f * 3.1415927f)
					* MathHelper.cos(entity.rotationPitch / 180.0f * 3.1415927f) * f;
			entityItem.motionZ = MathHelper.cos(entity.rotationYaw / 180.0f * 3.1415927f)
					* MathHelper.cos(entity.rotationPitch / 180.0f * 3.1415927f) * f;
			entityItem.motionY = -MathHelper.sin(entity.rotationPitch / 180.0f * 3.1415927f) * f + 0.1f;
			f = 0.02f;
			final float f2 = entity.worldObj.rand.nextFloat() * 3.1415927f * 2.0f;
			f *= entity.worldObj.rand.nextFloat();
			final EntityItem entityItem1 = entityItem;
			entityItem1.motionX += Math.cos(f2) * f;
			final EntityItem entityItem2 = entityItem;
			entityItem2.motionY += (entity.worldObj.rand.nextFloat() - entity.worldObj.rand.nextFloat()) * 0.1f;
			final EntityItem entityItem3 = entityItem;
			entityItem3.motionZ += Math.sin(f2) * f;
		}
		entity.worldObj.spawnEntityInWorld(entityItem);
		return entityItem;
	}

	public static EntityCreature spawnCreature(World world, Class<? extends EntityCreature> creatureType,
			EntityLivingBase victim, int minRange, int maxRange, ParticleEffect effect, SoundEffect sound) {
		int x = MathHelper.floor_double(victim.posX);
		int y = MathHelper.floor_double(victim.posY);
		int z = MathHelper.floor_double(victim.posZ);
		return spawnCreature(world, creatureType, x, y, z, victim, minRange, maxRange, effect, sound);
	}

	public static EntityCreature spawnCreature(World world, Class<? extends EntityCreature> creatureType, int x, int y,
			int z, EntityLivingBase victim, int minRange, int maxRange) {
		return spawnCreature(world, creatureType, x, y, z, victim, minRange, maxRange, null, SoundEffect.NONE);
	}

	public static EntityCreature spawnCreature(World world, Class<? extends EntityCreature> creatureType, int x, int y,
			int z, EntityLivingBase victim, int minRange, int maxRange, ParticleEffect effect, SoundEffect sound) {
		if (!world.isRemote) {
			int activeRadius = maxRange - minRange;
			int ax = world.rand.nextInt(activeRadius * 2 + 1);
			if (ax > activeRadius) {
				ax += minRange * 2;
			}

			int nx = x - maxRange + ax;
			int az = world.rand.nextInt(activeRadius * 2 + 1);
			if (az > activeRadius) {
				az += minRange * 2;
			}

			int nz, ny;
			for (nz = z - maxRange + az, ny = y; !world.isAirBlock(new BlockPos(nx, ny, nz)) && ny < y + 8; ny++) {
			}
			while (world.isAirBlock(new BlockPos(nx, ny, nz)) && ny > 0) {
				ny--;
			}
			int hy;
			for (hy = 0; world.isAirBlock(new BlockPos(nx, ny + hy + 1, nz)) && hy < 6; hy++) {
			}
			Log.instance().debug("Creature: hy: " + hy + " (" + nx + ", " + nz + ")");
			if (ny >= 2) {
				try {
					Constructor<? extends EntityCreature> ctor = creatureType.getConstructor(World.class);
					EntityCreature creature = ctor.newInstance(world);
					if (victim instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) victim;
						if (creature instanceof EntityIllusion) {
							((EntityIllusion) creature).setVictim(player.getUniqueID());
						} else if (creature instanceof EntityNightmare) {
							((EntityNightmare) creature).setVictim(player.getUniqueID());
							creature.setAttackTarget(victim);
						}
					}
					creature.setLocationAndAngles(0.5 + nx, 0.05 + ny + 1.0, 0.5 + nz, 0.0f, 0.0f);
					world.spawnEntityInWorld(creature);
					if (effect != null) {
						effect.send(sound, world, 0.5 + nx, 0.05 + ny + 1.0, 0.5 + nz, 1.0, creature.height, 16);
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

	public static boolean isOnCooldown(World world, ItemStack stack) {
		if (!world.isRemote) {
			NBTTagCompound nbtTag = stack.getTagCompound();
			if (nbtTag != null && nbtTag.hasKey(Reference.COOLDOWN)) {
				long currentTime = MinecraftServer.getCurrentTimeMillis();
				if (currentTime < nbtTag.getLong(Reference.COOLDOWN)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void setCooldown(World world, ItemStack stack, int milliseconds) {
		if (!world.isRemote) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound tags = stack.getTagCompound();
			if (tags != null) {
				long currentTime = MinecraftServer.getCurrentTimeMillis();
				tags.setLong(Reference.COOLDOWN, currentTime + milliseconds);
			}
		}
	}

	public Infusion(String name) {
		setInfusionName(name);
		setRegistryName("infusion_" + name);
		InitArrays.INFUSIONS.add(this);
	}

	private void setInfusionName(String name) {
		infusionName = name;
	}

	public void onHurt(World world, EntityPlayer player, LivingHurtEvent event) {
	}

	public void onFalling(World world, EntityPlayer player, LivingFallEvent event) {
	}

	public ResourceLocation getPowerBarIcon(EntityPlayer player, int index) {
		return new ResourceLocation("planks");
	}

	protected boolean consumeCharges(World world, EntityPlayer player, int cost, boolean playFailSound) {
		if (player.capabilities.isCreativeMode) {
			return true;
		}
		int charges = getCurrentPower(player);
		if (charges - cost < 0) {
			world.playSound(player, player.getPosition(), SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.PLAYERS, 0.5f,
					0.4f / ((float) Math.random() - 0.4f + 0.8f));
			clearInfusion(player);
			return false;
		}

		setCurrentEnergy(player, charges - cost);
		return true;
	}

	public void onUpdate(ItemStack stack, World world, EntityPlayer player, int par4, boolean par5) {
	}

	public void onLeftClickEntity(ItemStack stack, World world, EntityPlayer player, Entity otherEntity) {
		if (!world.isRemote) {
			world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.PLAYERS, 0.5f,
					0.4f / ((float) Math.random() * 0.4f + 0.8f));
		}
	}

	public int getMaxItemUseDuration(ItemStack stack) {
		return 400;
	}

	public void onUsingItemTick(ItemStack stack, World world, EntityPlayer player, int countdown) {
	}

	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int countdown) {
		if (!world.isRemote) {
			world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.PLAYERS, 0.5f,
					0.4f / ((float) Math.random() * 0.4f));
		}
	}

	public void playSound(World world, EntityPlayer player, SoundEvent sound) {
		world.playSound(null, player.getPosition(), sound, SoundCategory.PLAYERS, 0.5f,
				0.4f / ((float) world.rand.nextDouble() * 0.4f + 0.8f));
	}

	public void playFailSound(World world, EntityPlayer player) {
		playSound(world, player, SoundEvents.BLOCK_NOTE_SNARE);
	}

	public static NBTTagCompound getNBT(Entity player) {
		NBTTagCompound entityData = player.getEntityData();
		if (player.worldObj.isRemote) {
			return entityData;
		}
		NBTTagCompound persistedData = entityData.getCompoundTag(Reference.PLAYER_PERSISTED);
		if (!entityData.hasKey(Reference.PLAYER_PERSISTED)) {
			entityData.setTag(Reference.PLAYER_PERSISTED, persistedData);
		}
		return persistedData;
	}

	public void infuse(EntityPlayer player, int charges) {
		if (!player.worldObj.isRemote) {
			NBTTagCompound nbtTags = getNBT(player);
			nbtTags.setString(Reference.INFUSION_ID_KEY, getInfusionName());
			nbtTags.setInteger(Reference.INFUSION_CHARGES_KEY, charges);
			nbtTags.setInteger(Reference.MAX_CHARGES_KEY, charges);
			CreaturePower.setCreaturePowerID(player, getInfusionName(), 0);
			syncPlayer(player.worldObj, player);
		}
	}

	private void clearInfusion(EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			NBTTagCompound nbtTags = getNBT(player);
			nbtTags.removeTag(Reference.INFUSION_CHARGES_KEY);
			syncPlayer(player.worldObj, player);
		}
	}

	public static void setCurrentEnergy(EntityPlayer player, int currentEnergy) {
		if (!player.worldObj.isRemote) {
			NBTTagCompound nbtTags = getNBT(player);
			nbtTags.setInteger(Reference.INFUSION_CHARGES_KEY, currentEnergy);
			syncPlayer(player.worldObj, player);
		}
	}

	public static void syncPlayer(World world, EntityPlayer player) {
		if (!world.isRemote) {
			Reference.PACKET_HANDLER.sendTo(new PlayerSyncPacket(player), player);
		}
	}

	public static String getInfusionID(EntityPlayer player) {
		NBTTagCompound nbtTags = getNBT(player);
		return nbtTags.hasKey(Reference.INFUSION_ID_KEY) ? nbtTags.getString(Reference.INFUSION_ID_KEY) : "defused";
	}

	public static int getCurrentPower(EntityPlayer player) {
		NBTTagCompound nbtTags = getNBT(player);
		return nbtTags.hasKey(Reference.INFUSION_CHARGES_KEY) ? nbtTags.getInteger(Reference.INFUSION_CHARGES_KEY) : 0;
	}

	public static int getMaxEnergy(EntityPlayer player) {
		NBTTagCompound nbtTags = getNBT(player);
		return nbtTags.hasKey(Reference.MAX_CHARGES_KEY) ? nbtTags.getInteger(Reference.MAX_CHARGES_KEY) : 0;
	}

	public static void setEnergy(EntityPlayer player, String infusionID, int currentEnergy, int maxEnergy) {
		if (player.worldObj.isRemote) {
			NBTTagCompound nbtTags = getNBT(player);
			nbtTags.setString(Reference.INFUSION_ID_KEY, infusionID);
			nbtTags.setInteger(Reference.INFUSION_CHARGES_KEY, currentEnergy);
			nbtTags.setInteger(Reference.MAX_CHARGES_KEY, maxEnergy);
		}
	}

	public static void setSinkingCurseLevel(EntityPlayer playerEntity, int sinkingLevel) {
		if (playerEntity.worldObj.isRemote) {
			NBTTagCompound nbtTags = getNBT(playerEntity);
			if (nbtTags.hasKey(Reference.INFUSION_SINKING)) {
				nbtTags.removeTag(Reference.INFUSION_SINKING);
			}
			nbtTags.setInteger(Reference.INFUSION_SINKING, sinkingLevel);
		}
	}

	public static int getSinkingCurseLevel(EntityPlayer player) {
		NBTTagCompound tags = getNBT(player);
		return tags.hasKey(Reference.INFUSION_SINKING) ? tags.getInteger(Reference.INFUSION_SINKING) : 0;
	}

	public static boolean aquireEnergy(World world, EntityPlayer player, int cost, boolean showMessages) {
		NBTTagCompound nbtPlayer = getNBT(player);
		return nbtPlayer != null && aquireEnergy(world, player, nbtPlayer, cost, showMessages);
	}

	public static boolean aquireEnergy(World world, EntityPlayer player, NBTTagCompound nbtPlayer, int cost,
			boolean showMessages) {
		if (nbtPlayer == null || !nbtPlayer.hasKey(Reference.INFUSION_ID_KEY)
				|| !nbtPlayer.hasKey(Reference.INFUSION_CHARGES_KEY)) {
			if (showMessages) {
				ChatUtilities.sendTranslated(TextFormatting.RED, (ICommandSender) player, Reference.INFUSION_REQUIRED,
						new Object[0]);
				SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
			}
			return false;
		}
		if (player.capabilities.isCreativeMode || nbtPlayer.getInteger(Reference.INFUSION_CHARGES_KEY) >= cost) {
			if (!player.capabilities.isCreativeMode) {
				setCurrentEnergy(player, nbtPlayer.getInteger(Reference.INFUSION_CHARGES_KEY) - cost);
			}
			return true;
		}
		if (showMessages) {
			ChatUtilities.sendTranslated(TextFormatting.RED, (ICommandSender) player, Reference.INFUSION_NOCHARGE,
					new Object[0]);
			SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
		}
		return false;
	}

	public String getInfusionName() {
		return infusionName;
	}

}
