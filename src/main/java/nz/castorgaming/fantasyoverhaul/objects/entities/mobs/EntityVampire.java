package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.util.classes.BlockUtil;
import nz.castorgaming.fantasyoverhaul.util.classes.CreatureUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHandleDT;

public class EntityVampire extends EntityCreature implements IHandleDT {

	public static final DataParameter<Byte> GuardType = EntityDataManager.<Byte>createKey(EntityVampire.class, DataSerializers.BYTE);
	private Village villageObj;
	private BlockPos coffinBlockPos = new BlockPos(0, 0, 0);
	private ChunkPos coffinChunkPos = new ChunkPos(coffinBlockPos);
	private int attackTime;
	float damageDone = 0.0f;

	public EntityVampire(World world) {
		super(world);
		((PathNavigateGround) getNavigator()).setBreakDoors(true);
		((PathNavigateGround) getNavigator()).setCanSwim(false);
		addTask(this, new EntityAISwimming(this));
		addTask(this, new EntityAIRestrictSun(this));
		addTask(this, new EntityAIFleeSun(this, 1.0));
		addTask(this, new EntityAIRestrictOpenDoor(this));
		addTask(this, new EntityAIAttackMelee(this, 1.2, false));
		addTask(this, new EntityAIOpenDoor(this, true));
		addTask(this, new EntityAIWander(this, 1.0));
		addTask(this, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
		addTask(this, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityCreature.class, 0, false, true, (Predicate) this));
		experienceValue = 20;
	}

