package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.items.main.ItemBase;
import nz.castorgaming.fantasyoverhaul.objects.worlds.WorldProviderDreamWorld;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class EntityNightmare extends EntityMob {

	private int attackTimer;
	private int defenseTimer;
	private DataParameter<Boolean> SCREAMING = EntityDataManager.createKey(EntityNightmare.class, DataSerializers.BOOLEAN);
	private DataParameter<Boolean> DEFENDED = EntityDataManager.createKey(EntityNightmare.class, DataSerializers.BOOLEAN);
	private DataParameter<String> VICTIM = EntityDataManager.createKey(EntityNightmare.class, DataSerializers.STRING);

	public EntityNightmare(World world) {
		super(world);
		isImmuneToFire = true;
		setSize(0.6f, 1.8f);
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIBreakDoor(this));
		tasks.addTask(3, new EntityAIAttackMelee(this, 1.0, true));
		tasks.addTask(4, new EntityAIMoveTowardsTarget(this, 0.9, 32.0f));
		tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0));
		tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
		tasks.addTask(7, new EntityAIWander(this, 1.0));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false, true));
		experienceValue = 25;
	}

	@Override
	public int getTalkInterval() {
		return super.getTalkInterval() * 2;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(VICTIM, "");
		dataManager.register(SCREAMING, false);
		dataManager.register(DEFENDED, false);
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	public boolean isScreaming() {
		return dataManager.get(SCREAMING);
	}

	public void setScreaming(boolean screaming) {
		dataManager.set(SCREAMING, screaming);
	}

	public boolean isDefended() {
		return dataManager.get(DEFENDED);
	}

	public void setDefended(boolean defended) {
		dataManager.set(DEFENDED, defended);
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		if (!worldObj.isRemote && isEntityAlive()) {
			setScreaming(true);
		}
		else {
			setScreaming(false);
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (getVictimUUID() == Reference.BLANK_UUID) {
			compound.setString("Victim", Reference.BLANK_UUID.toString());
		}
		else {
			compound.setString("Victim", getVictimUUID().toString());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		String s = compound.getString("Victim");
		if (!s.equals(Reference.BLANK_UUID.toString())) {
			setVictim(UUID.fromString(s));
		}
	}

	public UUID getVictimUUID() {
		String uuString = dataManager.get(VICTIM);
		return (uuString == null) ? Reference.BLANK_UUID : (uuString.isEmpty() ? Reference.BLANK_UUID : UUID.fromString(uuString));
	}

	public void setVictim(UUID victim) {
		dataManager.set(VICTIM, victim.toString());
	}

	public void setVictim(EntityPlayer player) {
		setVictim(player.getUniqueID());
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!worldObj.isRemote) {
			if (defenseTimer > 0 && --defenseTimer == 0) {
				setDefended(false);
			}
			if (!isDead && getVictimUUID() != Reference.BLANK_UUID && (getAttackTarget() == null || getAttackTarget().isDead || getDistanceSqToEntity(getAttackTarget()) > 265.0)
					|| (worldObj.rand.nextInt(5) == 0 && getAttackTarget() instanceof EntityPlayer && WorldProviderDreamWorld.getPlayerHasNightmare((EntityPlayer) getAttackTarget()) == 0
							&& !isWakingNightmare((EntityPlayer) getAttackTarget()))) {
				ParticleEffect.EXPLODE.send(SoundEffect.NONE, this, 1.0, 2.0, 16);
				setDead();
			}
		}
		if (attackTimer > 0) {
			--attackTimer;
		}
	}

	private boolean isWakingNightmare(EntityPlayer player) {
		NBTTagCompound nbtTag = Infusion.getNBT(player);
		if (nbtTag != null && nbtTag.hasKey(Reference.WAKING_NIGHTMARE)) {
			return nbtTag.getBoolean(Reference.WAKING_NIGHTMARE);
		}
		return player.isPotionActive(Potions.WAKING_NIGHTMARE);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		attackTimer = 15;
		worldObj.setEntityState(this, (byte) 4);
		if (entityIn != null && entityIn instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityIn;
			if (findInInventory(player.inventory, ItemInit.CHARM_DISRUPTED_DREAMS)) {
				int index = player.worldObj.rand.nextInt(player.inventory.armorInventory.length);
				if (player.inventory.armorInventory[index] != null) {
					Infusion.dropEntityItemWithRandomChoice(player, player.inventory.armorInventory[index], true);
					player.inventory.armorInventory[index] = null;
				}
			}
		}
		float f = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		if (dimension != Config.instance().dimensionDreamID) {
			f = 0.5f;
		}
		int i = 0;
		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getEnchantmentModifierDamage(this.getHeldEquipment(), DamageSource.magic);
			i += EnchantmentHelper.getKnockbackModifier(this);
		}
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);
		if (flag) {
			if (i > 0) {
				entityIn.addVelocity(-MathHelper.sin(this.rotationYaw * 3.1415927f / 180.0f) * i * 0.5f, 0.1, MathHelper.cos(this.rotationYaw * 3.1415927f / 180.0f) * i * 0.5f);
				this.motionX *= 0.6;
				this.motionY *= 0.6;
			}
			int fireMod = EnchantmentHelper.getFireAspectModifier(this);
			if (fireMod > 0) {
				entityIn.setFire(fireMod * 4);
			}
		}
		return flag;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isDefended()) {
			return false;
		}

		boolean weakeningWeapon = false;
		if (source instanceof EntityDamageSource && ((EntityDamageSource) source).getEntity() != null && ((EntityDamageSource) source).getEntity() instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) ((EntityDamageSource) source).getEntity();
			if (living.getHeldItemMainhand() != null && living.getHeldItemMainhand().getItem() == ItemInit.HUNTSMAN_SPEAR) {
				weakeningWeapon = true;
			}
			if (!worldObj.isRemote && worldObj.getBlockState(new BlockPos(posX, posY, posZ)).getBlock() != BlockInit.FLOWING_SPIRIT) {
				defenseTimer = ((dimension == Config.instance().dimensionDreamID) ? (weakeningWeapon ? 40 : 80) : (weakeningWeapon ? 30 : 40));
				setDefended(true);
			}
			return super.attackEntityFrom(source, Math.min(amount, 15.0f));
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEffect.NIGHTMARE_DEATH.event();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEffect.NIGHTMARE_LIVE.event();
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEffect.NIGHTMARE_HURT.event();
	}

	private boolean findInInventory(InventoryPlayer inventory, ItemBase item) {
		for (int i = 0; i < inventory.mainInventory.length; i++) {
			ItemStack stack = inventory.mainInventory[i];
			if (stack != null && item.isMatch(stack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		if (dimension == Config.instance().dimensionDreamID) {
			int chance = rand.nextInt(Math.max(10 - lootingModifier, 5));
			int quantity = (lootingModifier > 0 && chance == 0) ? 2 : 1;
			entityDropItem(ItemInit.MELLIFLUOUS_HUNGER.createStack(quantity), 0.0f);
		}
	}

	@Override
	public void onDeath(DamageSource cause) {
		if (!worldObj.isRemote && cause != null && cause.getEntity() != null && cause.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) cause.getEntity();
			UUID victim = getVictimUUID();
			if (victim != null && victim != Reference.BLANK_UUID && player.getUniqueID() == victim && dimension == Config.instance().dimensionDreamID) {
				WorldProviderDreamWorld.setPlayerLastNightmareKillNow(player);
			}
		}
		super.onDeath(cause);
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}

	@Override
	public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
		return cls.isAssignableFrom(EntityPlayer.class);
	}
}
