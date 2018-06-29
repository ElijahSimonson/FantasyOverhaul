package nz.castorgaming.fantasyoverhaul.objects.armor.specialArmors.vampire;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.objects.armor.ArmorBase;

public class VampireClothes extends ArmorBase implements ISpecialArmor {

	public static boolean isDrinkBoostActive(EntityLivingBase entity) {
		return numLightPiecesWorn(entity, true) >= 2;
	}

	public static boolean isFlameProtectionActive(EntityLivingBase entity) {
		return numLightPiecesWorn(entity, true) >= 3 || numLightPiecesWorn(entity, true) >= 2;
	}

	public static boolean isMemorizseBoostActive(EntityLivingBase entity) {
		return numLightPiecesWorn(entity, true) >= 3;
	}

	public static int numLightPiecesWorn(EntityLivingBase entity, boolean light) {
		int pieces = 0;
		Iterable<ItemStack> armor = entity.getArmorInventoryList();
		for (ItemStack item : armor) {
			if (item != null && item.getItem() instanceof VampireClothes && light) {
				pieces++;
			}
		}

		return pieces;
	}

	boolean metal;
	boolean female;

	private int realDamageReduction;

	private EntityEquipmentSlot armorSlot;

	@SideOnly(Side.CLIENT)
	private ModelVampireArmor modelClothesChest;

	@SideOnly(Side.CLIENT)
	private ModelVampireArmor modelClothesLegs;

	public VampireClothes(String name, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, boolean metal,
			boolean female) {
		super(name, metal ? ArmorMaterial.IRON : ArmorMaterial.LEATHER, renderIndexIn, equipmentSlotIn);
		this.female = female;
		this.metal = metal;
		realDamageReduction = metal ? ArmorMaterial.IRON.getDamageReductionAmount(equipmentSlotIn)
				: ArmorMaterial.LEATHER.getDamageReductionAmount(equipmentSlotIn);
		setArmorSlot(equipmentSlotIn);
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		stack.damageItem(damage, entity);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return realDamageReduction;
	}

	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, EntityEquipmentSlot armorSlot) {
		if (modelClothesChest == null) {
			modelClothesChest = new ModelVampireArmor(0.3F, false, female, metal);
		}
		if (modelClothesLegs == null) {
			modelClothesLegs = new ModelVampireArmor(0.02F, true, female, metal);
		}

		ModelBiped armorModel = null;

		if (stack != null && stack.getItem() instanceof VampireClothes) {
			EntityEquipmentSlot type = armorSlot;
			if (type == EntityEquipmentSlot.LEGS) {
				armorModel = modelClothesLegs;
			} else {
				armorModel = modelClothesChest;
			}
		}

		if (armorModel != null) {
			boolean isVisible = true;
			if (entityLiving != null && entityLiving.isInvisible()) {
				String entityTypeName = entityLiving.getClass().getSimpleName();
				isVisible = entityTypeName == null || entityTypeName.isEmpty()
						|| entityTypeName.equals("Abstract Steve");
			}

			armorModel.bipedHead.showModel = isVisible && armorSlot == EntityEquipmentSlot.HEAD;
			armorModel.bipedHeadwear.showModel = isVisible && armorSlot == EntityEquipmentSlot.HEAD;
			armorModel.bipedBody.showModel = isVisible && armorSlot == EntityEquipmentSlot.CHEST
					|| armorSlot == EntityEquipmentSlot.LEGS;
			armorModel.bipedRightArm.showModel = isVisible && armorSlot == EntityEquipmentSlot.CHEST;
			armorModel.bipedLeftArm.showModel = isVisible && armorSlot == EntityEquipmentSlot.CHEST;
			armorModel.bipedRightLeg.showModel = isVisible && armorSlot == EntityEquipmentSlot.FEET
					|| armorSlot == EntityEquipmentSlot.LEGS;
			armorModel.bipedLeftLeg.showModel = isVisible && armorSlot == EntityEquipmentSlot.FEET
					|| armorSlot == EntityEquipmentSlot.LEGS;

			armorModel.isSneak = entityLiving.isSneaking();
			armorModel.isRiding = entityLiving.isRiding();
			armorModel.isChild = entityLiving.isChild();

			ItemStack heldStack = entityLiving.getHeldItemMainhand();
			armorModel.rightArmPose = heldStack != null ? ArmPose.ITEM : ArmPose.EMPTY;

			if (entityLiving instanceof EntityPlayer && heldStack != null
					&& ((EntityPlayer) entityLiving).getActiveItemStack().getMaxItemUseDuration() > 0) {

				EnumAction enumaction = heldStack.getItemUseAction();

				if (enumaction == EnumAction.BLOCK) {
					armorModel.rightArmPose = ArmPose.BOW_AND_ARROW;
				}
			}
			return armorModel;
		}
		return null;
	}

	public EntityEquipmentSlot getArmorSlot() {
		return armorSlot;
	}

	@Override
	public int getColor(ItemStack stack) {
		if (!hasColor(stack)) {
			return super.getColor(stack);
		}

		int color = super.getColor(stack);
		if (color == 10511680) {
			color = 13369344;
		}
		return color;
	}

	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int par2) {
		return super.getColor(stack);
	}

	@Override
	public int getItemEnchantability() {
		return ArmorMaterial.GOLD.getEnchantability();
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage,
			int slot) {
		return new ArmorProperties(0, realDamageReduction / 25.00, armor.getMaxDamage() + 1 - armor.getMetadata());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.COMMON;
	}

	public boolean hasColor() {
		return true;
	}

	public static boolean isExtendedFlameProtectionActive(EntityLivingBase entity) {
		return numLightPiecesWorn(entity, true) >= 4;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
	}

	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public void setArmorSlot(EntityEquipmentSlot armorSlot) {
		this.armorSlot = armorSlot;
	}

}