	protected void addRandomArmor() {
		setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemInit.CLOTH_VAMPIRE_BOOTS));
		boolean male = worldObj.rand.nextBoolean();
		if (male) {
			setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(worldObj.rand.nextInt(3) == 0 ? ItemInit.CHAIN_VAMPIRE_LEGGINGS : ItemInit.CLOTH_VAMPIRE_LEGGINGS));
			setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(worldObj.rand.nextInt(3) == 0 ? ItemInit.CHAIN_VAMPIRE_MALE_CHESTPLATE : ItemInit.CLOTH_VAMPIRE_MALE_CHESTPLATE));
		}
		else {
			setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(worldObj.rand.nextInt(4) != 0 ? ItemInit.CHAIN_VAMPIRE_LEGGINGS : ItemInit.CLOTH_VAMPIRE_LEGGINGS));
			setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(worldObj.rand.nextInt(3) == 0 ? ItemInit.CHAIN_VAMPIRE_FEMALE_CHESTPLATE : ItemInit.CLOTH_VAMPIRE_FEMALE_CHESTPLATE));
		}
	}

	private void addTask(EntityCreature entity, EntityAIBase ai) {
		int nextTask = entity.tasks.taskEntries.size();
		entity.tasks.addTask(nextTask, ai);
	}

	protected void attackEntity(Entity entity, float f) {
		if (attackTime <= 0 && f < 2.0f && entity.getEntityBoundingBox().maxY > getEntityBoundingBox().minY && entity.getEntityBoundingBox().minY < getEntityBoundingBox().maxY) {
			attackTime = 20;
			attackEntityAsMob(entity);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		boolean flag = false;
		float f = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;

		if (entity instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(getHeldItem(getActiveHand()), ((EntityLivingBase) entity).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}
		if (entity instanceof EntityVillager) {
			EntityVillager villagerEx = (EntityVillager) entity;
			if (villagerEx != null && worldObj.rand.nextInt(10) == 0) {
				damageDone += 4.0f;
				int taken = villagerEx.getCapability(CapabilityInit.EXTENDED_VILLAGER, null).takeBlood(30, this);
				if (taken > 0) {
					heal(4.0f);
					ParticleEffect.REDDUST.send(SoundEffect.fantasyoverhaul_RANDOM_DRINK, worldObj, entity.posX, entity.posY + entity.height * 0.8, entity.posZ, 0.5, 0.2, 16);
				}
			}
			flag = true;
		}
		else {
			boolean needsBlood = damageDone < 20.0f;
			if (needsBlood) {
				flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);
				if (flag) {
					int j;
					if (i > 0) {
						entity.addVelocity(-MathHelper.sin((float) (rotationYaw * Math.PI / 180.0f)) * i * 0.5f, 0.1, MathHelper.cos((float) (rotationYaw * Math.PI / 180.0f)) * i * 0.5f);
						motionX *= 0.6;
						motionZ *= 0.6;
					}
					if ((j = EnchantmentHelper.getFireAspectModifier(this)) > 0) {
						entity.setFire(j * 4);
					}
					if (entity instanceof EntityLivingBase) {
						EnchantmentHelper.applyThornEnchantments((EntityLivingBase) entity, this);
					}
					EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) entity, this);
				}
			}
		}

		return flag;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	protected void dropRareDrop(int drop) {
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(GuardType, new Byte((byte) 0));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_VILLAGER_AMBIENT;
	}

	@Override
	public float getCapDT(DamageSource source, float damage) {
		return 0.0f;
	}

	public String getCommandSenderName() {
		if (hasCustomName()) {
			return getCustomNameTag();
		}
		return getName();
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_VILLAGER_DEATH;
	}

	@Override
	protected SoundEvent getFallSound(int heightIn) {
		return heightIn > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
	}

	public int getGuardType() {
		return dataManager.get(GuardType);
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_VILLAGER_HURT;
	}

	@Override
	protected float getSoundPitch() {
		return 0.6f;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	public boolean isEntityApplicable(Entity entity) {
		return entity instanceof EntityVillager && villageObj != null || entity instanceof EntityPlayer && !IPlayerVampire.get((EntityPlayer) entity).isVampire();
	}

	@Override
	public void onDeath(DamageSource source) {
		if (!CreatureUtilities.checkForVampireDeath(this, source)) {
			return;
		}
		super.onDeath(source);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
		data = super.onInitialSpawn(difficulty, data);
		addRandomArmor();
		coffinBlockPos = new BlockPos(posX, posY, posZ);
		coffinChunkPos = new ChunkPos(coffinBlockPos);
		return data;
	}

	@Override
	public void onLivingUpdate() {
		updateArmSwingProgress();
		float f = getBrightness(1.0f);
		if (f > 0.5f) {
			entityAge += 2;
		}
		if (attackTime > 0) {
			--attackTime;
		}
		super.onLivingUpdate();
	}

	protected void playStepSound(int x, int y, int z, Block block) {
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		if (tag.hasKey("GuardType", 99)) {
			byte b0 = tag.getByte("GuardType");
			setGuardType(b0);
		}
		coffinBlockPos = new BlockPos(tag.getInteger("BaseX"), tag.getInteger("BaseY"), tag.getInteger("BaseZ"));
	}

	public void setGuardType(int byteIn) {
		dataManager.set(GuardType, Byte.valueOf((byte) byteIn));
	}

	public void setStalkingArea(int x, int y, int z) {
		coffinBlockPos = new BlockPos(x, y, z);
	}

	public void tryFillBloodCrucible() {
		int r = 6;
		for (int x = coffinBlockPos.getX() - 6; x <= coffinBlockPos.getX() + 6; x++) {
			for (int y = coffinBlockPos.getY() - 6; y <= coffinBlockPos.getY() + 6; y++) {
				for (int z = coffinBlockPos.getZ() - 6; z <= coffinBlockPos.getZ() + 6; z++) {
					BlockBloodCrucible.TileEntityBloodCrucible crucible = BlockUtil.getTileEntity((IBlockAccess) worldObj, x, y, z, BlockBloodCrucible.TileEntityBloodCrucible.class);
					if (crucible != null) {
						crucible.increaseBloodLevel();
					}
					return;
				}
			}
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if (!worldObj.isRemote) {
			if (worldObj.isDaytime()) {
				if (getAITarget() == null) {
					setAttackTarget(null);
				}

				if (ticksExisted % 100 == 2) {
					villageObj = null;
					damageDone = 0.0f;
					if (coffinChunkPos.getDistanceSq(this) > 16.0) {
						ParticleEffect.SMOKE.send(SoundEffect.fantasyoverhaul_RANDOM_POOF, this, 0.8, 1.5, 16);
						EntityUtil.moveBlockToPositionAndUpdate(this, coffinBlockPos.getX(), coffinBlockPos.getY(), coffinBlockPos.getZ(), 8);
						ParticleEffect.SMOKE.send(SoundEffect.fantasyoverhaul_RANDOM_POOF, this, 0.8, 1.5, 16);
						setHomePosAndDistance(coffinBlockPos, 4);
					}
				}
				if (ticksExisted % 20 == 2 && CreatureUtilities.isInSunlight(this)) {
					setFire(2);
				}
			}
			else if (damageDone >= 20.0f) {
				if (villageObj != null) {
					setAttackTarget(null);
					setRevengeTarget(null);
					villageObj = null;
					ParticleEffect.SMOKE.send(SoundEffect.fantasyoverhaul_RANDOM_POOF, this, 0.8, 1.5, 16);
					EntityUtil.moveBlockToPositionAndUpdate(this, coffinBlockPos.getX(), coffinBlockPos.getY(), coffinBlockPos.getZ(), 8);
					ParticleEffect.SMOKE.send(SoundEffect.fantasyoverhaul_RANDOM_POOF, this, 0.8, 1.5, 16);
					setHomePosAndDistance(coffinBlockPos, 4);
					tryFillBloodCrucible();
				}
			}
			else if (villageObj == null && ticksExisted % 500 == 2) {

				villageObj = worldObj.villageCollectionObj.getNearestVillage(new BlockPos(posX, posY, posX), 128);
				if (villageObj != null) {
					BlockPos townPos = villageObj.getCenter();
					ParticleEffect.SMOKE.send(SoundEffect.fantasyoverhaul_RANDOM_POOF, this, 0.8, 1.5, 16);
					EntityUtil.moveBlockToPositionAndUpdate(this, coffinBlockPos.getX(), coffinBlockPos.getY(), coffinBlockPos.getZ(), 8);
					ParticleEffect.SMOKE.send(SoundEffect.fantasyoverhaul_RANDOM_POOF, this, 0.8, 1.5, 16);
					setHomePosAndDistance(townPos, villageObj.getVillageRadius());
				}

			}
		}
	}

	@Override
	public void updateRidden() {
		super.updateRidden();
		if (getRidingEntity() instanceof EntityCreature) {
			EntityCreature entityCreature = (EntityCreature) getRidingEntity();
			renderYawOffset = entityCreature.renderYawOffset;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setByte("GuardType", (byte) getGuardType());
		tag.setInteger("BaseX", coffinBlockPos.getX());
		tag.setInteger("BaseY", coffinBlockPos.getY());
		tag.setInteger("BaseZ", coffinBlockPos.getZ());
	}

}
