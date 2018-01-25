package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.client.renderer.RenderReflection;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.powers.symbols.SymbolEffect;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.classes.CreatureUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.RandomCollection;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHandleDT;

public class EntityReflection extends EntityMob implements IRangedAttackMob, IHandleDT {

	public static final DataParameter<Byte> ModelType = EntityDataManager.createKey(EntityReflection.class, DataSerializers.BYTE);
	public static final DataParameter<String> Owner = EntityDataManager.createKey(EntityReflection.class, DataSerializers.STRING);
	private int attackTimer;
	private boolean freeSpawn;
	private boolean isVampire;
	private int livingTicks;
	private EntityAIAttackRanged aiArrowAttack;
	private EntityAIAttackMelee aiAttackOnCollide;
	private UUID owner;
	private Task task;
	private static final RandomCollection<SymbolEffect> SPELLS;
	private UUID lastSkinOwner;
	@SideOnly(Side.CLIENT)
	private ThreadDownloadImageData downloadImageSkin;
	@SideOnly(Side.CLIENT)
	private ResourceLocation locationSkin;

	public EntityReflection(World worldIn) {
		super(worldIn);
		livingTicks = -1;
		aiArrowAttack = new EntityAIAttackRanged(this, 1.0, 20, 60, 15.0f);
		aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2, false);
		owner = Reference.BLANK_UUID;
		task = Task.NONE;
		setSize(0.6f, 1.8f);
		isImmuneToFire = true;

		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(3, new EntityAIWander(this, 1.0));
		tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
		tasks.addTask(5, new EntityAILookIdle(this));

		targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true, true));
		targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));

		experienceValue = 50;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(Owner, Reference.BLANK_UUID.toString());
		dataManager.register(ModelType, (byte) 0);
	}

	public UUID getOwnerSkin() {
		return UUID.fromString(dataManager.get(Owner));
	}

	public void setOwnerSkin(UUID skinUUID) {
		dataManager.set(Owner, skinUUID.toString());
	}

	public void setOwner(UUID owner) {
		enablePersistence();
		this.owner = owner;
	}

	public EntityPlayer getOwnerEntity() {
		return worldObj.getPlayerEntityByUUID(owner);
	}

	public void setModel(int model) {
		dataManager.set(ModelType, (byte) model);
	}

	public int getModel() {
		return dataManager.get(ModelType);
	}

	public void setLifetime(int ticks) {
		livingTicks = ticks;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (attackTimer > 0) {
			--attackTimer;
		}

		if (!worldObj.isRemote && ticksExisted % 30 == 1) {
			if (!freeSpawn && dimension != Config.instance().dimensionMirrorID) {
				setDead();
				return;
			}
			if (livingTicks > -1 && --livingTicks == 0) {
				setDead();
				return;
			}
			double R = 10.0;
			double RY = 8.0;
			AxisAlignedBB bounds = new AxisAlignedBB(posX - R, posY - RY, posZ - R, posX + R, posY + RY, posZ + R);
			List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, bounds);
			EntityPlayer ownerEntity = getOwnerEntity();
			boolean ownerFound = false;
			EntityPlayer closest = null;
			double distance = Double.MAX_VALUE;
			for (EntityPlayer player : players) {
				double newDistance = player.getDistanceSqToEntity(this);
				if (closest == null || newDistance < distance) {
					closest = player;
					distance = newDistance;
				}
				if (ownerEntity == player) {
					ownerFound = true;
				}
			}
			if (ownerEntity == null || !ownerFound) {
				if (closest != null) {
					setOwner(closest.getCommandSenderEntity().getUniqueID());
				}
				else {
					setOwner(Reference.BLANK_UUID);
				}
			}
			boolean resetGear = true;
			UUID skinName = getOwnerSkin();
			if (getOwnerEntity() != null && getOwnerSkin() != Reference.BLANK_UUID) {
				EntityPlayer owner = (ownerEntity == null || !ownerFound) ? getOwnerEntity() : ownerEntity;
				if (owner != null) {
					for (EntityEquipmentSlot slot : new EntityEquipmentSlot[] { EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD }) {
						ItemStack stack = owner.inventory.getStackInSlot(slot.getSlotIndex());
						if (stack != null) {
							stack = stack.copy();
						}
						setItemStackToSlot(slot, stack);
					}
					ItemStack bestWeapon = null;
					double bestDamage = 0.0;
					for (int hot = 0; hot < 9; ++hot) {
						ItemStack stack2 = owner.inventory.getStackInSlot(hot);
						if (stack2 != null) {
							Multimap modifierMap = stack2.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
							Iterator itr = modifierMap.get(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName()).iterator();
							double damage = 0.0;
							while (itr.hasNext()) {
								AttributeModifier modifier = (AttributeModifier) itr.next();
								if (modifier.getOperation() == 0) {
									damage += modifier.getAmount();
								}
							}
							if (damage > bestDamage) {
								bestWeapon = stack2;
								bestDamage = damage;
							}
						}
					}
					ExtendedPlayer playerEx = IExtendPlayer.get(owner);
					if (playerEx != null) {
						setModel((playerEx.getCreatureType() == TransformCreatures.WOLFMAN) ? 1 : 0);
						isVampire = IPlayerVampire.get(owner).isVampire();
						if (playerEx.getCreatureType() == TransformCreatures.PLAYER) {
							skinName = playerEx.getOtherPlayerSkin();
						}
					}
					ItemStack heldItem = (bestWeapon != null) ? bestWeapon : owner.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
					if (heldItem != null) {
						heldItem = heldItem.copy();
						Reference.modHooks.makeItemModProof(heldItem);
					}
					if (getModel() == 1) {
						heldItem = null;
						getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
					}
					else {
						getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
					}

					setItemStackToSlot(EntityEquipmentSlot.MAINHAND, heldItem);
					resetGear = false;
					if (ticksExisted % 30 == 1) {
						clearActivePotions();
						for (PotionEffect effect : owner.getActivePotionEffects()) {
							addPotionEffect(new PotionEffect(effect));
						}
					}
				}
			}
			if (resetGear) {
				for (EntityEquipmentSlot slot : new EntityEquipmentSlot[] { EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD }) {
					setItemStackToSlot(slot, null);
				}
			}
			setOwnerSkin(skinName);
			ItemStack held = getHeldItem(EnumHand.MAIN_HAND);
			if (held != null) {
				if (held.getItem() == ItemInit.mystic_branch || held.getItem() == ItemInit.crossbow_pistol || held.getItem() instanceof ItemBow) {
					if (task == Task.MELEE) {
						tasks.removeTask(aiAttackOnCollide);
					}
					tasks.addTask(2, aiArrowAttack);
					task = Task.RANGED;
				}
				else {
					if (task == Task.RANGED) {
						tasks.removeTask(aiArrowAttack);
					}
					tasks.addTask(2, aiAttackOnCollide);
					task = Task.MELEE;
				}
			}
			else {
				if (task == Task.RANGED) {
					tasks.removeTask(aiArrowAttack);
				}
				tasks.addTask(2, aiAttackOnCollide);
				task = Task.MELEE;
			}
			if (isEntityAlive() && getAttackTarget() != null && getNavigator().noPath() && getEntitySenses().canSee(getAttackTarget())) {
				EntityLivingBase attackTarget = getAttackTarget();
				float range = 1.0f;
				castSpell(attackTarget, range, SymbolEffect.REGISTRY.getObject(Reference.location("Attraho")));
			}
		}
		if (!worldObj.isRemote && worldObj.rand.nextDouble() < 0.05 && getAttackTarget() != null && getAttackTarget().isAirBorne
				|| (getAttackTarget() instanceof EntityPlayer && ((EntityPlayer) getAttackTarget()).capabilities.isFlying) && !getAttackTarget().isPotionActive(MobEffects.SLOWNESS)) {
			getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 5));
		}
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		((BlockMirror) BlockInit.mirror).demonSlain(worldObj, posX, posY, posZ);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return super.attackEntityFrom(source, Math.min(amount, 6.0f));
	}

	@Override
	public float getCapDT(DamageSource source, float damage) {
		return 2.0f;
	}

	public boolean isVampire() {
		return isVampire;
	}

	@SideOnly(Side.CLIENT)
	public int getAttackTimer() {
		return attackTimer;
	}

	@Override
	public float getBrightness(float partialTicks) {
		return 1.0f;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEffect.fantasyoverhaul_MOB_REFLECTION_HURT.event();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEffect.fantasyoverhaul_MOB_REFLECTION_DEATH.event();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEffect.fantasyoverhaul_MOB_REFLECTION_SPEECH.event();
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	private static RandomCollection<SymbolEffect> createSpells() {
		RandomCollection<SymbolEffect> spells = new RandomCollection<SymbolEffect>();
		spells.add(14.0, SymbolEffect.REGISTRY.getObject(Reference.location("symbol_ignianima")));
		spells.add(2.0, SymbolEffect.REGISTRY.getObject(Reference.location("symbol_expelliarmus")));
		spells.add(2.0, SymbolEffect.REGISTRY.getObject(Reference.location("symbol_flipendo")));
		spells.add(2.0, SymbolEffect.REGISTRY.getObject(Reference.location("symbol_impedimenta")));
		spells.add(1.0, SymbolEffect.REGISTRY.getObject(Reference.location("symbol_confundus")));
		return spells;
	}

	@SideOnly(Side.CLIENT)
	public ThreadDownloadImageData getDownloadImageSkin(ResourceLocation location, String name) {
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		Object object = texturemanager.getTexture(location);
		if (object == null) {
			object = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(name)), RenderReflection.SKIN,
					new ImageBufferDownload());
			texturemanager.loadTexture(location, (ITextureObject) object);
		}
		return (ThreadDownloadImageData) object;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		ItemStack held = getHeldItemMainhand();
		if (held == null) {
			return;
		}
		attackTimer = 10;
		worldObj.setEntityState(this, (byte) 4);
		if (held.getItem() == ItemInit.mystic_branch) {
			if (worldObj.rand.nextBoolean()) {
				castSpell(target, distanceFactor, EntityReflection.SPELLS.next());
			}
		}
		else if (held.getItem() == ItemInit.crossbow_pistol) {
			EntityBolt entityArrow = new EntityBolt(worldObj, this);
			setHeading(entityArrow, this, target);
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, held);
			int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, held);
			entityArrow.setDamage(distanceFactor * 2.0f + rand.nextGaussian() * 0.25 + worldObj.getDifficulty().getDifficultyId() * 0.11f);
			if (i > 0) {
				entityArrow.setDamage(entityArrow.getDamage() + i * 0.5 + 0.5);
			}
			if (j > 0) {
				entityArrow.setKnockbackStrength(j);
			}
			if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, held) > 0 || (CreatureUtilities.isVampire(getAttackTarget())) && worldObj.rand.nextInt(3) == 0) {
				entityArrow.setFire(100);
			}
			if (getAttackTarget() != null) {
				if (CreatureUtilities.isWerewolf(getAttackTarget())) {
					entityArrow.setBoltType(4);
				}
				else if (CreatureUtilities.isUndead(getAttackTarget())) {
					entityArrow.setBoltType(3);
				}
				else if (worldObj.rand.nextInt(4) == 0) {
					entityArrow.setBoltType(2);
				}
				playSound(SoundEffect.RANDOM_BOW.event(), 1.0f, 1.0f / (getRNG().nextFloat() * 0.4f + 0.8f));
				worldObj.spawnEntityInWorld(entityArrow);
			}

		}
		else {
			EntityArrow entityArrow = new EntityTippedArrow(worldObj, this);
			setHeading(entityArrow, this, target);
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, held);
			int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, held);
			entityArrow.setDamage(distanceFactor * 2.0f + rand.nextGaussian() * 0.25 + worldObj.getDifficulty().getDifficultyId() * 0.11f);
			if (i > 0) {
				entityArrow.setDamage(entityArrow.getDamage() + i * 0.5 + 0.5);
			}
			if (j > 0) {
				entityArrow.setKnockbackStrength(j);
			}
			if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, held) > 0) {
				entityArrow.setFire(100);
			}

			playSound(SoundEffect.RANDOM_BOW.event(), 1.0f, 1.0f / (getRNG().nextFloat() * 0.4f + 0.8f));
			worldObj.spawnEntityInWorld(entityArrow);
		}
	}

	private void setHeading(Entity obj, Entity entity, Entity target) {
		double d0 = target.posX - this.posX;
		double d1 = target.getEntityBoundingBox().minY + target.height / 3.0F - obj.posY;
		double d2 = target.posZ - this.posZ;
		double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);

		if (obj instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow) obj;
			arrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, 14 - this.worldObj.getDifficulty().getDifficultyId() * 3);
		}
		else if (obj instanceof EntityBolt) {
			EntityBolt bolt = (EntityBolt) obj;
			bolt.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.worldObj.getDifficulty().getDifficultyId() * 4));
		}
	}

	private void castSpell(EntityLivingBase targetEntity, float par2, SymbolEffect spell) {
		double d0 = targetEntity.posX - posX;
		double d1 = targetEntity.getCollisionBoundingBox().minY + targetEntity.height / 0.2f - (posY + height / 2.0f);
		double d2 = targetEntity.posZ - posZ;
		float f1 = MathHelper.sqrt_float(par2) * 0.5f;
		if (!worldObj.isRemote) {
			worldObj.playEvent(null, 1009, new BlockPos(posX, posY, posZ), 0);
			int count = rand.nextInt(10) == 0 ? 9 : 3;
			EntitySpellEffect effect = new EntitySpellEffect(worldObj, this, d0 + rand.nextGaussian() * f1, d1, d3 + rand.nextGaussian() * f1);
			effect.posX = posX;
			effect.posY = posY + height / 2.0f;
			effect.posZ = posZ;
			worldObj.spawnEntityInWorld(effect);
			effect.setShooter(this);
		}
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		entityDropItem(ItemInit.demon_heart.createStack(), 0.0f);
	}

	private enum Task {
		NONE, MELEE, RANGED;
	}

	@Override
	public String getName() {
		if (hasCustomName()) {
			return getCustomNameTag();
		}
		String owner = getOwnerEntity().getDisplayNameString();
		return (owner == null || owner.isEmpty()) ? I18n.format("entity.reflection.name") : owner;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getLocationSkin() {
		if (locationSkin == null || !lastSkinOwner.equals(getOwnerEntity().getUniqueID())) {
			setupCustomSkin();
		}
		if (locationSkin != null) {
			return locationSkin;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void setupCustomSkin() {
		String ownerName = getOwnerEntity().getName();
		if (ownerName != null && !ownerName.isEmpty()) {
			locationSkin = AbstractClientPlayer.getLocationSkin(ownerName);
			downloadImageSkin = getDownloadImageSkin(locationSkin, ownerName);
			lastSkinOwner = getOwnerEntity().getUniqueID();
		}
		else {
			locationSkin = null;
			downloadImageSkin = null;
			lastSkinOwner = Reference.BLANK_UUID;
		}
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		freeSpawn = true;
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setString("Owner", getOwnerEntity().getUniqueID().toString());
		compound.setString("OwnerSkin", getOwnerSkin().toString());
		compound.setInteger("Model", getModel());
		compound.setBoolean("FreeSpawn", freeSpawn);
		compound.setBoolean("Vampire", isVampire);
		compound.setInteger("LivingTicks", livingTicks);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		setOwner(UUID.fromString(compound.getString("Owner")));
		setOwnerSkin(UUID.fromString(compound.getString("OwnerSkin")));
		freeSpawn = compound.getBoolean("FreeSpawn");
		livingTicks = compound.getInteger("LivingTicks");
		isVampire = compound.getBoolean("Vampire");
		setModel(compound.getInteger("Model"));
	}

	static {
		SPELLS = createSpells();
	}
}
