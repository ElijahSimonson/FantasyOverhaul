package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.IPlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.PlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.init.InfusionInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.armor.specialArmors.vampire.VampireClothes;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityDemon;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityHornedHuntsman;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityNightmare;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityReflection;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityVampire;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;

public class CreatureUtilities {

	private static Class classBloodMagicDemon;

	public static boolean isDemonic(Entity entity) {
		if (entity != null) {
			if (entity instanceof EntityDemon || entity instanceof EntityGhast || entity instanceof EntityBlaze || entity instanceof EntityMagmaCube || entity instanceof EntityLeonard
					|| entity instanceof EntityLordOfTorment || entity instanceof EntityImp || entity instanceof EntityLilith || entity instanceof EntityWither || isModDemon(entity)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isModDemon(Entity entity) {
		if (classBloodMagicDemon == null) {
			try {
				classBloodMagicDemon = Class.forName("WayofTime.alchemicalWizardru.common.entity.mob.EntityDemon");
			} catch (ClassNotFoundException ex) {
			}
		}
		return classBloodMagicDemon != null && classBloodMagicDemon.isAssignableFrom(entity.getClass());
	}

	public static boolean isUndead(Entity entity) {
		if (entity != null) {
			if (entity instanceof EntityLiving) {
				return ((EntityLiving) entity).isEntityUndead();
			}
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = ((EntityPlayer) entity);
				return IPlayerVampire.get(player).isVampire() || InfusionBrewEffect.getActiveBrew(player) == InfusedBrew.Grave;
			}
		}
		return false;
	}

	public static boolean isInsect(EntityLivingBase entity) {
		return entity != null && entity.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD;
	}

	public static boolean isSpirit(EntityLivingBase entity) {
		return entity != null && (entity instanceof EntityMandrake || entity instanceof EntityHornedHuntsman || entity instanceof EntityTreefyd || entity instanceof EntityNightmare
				|| entity instanceof EntitySpirit);
	}

	public static EntityLiving spawnWithEgg(EntityLiving entity, boolean requirePersistance) {
		if (entity != null) {
			entity.onInitialSpawn(entity.worldObj.getDifficultyForLocation(entity.getPosition()), null);
			if (requirePersistance) {
				entity.enablePersistence();
			}
		}
		return entity;
	}

	public static boolean isWitch(Entity entity) {
		if (entity != null) {
			if (entity instanceof EntityWitch || entity instanceof EntityBabaYaga) {
				return true;
			}
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (InvUtil.hasItem(player.inventory, ItemInit.POPPET_VOODOO) || InvUtil.hasItem(player.inventory, ItemInit.POPPET_VAMPURIC)
						|| Infusion.getInfusionID(player).equals(InfusionInit.INFERNAL.getInfusionName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isWoodenDamage(DamageSource source) {
		if (source.getSourceOfDamage() != null && source.getSourceOfDamage() instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) source.getSourceOfDamage();
			if (entity instanceof EntityHornedHuntsman && !source.isProjectile()) {
				return true;
			}
			ItemStack stack = entity.getHeldItemMainhand();
			if (stack != null && stack.getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) stack.getItem();
				if (sword.getToolMaterialName().equalsIgnoreCase(Item.ToolMaterial.WOOD.toString())) {
					return true;
				}
			}
		}
		if (source instanceof BoltDamageSource) {
			return ((BoltDamageSource) source).isWooden();
		}
		return false;
	}

	public static boolean isSilverDamage(DamageSource source) {
		if (source instanceof EntityDamageSourceIndirectSilver) {
			return true;
		}
		if (source.getSourceOfDamage() != null && source.getSourceOfDamage() instanceof EntityBolt) {
			return ((EntityBolt) source.getSourceOfDamage()).isSilverDamage();
		}
		if (!source.isProjectile() && source.getEntity() != null && source.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) source.getEntity();
			ItemStack stack = entity.getHeldItemMainhand();
			if (stack != null && stack.getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) stack.getItem();
				String materialName = sword.getToolMaterialName();
				if (materialName != null) {
					if (materialName.equals("SILVER")) {
						return true;
					}
					int colonPos = materialName.lastIndexOf(":");
					if (colonPos >= 0 && colonPos < materialName.length()) {
						return materialName.substring(colonPos + 1).equals("SILVER");
					}
				}
			}
		}
		return false;
	}

	public static boolean isWerewolf(Entity entity) {
		return isWerewolf(entity, false);
	}

	public static boolean isWerewolf(Entity entity, boolean includeUnshifted) {
		if (entity == null) {
			return false;
		}
		if (entity instanceof EntityWolfman) {
			return true;
		}
		if (entity instanceof EntityReflection) {
			return ((EntityReflection) entity).getModel() == 1;
		}
		if (entity instanceof EntityVillagerWere) {
			return includeUnshifted;
		}
		if (entity instanceof EntityPlayer) {
			ExtendedPlayer playerEx = IExtendPlayer.get((EntityPlayer) entity);
			PlayerWerewolf playerWere = IPlayerWerewolf.get((EntityPlayer) entity);
			return (includeUnshifted && playerWere.getWerewolfLevel() > 0) || playerEx.getCreatureType() == TransformCreatures.WOLF || playerEx.getCreatureType() == TransformCreatures.WOLFMAN;
		}
		if (entity instanceof EntityLiving) {
			String name = entity.getClass().getSimpleName();
			return name != null && name.toUpperCase().contains("WEREWOLF");
		}
		return false;
	}

	public static boolean isVampire(Entity entity) {
		if (entity == null) {
			return false;
		}
		if (entity instanceof EntityVampire) {
			return true;
		}
		if (entity instanceof EntityReflection) {
			return ((EntityReflection) entity).isVampire();
		}
		if (entity instanceof EntityPlayer) {
			final PlayerVampire playerEx = IPlayerVampire.get((EntityPlayer) entity);
			return playerEx.isVampire();
		}
		if (entity instanceof EntityLiving) {
			final String name = entity.getClass().getSimpleName();
			return name != null && name.toUpperCase().contains("VAMPIRE");
		}
		return false;
	}

	public static boolean isFullMoon(final World world) {
		return world.getCurrentMoonPhaseFactor() == 1.0 && !world.isDaytime();
	}

	public static boolean isImmuneToDisease(final EntityLivingBase livingEntity) {
		return isUndead(livingEntity) || isDemonic(livingEntity) || isWerewolf(livingEntity, true) || !livingEntity.isNonBoss() || livingEntity instanceof EntityGolem;
	}

	public static boolean isImmuneToPoison(final EntityLivingBase livingEntity) {
		return isWerewolf(livingEntity, false);
	}

	public static boolean isInSunlight(final EntityLivingBase entity) {
		final World world = entity.worldObj;
		if (world.provider.getDimension() == Config.instance().dimensionDreamID || world.provider.getDimension() == Config.instance().dimensionTormentID || world.provider.getHasNoSky()
				|| !world.provider.isSurfaceWorld() || !world.isDaytime()) {
			return false;
		}
		final int x = MathHelper.floor_double(entity.posX);
		final int y = MathHelper.floor_double(entity.posY);
		final int z = MathHelper.floor_double(entity.posZ);
		Biome biome = world.getBiome(new BlockPos(x, y, z));
		return !biome.getBiomeName().equals("Ominous Woods") && (!world.isRaining() || !biome.canRain()) && world.canBlockSeeSky(new BlockPos(z, y + MathHelper.ceiling_double_int(entity.height), z));
	}

	public static boolean checkForVampireDeath(EntityLivingBase creature, DamageSource source) {
		boolean dead = false;
		if (source.isFireDamage() || source instanceof EntityUtil.DamageSourceVampireFire) {
			if (VampireClothes.isExtendedFlameProtectionActive(creature)) {
				dead = (creature.worldObj.rand.nextInt(4) == 0);
			}
			else {
				dead = (!VampireClothes.isFlameProtectionActive(creature) || creature.worldObj.rand.nextInt(4) != 0);
			}
		}
		else if (source instanceof EntityUtil.DamageSourceSunlight) {
			dead = true;
		}
		else if (creature instanceof EntityPlayer && Reference.modHooks.canVampireBeKilled((EntityPlayer) creature)) {
			dead = true;
		}
		else if (source == DamageSource.inWall || source == DamageSource.outOfWorld) {
			dead = true;
		}
		else if (source.getEntity() != null && (isWerewolf(source.getEntity()) || isVampire(source.getEntity()) || !source.getEntity().isNonBoss())) {
			dead = true;
		}
		else if (isWerewolf(creature, true) && isSilverDamage(source)) {
			dead = true;
		}
		if (!dead) {
			creature.setHealth(1.0f);
			if (creature instanceof EntityPlayer) {
				((EntityPlayer) creature).getFoodStats().addExhaustion(5.0f);
			}
			if (source.isExplosion() && creature.worldObj.rand.nextInt(4) == 0) {
				creature.setFire(2);
			}
			return false;
		}
		return true;
	}
}
