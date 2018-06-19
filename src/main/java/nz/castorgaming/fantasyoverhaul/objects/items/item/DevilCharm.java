package nz.castorgaming.fantasyoverhaul.objects.items.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityDemon;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.powers.infusions.player.InfusionOtherwhere;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class DevilCharm extends GeneralItem {
	
	private boolean charmDemons;

	public DevilCharm(String name, boolean charmsDevils) {
		super(name);
		charmDemons = charmsDevils;		
		setMaxStackSize(1);
		setMaxDamage(50);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (!worldIn.isRemote) {
			boolean success = false;
			double MAX_TARGET_RANGE = 5.0;
			RayTraceResult rtr = InfusionOtherwhere.doCustomRayTrace(worldIn, playerIn, true, MAX_TARGET_RANGE);
			if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.ENTITY && rtr.entityHit instanceof EntityLiving) {
				EntityLiving living = (EntityLiving) rtr.entityHit;
				if (
						(living instanceof EntityAnimal || living instanceof EntityAmbientCreature || living instanceof EntitySpider || living instanceof EntityWaterMob || 
								(living instanceof EntityCreeper && ItemInit.WITCHES_ROBES.isRobeWorn(playerIn)) 
								|| 
								(living.isEntityUndead() && ItemInit.NECROMANCERS_ROBES.isRobeWorn(playerIn))
						) && !(living instanceof EntityFamiliar) && !(living instanceof EntityCovenWitch) && !(living instanceof EntityImp) && living.isEntityAlive() && !living.isChild() && living.getAttackTarget() == null && (!(living instanceof EntityBat) || canBatDrop(living))){
					AnimalMerchant merchant = new AnimalMerchant(living);
					merchant.playIntro(playerIn);
					merchant.setCustomer(playerIn);
					playerIn.displayVillagerTradeGui(merchant);
					success = true;
				}
			}
			if (!success || (rtr != null && rtr.entityHit instanceof EntityDemon)) {
				SoundEffect.NOTE_SNARE.playAtPlayer(worldIn, playerIn);
			}
			else {
				itemStackIn.damageItem(1, playerIn);
				if (itemStackIn.stackSize <= 0) {
					EntityEquipmentSlot slot = playerIn.getActiveHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
					playerIn.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
				}
			}
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}

	private boolean canBatDrop(EntityLiving living) {
		NBTTagCompound nbt = living.getEntityData();
		return nbt == null || !nbt.hasKey(Reference.NO_DROPS) || !nbt.getBoolean(Reference.NO_DROPS);
	}
	
	public boolean canCharmDemons() {
		return charmDemons;
	}
	
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
	
	public static boolean hasStockInventory(EntityLiving entity) {
		if (entity == null) {
			return false;
		}
		NBTTagCompound nbt = entity.getEntityData();
		return nbt != null && nbt.hasKey(Reference.SHOP_STOCK);
	}
	
	public static void setEmptyStockInventory(World world, EntityLiving entity) {
		if (entity != null && !world.isRemote) {
			NBTTagCompound nbt = entity.getEntityData();
			nbt.setTag(Reference.SHOP_STOCK, new NBTTagCompound());
		}
	}
	
	private static class AnimalMerchant implements IMerchant{

		private final EntityLiving animal;
		private EntityPlayer customer;
		private MerchantRecipeList currentList;
		
		public AnimalMerchant(EntityLiving living) {
			animal = living;
			currentList = null;
		}
		
		public void playIntro(EntityPlayer player) {
			playGreeting(animal, player);
		}
		
		@Override
		public void setCustomer(EntityPlayer player) {
			customer = player;
		}

		@Override
		public EntityPlayer getCustomer() {
			return customer;
		}

		@Override
		public MerchantRecipeList getRecipes(EntityPlayer player) {
			NBTTagCompound nbt = animal.getEntityData();
			if (currentList != null) {
				return currentList;
			}
			if (nbt.hasKey(Reference.SHOP_STOCK)) {
				NBTTagCompound stock = nbt.getCompoundTag(Reference.SHOP_STOCK);
				if (stock.hasNoTags()) {
					currentList = new MerchantRecipeList();
				}else {
					currentList = new MerchantRecipeList(stock);
				}
				return currentList;
			}
			currentList = new MerchantRecipeList();
			populateList(animal, currentList);
			nbt.setTag(Reference.SHOP_STOCK, currentList.getRecipiesAsTags());
			return currentList;
		}

		@Override
		public void setRecipes(MerchantRecipeList recipeList) {
		}

		@Override
		public void useRecipe(MerchantRecipe recipe) {
			if (animal != null && animal.isEntityAlive() && !animal.worldObj.isRemote) {
				recipe.incrementToolUses();
				if (currentList != null) {
					NBTTagCompound nbt = animal.getEntityData();
					nbt.setTag(Reference.SHOP_STOCK, currentList.getRecipiesAsTags());
				}
			}
			animal.playLivingSound();
		}

		@Override
		public void verifySellingItem(ItemStack stack) {
			animal.playLivingSound();
		}

		@Override
		public ITextComponent getDisplayName() {
			return animal.hasCustomName() ? new TextComponentString(animal.getCustomNameTag()) : animal.getDisplayName();
		}
		
		private static void populateList(EntityLiving animal, MerchantRecipeList finalList) {
			Random r = animal.worldObj.rand;
			MerchantRecipeList list = new MerchantRecipeList();
			ItemStack[] stacks = {ItemInit.MANDRAKE_ROOT.createStack(3), ItemInit.BELLADONNA.createStack(3), ItemInit.ARTICHOKE.createStack(3), new ItemStack(Blocks.SAPLING, 4, 0), new ItemStack(Blocks.SAPLING, 4, 1), new ItemStack(Blocks.SAPLING, 4, 2),new ItemStack(Blocks.SAPLING, 4, 3), new ItemStack(Blocks.CACTUS, 2), new ItemStack(Items.GOLD_NUGGET, 5), new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.BONE, 4), new ItemStack(Items.FLINT, 5), ItemInit.DOG_TONGUE.createStack(1), new ItemStack(Items.POTATO, 5), new ItemStack(Items.POISONOUS_POTATO, 2), new ItemStack(Items.CARROT, 5), new ItemStack(Items.CLAY_BALL, 10)};
			ArrayList<ItemStack> currencies = new ArrayList<ItemStack>();
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			items.add(stacks[r.nextInt(stacks.length)]);
			if (animal.worldObj.rand.nextDouble() < 0.03) {
				items.add(ItemInit.SEED_TREEFYD.createStack());
			}
			if (animal instanceof EntityPig) {
				currencies.add(new ItemStack(Items.CARROT));
				currencies.add(new ItemStack(Items.POTATO));
				currencies.add(new ItemStack(Items.APPLE));
				items.add(new ItemStack(Blocks.RED_MUSHROOM));
				items.add(new ItemStack(Blocks.BROWN_MUSHROOM));
				if (r.nextDouble() < 0.02) {
					items.add(new ItemStack(Items.EMERALD, 1));
				}
				if (r.nextDouble() < 0.01) {
					items.add(new ItemStack(Items.DIAMOND, 1));
				}
			}else if (animal instanceof EntityHorse) {
				 currencies.add(new ItemStack(Items.CARROT));
	             currencies.add(new ItemStack(Items.APPLE));
	             currencies.add(new ItemStack(Items.WHEAT));
	             if (r.nextDouble() < 0.01) {
	               	items.add(new ItemStack(Items.SADDLE, 1));
	             }
			}else if (animal instanceof EntityWolf) {
				currencies.add(new ItemStack(Items.BEEF));
                currencies.add(new ItemStack(Items.PORKCHOP));
                currencies.add(new ItemStack(Items.CHICKEN));
                items.add(new ItemStack(Items.BONE, 5));
                if (r.nextDouble() < 0.02) {
                    items.add(new ItemStack(Items.EMERALD, 1));
                }
                if (r.nextDouble() < 0.01) {
                    items.add(new ItemStack(Items.DIAMOND, 1));
                }
			}else if (animal instanceof EntityOcelot) {
				currencies.add(new ItemStack(Items.MILK_BUCKET));
                currencies.add(new ItemStack(Items.FISH));
			}else if (animal instanceof EntityCow) {
				currencies.add(new ItemStack(Items.WHEAT));
			}else if (animal instanceof EntityChicken) {
				currencies.add(new ItemStack(Items.WHEAT_SEEDS));
                items.add(new ItemStack(Items.FEATHER, 10));
                items.add(new ItemStack(Items.EGG, 5));
			}else if (animal instanceof EntityMooshroom) {
				currencies.add(new ItemStack((Block)Blocks.RED_MUSHROOM));
                currencies.add(new ItemStack((Block)Blocks.BROWN_MUSHROOM));
			}else if (animal instanceof EntitySheep) {
				currencies.add(new ItemStack(Items.WHEAT));
			}else if (animal instanceof EntitySquid) {
				currencies.add(new ItemStack(Items.FISH));
                items.add(new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getMetadata()));
			}else if (animal instanceof EntityBat) {
				currencies.add(new ItemStack(Items.WHEAT_SEEDS));
                currencies.add(new ItemStack(Items.WHEAT));
                currencies.add(new ItemStack(Items.BEEF));
                currencies.add(new ItemStack(Items.PORKCHOP));
                items.add(ItemInit.BAT_WOOL.createStack(5));
			}else if (animal instanceof EntitySpider) {
				currencies.add(new ItemStack(Items.BEEF));
                currencies.add(new ItemStack(Items.PORKCHOP));
                currencies.add(new ItemStack(Items.CHICKEN));
                currencies.add(new ItemStack(Items.FISH));
                items.add(new ItemStack(Items.STRING, 8));
                items.add(ItemInit.WEB.createStack(4));
			}else if (animal instanceof EntityCreeper) {
				currencies.add(new ItemStack(Items.GUNPOWDER));
                currencies.add(new ItemStack(Items.FISH));
                if (r.nextDouble() < 0.05) {
                    items.add(ItemInit.DUST_SPECTRAL.createStack(2));
                }
                if (animal.worldObj.rand.nextDouble() < 0.1) {
                    items.add(ItemInit.SEED_TREEFYD.createStack());
                }
                if (r.nextDouble() < 0.02) {
                    items.add(ItemInit.HEART_CREEPER.createStack(1));
                }
			}else if (animal.isEntityUndead()) {
				currencies.add(new ItemStack(Items.BONE));
                items.add(ItemInit.DUST_SPECTRAL.createStack(1));
			}else {
				currencies.add(new ItemStack(Items.BEEF));
                currencies.add(new ItemStack(Items.PORKCHOP));
                currencies.add(new ItemStack(Items.CHICKEN));
                currencies.add(new ItemStack(Items.FISH));
                currencies.add(new ItemStack(Items.WHEAT));
                currencies.add(new ItemStack(Items.WHEAT_SEEDS));
                currencies.add(new ItemStack(Items.CARROT));
                currencies.add(new ItemStack(Items.APPLE));
                currencies.add(new ItemStack(Items.POTATO));
			}
			for (ItemStack stack : items) {
				if (stack != null && stack.getItem() != null) {
					ItemStack goods = stack.copy();
					goods.stackSize = Math.min(r.nextInt(stack.stackSize) + ((stack.stackSize > 4) ? 3 : 1), goods.getMaxStackSize());
					ItemStack currency = currencies.get(r.nextInt(currencies.size()));
					ItemStack cost = currency.copy();
					int multiplier = 1;
					if (goods.getItem() == Items.DIAMOND || goods.getItem() == Items.EMERALD || goods.getItem() == Items.SADDLE || ItemInit.SEED_TREEFYD.isMatch(goods) || animal.isEntityUndead()) {
						multiplier = 2;
					}
					int factor = (goods.stackSize > 4) ? 1 : 2;
					cost.stackSize = Math.min(r.nextInt(2) + goods.stackSize * multiplier * (r.nextInt(2) + factor), currency.getMaxStackSize());
					MerchantRecipe recipe = new MerchantRecipe(cost, goods);
					recipe.increaseMaxTradeUses(0 - (6 - r.nextInt(2)));
					list.add(recipe);
				}
			}
			Collections.shuffle(list);
			for (int MAX_ITEMS = r.nextInt(2) + 1, i = 0; i < MAX_ITEMS && i < list.size(); i++) {
				finalList.add(list.get(i));
			}
		}
		
		private void playGreeting(EntityLiving animal, EntityPlayer player) {
			animal.playLivingSound();
			animal.playLivingSound();
			animal.playLivingSound();
		}
	}
}
