package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import com.jcraft.jorbis.Block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHandleDT;

public class EntityHornedHuntsman extends EntityMob implements IRangedAttackMob, IHandleDT {

	public static final DataParameter<Integer> INVUL_TIME = EntityDataManager
			.<Integer>createKey(EntityHornedHuntsman.class, DataSerializers.VARINT);
	public static final DataParameter<Byte> PLAYER_CREATED = EntityDataManager
			.<Byte>createKey(EntityHornedHuntsman.class, DataSerializers.BYTE);
	private int attackTimer;
	private boolean explosiveEntrance;
	long ticksSinceTeleport;

	public EntityHornedHuntsman(World worldIn) {
		super(worldIn);
		ticksSinceTeleport = 0L;
		setSize(1.4f, 3.2f);
		isImmuneToFire = true;
		((PathNavigateGround) getNavigator()).setCanSwim(true);
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIAttackMelee(this, 1.0, true));
		tasks.addTask(3, new EntityAIMoveTowardsTarget(this, 1.0, 48.0f));
		tasks.addTask(4, new EntityAIAttackRanged(this, 1.0, 20, 60, 30.0f));
		tasks.addTask(5, new EntityAIWander(this, 1.0));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
		tasks.addTask(7, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(2,
				new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 0, true, false, null));
		experienceValue = 70;
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(400.0);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
	}

	@Override
	public boolean attackEntityAsMob(Entity victim) {
		attackTimer = 10;
		worldObj.setEntityState(this, (byte) 4);
		boolean flag = victim.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + rand.nextInt(15));
		if (flag) {
			victim.motionY += 0.4000000059604645;
		}
		playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0f, 1.0f);
		return flag;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return super.attackEntityFrom(source, Math.max(damage, 15));
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase targetEntity, float distanceFactor) {
		EntityTippedArrow arrow = new EntityTippedArrow(worldObj, this);

		double d0 = targetEntity.posX - posX;
		double d1 = targetEntity.getEntityBoundingBox().minY + targetEntity.height / 3.0F - arrow.posY;
		double d2 = targetEntity.posZ - posZ;
		double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
		arrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F,
				14 - worldObj.getDifficulty().getDifficultyId() * 4);
		arrow.setDamage(distanceFactor * 2.0F + rand.nextGaussian() * 0.25D
				+ worldObj.getDifficulty().getDifficultyId() * 0.11F);
		arrow.setKnockbackStrength(2);
		playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (getRNG().nextFloat() * 0.4f + 0.8f));
		worldObj.spawnEntityInWorld(arrow);
	}

	@Override
	public boolean canAttackClass(Class<? extends EntityLivingBase> clazz) {
		return super.canAttackClass(clazz);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	public void causeExplosiveEntrance() {
		explosiveEntrance = true;
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		super.collideWithEntity(entity);
	}

	@Override
	protected int decreaseAirSupply(int par1) {
		return par1;
	}

	@Override
	protected void dropFewItems(boolean par1, int par2) {
		entityDropItem(new ItemStack(Items.SKULL, worldObj.rand.nextInt(3) == 0 ? 3 : 2, 1), 0.0f);
		Enchantment enchantment = Enchantment.REGISTRY.getRandomObject(getRNG());
		int k = MathHelper.getRandomIntegerInRange(rand,
				Math.min(enchantment.getMinLevel() + 2, enchantment.getMaxLevel()), enchantment.getMaxLevel());
		ItemStack itemstack = Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(enchantment, k));
		entityDropItem(itemstack, 0.0f);
		entityDropItem(ItemInit.INFERNAL_BLOOD.createStack(), 0.0f);
		if (worldObj.rand.nextInt(4) == 0) {
			entityDropItem(new ItemStack(ItemInit.HUNTSMAN_SPEAR), 0.0f);
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(INVUL_TIME, 0);
		dataManager.register(PLAYER_CREATED, (byte) 0);
	}

	@SideOnly(Side.CLIENT)
	public int getAttackTimer() {
		return attackTimer;
	}

	public float getBrightness() {
		return 1.0f;
	}

	@Override
	public float getCapDT(DamageSource source, float damage) {
		return 15.0f;
	}

	public String getCommandSenderName() {
		return hasCustomName() ? getCustomNameTag() : I18n.format("entity.hornedHuntsman.name");
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITHER_DEATH;
	}

	@Override
	protected Item getDropItem() {
		return null;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
	}

	public int getInvulTime() {
		return dataManager.get(INVUL_TIME);
	}

	protected SoundEvent getLivingSound() {
		return SoundEvents.ENTITY_ENDERDRAGON_GROWL;
	}

	@Override
	public int getTotalArmorValue() {
		return 4;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte par1) {
		if (par1 == 1) {
			attackTimer = 10;
			playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0f, 1.0f);
		} else {
			super.handleStatusUpdate(par1);
		}
	}

	public void ignite() {
		setInvulTime(150);
		setHealth(getMaxHealth() / 4.0f);
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	public boolean isNonBoss() {
		return false;
	}

	public boolean isPlayerCreated() {
		return dataManager.get(PLAYER_CREATED) != 0;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (attackTimer > 0) {
			attackTimer--;
		}
	}

	protected void playStepSound(int par1, int par2, int par3, Block block) {
		playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.0f, 1.0f);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setPlayerCreated(compound.getBoolean("PlayerCreated"));
		setInvulTime(compound.getInteger("InvulTime"));
		if (compound.hasKey("explosiveEntrance")) {
			explosiveEntrance = compound.getBoolean("explosiveEntrance");
		} else {
			explosiveEntrance = false;
		}
	}

	public void setInvulTime(int time) {
		dataManager.set(INVUL_TIME, time);
	}

	public void setPlayerCreated(boolean playerCreated) {
		enablePersistence();
		byte b0 = dataManager.get(PLAYER_CREATED);
		if (playerCreated) {
			dataManager.set(PLAYER_CREATED, (byte) (b0 | 0x1));
		} else {
			dataManager.set(PLAYER_CREATED, (byte) (b0 & 0xFFFFFFFF));
		}
	}

	protected boolean teleportTo(double x, double y, double z) {
		double prevX = posX, prevY = posY, prevZ = posZ;
		posX = x;
		posY = y;
		posZ = z;
		boolean flag = false;
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY);
		int k = MathHelper.floor_double(posZ);
		BlockPos testPos = new BlockPos(i, j, k);
		if (!worldObj.isAirBlock(testPos) && worldObj.isBlockNormalCube(testPos, true)) {
			boolean flag2 = false;
			while (!flag2 && j > 0) {
				testPos.offset(EnumFacing.DOWN);
				IBlockState testState = worldObj.getBlockState(testPos);
				if (testState.getMaterial().blocksMovement()) {
					flag2 = true;
				} else {
					--posY;
					--j;
				}
			}
			if (flag2) {
				setPosition(posX, posY, posZ);
				if (worldObj.getCollisionBoxes(this, getCollisionBoundingBox()).isEmpty()
						&& !worldObj.containsAnyLiquid(getCollisionBoundingBox())) {
					flag = true;
				}
			}
		}
		if (!flag) {
			setPosition(prevX, prevY, prevZ);
			return false;
		}
		short short1 = 128;
		for (int l = 0; l < short1; ++l) {
			double d1 = l / (short1 / 1.0);
			float f = (rand.nextFloat() - 0.5f) * 0.2f;
			float f2 = (rand.nextFloat() - 0.5f) * 0.2f;
			float f3 = rand.nextFloat() * 0.5f * 0.2f;
			double d2 = prevX + (posX - prevX) * d1 + (rand.nextDouble() - 0.5) * width * 2.0;
			double d3 = prevY + (posY - prevY) * d1 + rand.nextDouble() * height;
			double d4 = prevZ + (posZ - prevZ) * d1 + (rand.nextDouble() - 0.5) * width * 2.0;
			worldObj.spawnParticle(EnumParticleTypes.PORTAL, d2, d3, d4, f, f2, f3);
		}
		worldObj.playSound(prevX, prevY, prevZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0f, 1.0f,
				false);
		playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
		return true;
	}

	protected boolean teleportToEntity(Entity entity) {
		Vec3d vec = new Vec3d(posX - entity.posX,
				getEntityBoundingBox().minY + height / 2.0f - entity.posY + entity.getEyeHeight(), posZ - entity.posZ);
		vec = vec.normalize();
		double d0 = 8.0;
		double d2 = posX + (rand.nextDouble() - 0.5) * 8.0 - vec.xCoord * d0;
		double d3 = posY + (rand.nextInt(16) - 8) - vec.yCoord * d0;
		double d4 = posZ + (rand.nextDouble() - 0.5) * 8.0 - vec.zCoord * d0;
		return teleportTo(d2, d3, d4);
	}

	@Override
	protected void updateAITasks() {
		if (getInvulTime() > 0) {
			int i = getInvulTime() - 1;
			if (i <= 0) {
				if (explosiveEntrance) {
					worldObj.newExplosion(this, posX, posY + getEyeHeight(), posZ, 6.0f, false, false);
				}
				worldObj.playBroadcastSound(1013, new BlockPos(posX, posY, posZ), 0);
			}
			setInvulTime(i);
			if (ticksExisted % 10 == 0) {
				heal(20.f);
			}
		} else {
			super.updateAITasks();
			if (ticksExisted % 20 == 0) {
				heal(1.0f);
			}
			if (ticksExisted % 20 == 0 && worldObj.rand.nextInt(5) == 0 && getAttackTarget() != null
					&& !worldObj.isRemote && getEntitySenses().canSee(getAttackTarget())) {
				double d0 = getDistanceSq(getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY,
						getAttackTarget().posZ);
				getLookHelper().setLookPositionWithEntity(getAttackTarget(), 30.0f, 30.0f);
				float range = 30.0f;
				float f2;
				float f = f2 = MathHelper.sqrt_double(d0) / range;
				if (f < 0.1f) {
					f2 = 0.1f;
				}
				if (f2 > 1.0f) {
					f2 = 1.0f;
				}
				attackEntityWithRangedAttack(getAttackTarget(), f);
			}
			if (ticksExisted % (200 + worldObj.rand.nextInt(4) * 100) == 0 && getAttackTarget() != null
					&& getDistanceSqToEntity(getAttackTarget()) <= 256.0f
					&& getEntitySenses().canSee(getAttackTarget())) {
				EntityWolf wolf = new EntityWolf(worldObj);
				wolf.setLocationAndAngles(posX - 0.5 + worldObj.rand.nextDouble(), posY,
						posZ - 0.5 + worldObj.rand.nextDouble(), rotationYawHead, rotationPitch);
				wolf.setAngry(true);
				wolf.setAttackTarget(getAttackTarget());
				wolf.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20000, 1));
				ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_FIZZ, wolf, 2.0, 2.0, 10);
				worldObj.spawnEntityInWorld(wolf);
			}
			if (!worldObj.isRemote && getNavigator().noPath() && getAttackTarget() != null
					&& ticksExisted - ticksSinceTeleport > 100) {
				ticksSinceTeleport = ticksExisted;
				teleportToEntity(getAttackTarget());
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("PlayerCreated", isPlayerCreated());
		compound.setInteger("Invul", getInvulTime());
		compound.setBoolean("explosiveEntrance", explosiveEntrance);
	}
}
