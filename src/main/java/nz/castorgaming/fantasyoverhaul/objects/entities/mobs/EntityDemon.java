package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.worlds.WorldProviderDreamWorld;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.classes.ai.EntityAIAttackCloseTargetOnCollide;
import nz.castorgaming.fantasyoverhaul.util.classes.ai.EntityAIDemonicBarginPlayer;
import nz.castorgaming.fantasyoverhaul.util.classes.ai.EntityAILookAtDemonicBarginPlayer;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class EntityDemon extends EntityGolem implements IRangedAttackMob, IMerchant {

	DataParameter<Boolean> PLAYER_CREATED = EntityDataManager.createKey(EntityDemon.class, DataSerializers.BOOLEAN);

	private int attackTimer;
	private EntityPlayer buyingPlayer;
	private MerchantRecipeList buyingList;
	private int tryEscape;

	public EntityDemon(World worldIn) {
		super(worldIn);
		this.tryEscape = -1;
		this.setSize(1.0f, 2.9f);
		this.isImmuneToFire = true;
		this.tasks.addTask(1, new EntityAIAttackCloseTargetOnCollide(this, 1.0, true, 3.0));
		this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0, 20, 60, 15.0f));
		this.tasks.addTask(3, new EntityAIDemonicBarginPlayer(this));
		this.tasks.addTask(4, new EntityAILookAtDemonicBarginPlayer(this));
		this.tasks.addTask(5, new EntityAIMoveTowardsTarget(this, 0.9, 32.0f));
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 0.6, true));
		this.tasks.addTask(7, new EntityAIMoveTowardsRestriction(this, 1.0));
		this.tasks.addTask(8, new EntityAIWander(this, 0.6));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
		this.tasks.addTask(10, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, true));
		this.experienceValue = 10;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(PLAYER_CREATED, false);
	}

	public boolean isPlayerCreated() {
		return dataManager.get(PLAYER_CREATED);
	}

	public void setPlayerCreated(boolean created) {
		enablePersistence();
		dataManager.set(PLAYER_CREATED, created);
	}

	@Override
	public void setCustomer(EntityPlayer player) {
		buyingPlayer = player;
	}

	@Override
	public EntityPlayer getCustomer() {
		return buyingPlayer;
	}

	public boolean isTrading() {
		return buyingPlayer != null;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player) {
		if (buyingList == null) {
			addDefaultEquipmentAndRecipies(rand.nextInt(4) + 6);
		}
		if (getCustomer() != null && getCustomer().getHeldItemMainhand() != null && getCustomer().getHeldItemMainhand().getItem() == ItemInit.DEVIL_TONGUE_CHARM) {
			MerchantRecipeList list = new MerchantRecipeList();
			for (MerchantRecipe recipe : buyingList) {
				NBTTagCompound nbtTag = recipe.writeToTags();
				MerchantRecipe newRec = new MerchantRecipe(nbtTag);
				ItemStack cost = newRec.getItemToBuy();
				cost.stackSize = Math.max(cost.stackSize - ((cost.getItem() == Items.GOLD_INGOT) ? 5 : ((cost.getItem() == Items.EMERALD) ? 2 : (cost.getItem() == Items.DIAMOND) ? 0 : 1)), 1);
				list.add(newRec);
			}
			return list;
		}
		return buyingList;

	}

	@Override
	public void setRecipes(MerchantRecipeList recipeList) {
	}

	@Override
	public void useRecipe(MerchantRecipe recipe) {
		recipe.incrementToolUses();
		Item itemToBuy = recipe.getItemToBuy().getItem();
		if (!worldObj.isRemote && (itemToBuy == Items.MAGMA_CREAM || itemToBuy == Items.BLAZE_ROD)) {
			playSound(SoundEvents.ENTITY_WITHER_SHOOT, getSoundVolume(), getSoundPitch());
			tryEscape = 50;
		}
		else {
			playSound(SoundEffect.RANDOM_BREATH.event(), getSoundVolume(), getSoundPitch());
		}
		if (getCustomer() != null && getCustomer().getHeldItemMainhand() != null && getCustomer().getHeldItemMainhand().getItem() == ItemInit.DEVILS_TONGUE_CHARM) {
			getCustomer().getHeldItemMainhand().damageItem(5, getCustomer());
			if (getCustomer().getHeldItemMainhand().stackSize <= 0) {
				getCustomer().setHeldItem(EnumHand.MAIN_HAND, null);
			}
		}
	}

	@Override
	public void verifySellingItem(ItemStack stack) {
		if (!worldObj.isRemote && livingSoundTime > -getTalkInterval() + 20) {
			livingSoundTime = -getTalkInterval();
			if (stack != null) {
				playSound(SoundEffect.RANDOM_BREATH.event(), getSoundVolume(), getSoundPitch());
			}
			else {
				playSound(SoundEvents.ENTITY_WITHER_AMBIENT, getSoundVolume(), getSoundPitch());
			}
		}
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		if (target.getHeldItemMainhand() == null || target.getHeldItemMainhand() != ItemInit.DEVILS_TONGUE_CHARM || worldObj.rand.nextDouble() < 0.05) {
			double dx = target.posX - posX;
			double dy = target.getEntityBoundingBox().minY + target.height / 2.0f - (posY + height / 2.0f);
			double dz = target.posZ - posZ;
			float f1 = MathHelper.sqrt_float(distanceFactor) * 0.5f;
			EntityLargeFireball fireball = new EntityLargeFireball(worldObj, this, dx + rand.nextGaussian() * f1, dy, dz + rand.nextGaussian() * f1);
			Vec3d vec = getLook(1.0f);
			fireball.posX = posX + vec.xCoord * 1.0;
			fireball.posY = posY + height / 2.0f + 0.5;
			fireball.posZ = posZ + vec.zCoord * 1.0;
			if (!worldObj.isRemote) {
				worldObj.playEvent(1009, new BlockPos(posX, posY, posZ), 0);
				worldObj.spawnEntityInWorld(fireball);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return super.attackEntityFrom(source, Math.min(amount, 15.0f));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		attackTimer = 10;
		worldObj.setEntityState(this, (byte) 4);
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + rand.nextInt(15));
		if (flag) {
			entityIn.motionY += 0.4;
		}
		playSound(SoundEffect.GOLEM_THROW.event(), 1.0f, 1.0f);
		return flag;
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
	protected SoundEvent getAmbientSound() {
		return SoundEffect.BLAZE_BREATH.event();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEffect.MOB_WITHER_DEATH.event();
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEffect.MOB_WITHER_HURT.event();
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.0f, 1.0f);
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		if (wasRecentlyHit) {
			dropItem(Items.MAGMA_CREAM, (rand.nextInt(2 + lootingModifier)));
		}
	}

	@Override
	protected Item getDropItem() {
		return Items.MAGMA_CREAM;
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if (dimension == Config.instance().dimensionDreamID) {
			return super.processInteract(player, hand, stack);
		}
		boolean flag = stack != null && (stack.getItem() == Items.SPAWN_EGG || stack.getItem() == Items.NAME_TAG);
		if (!flag && isEntityAlive() && !isTrading() && !isChild()) {
			if (!worldObj.isRemote) {
				setCustomer(player);
				player.displayVillagerTradeGui(this);
			}
			return true;
		}
		return super.processInteract(player, hand, stack);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, Integer.MAX_VALUE, 4));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (attackTimer > 0) {
			--attackTimer;
		}
		if (dimension == Config.instance().dimensionDreamID && worldObj.provider instanceof WorldProviderDreamWorld && !((WorldProviderDreamWorld) worldObj.provider).isDemonicNightmare()) {
			setDead();
		}
		if (tryEscape == 0) {
			tryEscape = -1;
			worldObj.createExplosion(this, posX, posY, posZ, 3.0f, true);
		}
		else if (tryEscape > 0) {
			--tryEscape;
		}
		if (motionX * motionX + motionZ * motionZ > 2.5E-7 && rand.nextInt(5) == 0) {
			int i = MathHelper.floor_double(posX);
			int j = MathHelper.floor_double(posY - 0.2 - this.getYOffset());
			int k = MathHelper.floor_double(posZ);
			Block l = worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();
			if (l != Blocks.AIR) {
				worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (this.rand.nextFloat() - 0.5) * this.width, getEntityBoundingBox().minY + 0.1,
						this.posZ + (this.rand.nextFloat() - 0.5) * this.width, 4.0 * (this.rand.nextFloat() - 0.5), 0.5, (this.rand.nextFloat() - 0.5) * 4.0);
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("PlayerCreated", isPlayerCreated());
		if (buyingList != null) {
			compound.setTag("Bargins", buyingList.getRecipiesAsTags());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setPlayerCreated(compound.getBoolean("PlayerCreated"));
		if (compound.hasKey("Bargins")) {
			NBTTagCompound bargins = compound.getCompoundTag("Bargins");
			buyingList = new MerchantRecipeList(bargins);
		}
	}

	private Item getCurrency() {
		double chance = rand.nextDouble();
		if (chance < 0.2) {
			return Items.BLAZE_ROD;
		}
		if (chance < 0.4) {
			return Items.MAGMA_CREAM;
		}
		if (chance < 0.5) {
			return Items.DIAMOND;
		}
		if (chance < 0.75) {
			return Items.EMERALD;
		}
		return Items.GOLD_INGOT;
	}

	private ItemStack getPrice(int basePriceInEmeralds) {
		Item currency = getCurrency();
		int multi = (currency == Items.GOLD_INGOT) ? 1 : ((currency == Items.EMERALD) ? 3 : ((currency == Items.DIAMOND) ? 5 : 4));
		int quantity = Math.max(1, basePriceInEmeralds / multi);
		return new ItemStack(currency, quantity);
	}

	private void addDefaultEquipmentAndRecipies(int par1) {
		MerchantRecipeList merchantList = new MerchantRecipeList();
		int STOCK_REDUCTION = -5;
		for (int i = 0; i < par1; i++) {
			Enchantment enchant = Enchantment.REGISTRY.getObjectById(rand.nextInt(Enchantment.REGISTRY.getKeys().size()));
			int k = MathHelper.getRandomIntegerInRange(rand, enchant.getMinLevel(), enchant.getMaxLevel());
			ItemStack itemstack = Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(enchant, k));
			int j = 2 + rand.nextInt(5 + k * 10) + 3 * k;
			MerchantRecipe recipe = new MerchantRecipe(getPrice(j), itemstack);
			recipe.increaseMaxTradeUses(STOCK_REDUCTION);
			merchantList.add(recipe);
		}
		if (rand.nextDouble() < 0.25) {
			MerchantRecipe recipe1 = new MerchantRecipe(getPrice(rand.nextInt(3) + 8), ItemInit.SPECTRAL_DUST.createStack(rand.nextInt(4) + 3));
			recipe1.increaseMaxTradeUses(STOCK_REDUCTION);
			merchantList.add(recipe1);
		}
		if (rand.nextDouble() < 0.25) {
			MerchantRecipe recipe1 = new MerchantRecipe(getPrice(rand.nextInt(3) + 8), ItemInit.DOG_TONGUE.createStack(rand.nextInt(4) + 4));
			recipe1.increaseMaxTradeUses(STOCK_REDUCTION);
			merchantList.add(recipe1);
		}
		if (rand.nextDouble() < 0.15) {
			MerchantRecipe recipe1 = new MerchantRecipe(getPrice(rand.nextInt(3) + 8), ItemInit.REDSTONE_SOUP.createStack(1));
			recipe1.increaseMaxTradeUses(STOCK_REDUCTION);
			merchantList.add(recipe1);
		}
		if (rand.nextDouble() < 0.15) {
			MerchantRecipe recipe1 = new MerchantRecipe(new ItemStack(Items.DIAMOND), new ItemStack(Items.GHAST_TEAR, 2));
			recipe1.increaseMaxTradeUses(STOCK_REDUCTION);
			merchantList.add(recipe1);
		}
		if (rand.nextDouble() < 0.15) {
			MerchantRecipe recipe1 = new MerchantRecipe(new ItemStack(Items.DIAMOND), new ItemStack(Items.ENDER_PEARL, 2));
			recipe1.increaseMaxTradeUses(STOCK_REDUCTION);
			merchantList.add(recipe1);
		}
		Collections.shuffle(merchantList);
		Item currencyForHeart = getCurrency();
		MerchantRecipe heart = new MerchantRecipe(new ItemStack(currencyForHeart, (currencyForHeart == Items.GOLD_INGOT) ? 30 : 3), ItemInit.DEMON_HEART.createStack(1));
		heart.increaseMaxTradeUses(STOCK_REDUCTION);
		merchantList.add(rand.nextInt(3), heart);
		if (buyingList == null) {
			buyingList = new MerchantRecipeList();
		}
		for (int index = 0; index < merchantList.size(); index++) {
			buyingList.add(merchantList.get(index));
		}
	}
}
