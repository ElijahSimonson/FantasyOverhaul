package nz.castorgaming.fantasyoverhaul.init;

import net.minecraft.inventory.EntityEquipmentSlot;
import nz.castorgaming.fantasyoverhaul.objects.armor.specialArmors.vampire.VampireClothes;
import nz.castorgaming.fantasyoverhaul.objects.items.general.GeneralItem;
import nz.castorgaming.fantasyoverhaul.objects.items.tools.HuntsmanSpear;

public class ItemInit {

	// Items

	// General

	public static final GeneralItem INFERNAL_BLOOD = new GeneralItem("infernal_blood");

	// Tools & Weapons
	public static final HuntsmanSpear HUNTSMAN_SPEAR = new HuntsmanSpear("huntsman_spear");

	// Armor

	// Vampire
	// Cloth
	public static final VampireClothes CLOTH_VAMPIRE_HELMET = new VampireClothes("cloth_vampire_helmet", 1,
			EntityEquipmentSlot.HEAD, false, false);
	public static final VampireClothes CLOTH_VAMPIRE_MALE_CHESTPLATE = new VampireClothes(
			"cloth_vampire_male_chestplate", 1, EntityEquipmentSlot.CHEST, false, false);
	public static final VampireClothes CLOTH_VAMPIRE_FEMALE_CHESTPLATE = new VampireClothes(
			"cloth_vampire_female_chestplate", 1, EntityEquipmentSlot.CHEST, false, true);
	public static final VampireClothes CLOTH_VAMPIRE_LEGGINGS = new VampireClothes("cloth_vampire_leggings", 1,
			EntityEquipmentSlot.LEGS, false, false);
	public static final VampireClothes CLOTH_VAMPIRE_BOOTS = new VampireClothes("cloth_vampire_boots", 1,
			EntityEquipmentSlot.FEET, false, false);
	// Metal
	public static final VampireClothes HELMET_VAMPIRE = new VampireClothes("chain_vampire_helmet", 1,
			EntityEquipmentSlot.HEAD, true, false);
	public static final VampireClothes CHAIN_VAMPIRE_MALE_CHESTPLATE = new VampireClothes(
			"chain_vampire_male_chestplate", 1, EntityEquipmentSlot.CHEST, true, false);
	public static final VampireClothes CHAIN_VAMPIRE_FEMALE_CHESTPLATE = new VampireClothes(
			"chain_vampire_female_chestplate", 1, EntityEquipmentSlot.CHEST, true, true);
	public static final VampireClothes CHAIN_VAMPIRE_LEGGINGS = new VampireClothes("chain_vampire_leggings", 1,
			EntityEquipmentSlot.LEGS, true, false);
}
