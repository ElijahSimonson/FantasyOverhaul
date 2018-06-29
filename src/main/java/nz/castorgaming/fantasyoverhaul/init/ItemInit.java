package nz.castorgaming.fantasyoverhaul.init;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.potion.PotionEffect;
import nz.castorgaming.fantasyoverhaul.objects.armor.specialArmors.vampire.VampireClothes;
import nz.castorgaming.fantasyoverhaul.objects.items.brew.BrewGrotesque;
import nz.castorgaming.fantasyoverhaul.objects.items.brew.BrewRevealing;
import nz.castorgaming.fantasyoverhaul.objects.items.brew.BrewSleeping;
import nz.castorgaming.fantasyoverhaul.objects.items.brew.base.BrewFluid;
import nz.castorgaming.fantasyoverhaul.objects.items.brew.base.BrewInfused;
import nz.castorgaming.fantasyoverhaul.objects.items.brew.base.BrewSolid;
import nz.castorgaming.fantasyoverhaul.objects.items.brew.base.BrewSoul;
import nz.castorgaming.fantasyoverhaul.objects.items.contract.ContractBlaze;
import nz.castorgaming.fantasyoverhaul.objects.items.contract.ContractEvaporate;
import nz.castorgaming.fantasyoverhaul.objects.items.contract.ContractFiery;
import nz.castorgaming.fantasyoverhaul.objects.items.contract.ContractFireResist;
import nz.castorgaming.fantasyoverhaul.objects.items.contract.ContractSmelting;
import nz.castorgaming.fantasyoverhaul.objects.items.dreamweave.DreamWeave;
import nz.castorgaming.fantasyoverhaul.objects.items.item.AnnointingPaste;
import nz.castorgaming.fantasyoverhaul.objects.items.item.BinkySkull;
import nz.castorgaming.fantasyoverhaul.objects.items.item.BoneNeedle;
import nz.castorgaming.fantasyoverhaul.objects.items.item.BookVampire;
import nz.castorgaming.fantasyoverhaul.objects.items.item.BroomEnchanted;
import nz.castorgaming.fantasyoverhaul.objects.items.item.CreeperHeart;
import nz.castorgaming.fantasyoverhaul.objects.items.item.DemonHeart;
import nz.castorgaming.fantasyoverhaul.objects.items.item.FrozenHeart;
import nz.castorgaming.fantasyoverhaul.objects.items.item.InfernalBlood;
import nz.castorgaming.fantasyoverhaul.objects.items.item.ItemArtichoke;
import nz.castorgaming.fantasyoverhaul.objects.items.item.ItemBatBall;
import nz.castorgaming.fantasyoverhaul.objects.items.item.ItemChalice;
import nz.castorgaming.fantasyoverhaul.objects.items.item.ItemMutandis;
import nz.castorgaming.fantasyoverhaul.objects.items.item.ItemNecroStone;
import nz.castorgaming.fantasyoverhaul.objects.items.item.KobolditePentacle;
import nz.castorgaming.fantasyoverhaul.objects.items.item.SeerStone;
import nz.castorgaming.fantasyoverhaul.objects.items.item.SubduedSpirit;
import nz.castorgaming.fantasyoverhaul.objects.items.item.Waystone;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Bolt;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Contract;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Drinkable;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Edible;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItemEnchanted;
import nz.castorgaming.fantasyoverhaul.objects.items.seeds.TreefydSeed;
import nz.castorgaming.fantasyoverhaul.objects.items.tools.HuntsmanSpear;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;

public final class ItemInit {

	// Items

	// General
	public static HashMap<String, DreamWeave> WEAVES = new HashMap<String, DreamWeave>();

	public static final GeneralItem CLAY_JAR_SOFT = new GeneralItem("clay_jar_soft");
	public static final GeneralItem CLAY_JAR_EMPTY = new GeneralItem("clay_jar_empty");
	public static final Waystone WAYSTONE = new Waystone("waystone", false);
	public static final Waystone WAYSTONE_BOUND = (Waystone) new Waystone("waystone_bound", true).setCreativeTab(null);
	public static final GeneralItem DOOR_KEY = new GeneralItem("door_key");
	public static final GeneralItem DOOR_KEYRING = new GeneralItem("door_keyring");
	public static final GeneralItem ROCK = new GeneralItem("rock");
	public static final GeneralItem WEB = new GeneralItem("web");
	public static final BoneNeedle NEEDLE_BONE = new BoneNeedle("needle_bone");
	public static final GeneralItem NEEDLE_ICY = new GeneralItem("needle_icy");
	public static final ItemNecroStone NECROSTONE = new ItemNecroStone("necrostone");
	public static final GeneralItem QUICKLIME = new GeneralItem("quicklime");
	public static final GeneralItem GYPSUM = new GeneralItem("gypsum");
	public static final GeneralItem WOOD_ASH = new GeneralItem("wood_ash");
	public static final GeneralItem DUST_SPECTRAL = new GeneralItem("dust_spectral");
	public static final GeneralItem ENDER_DEW = new GeneralItem("ender_dew");
	public static final GeneralItem LEATHER_IMPREGNATED = new GeneralItem("leather_impregnated");
	public static final GeneralItem ATTUNED_STONE = new GeneralItem("attuned_stone");
	public static final GeneralItemEnchanted ATTUNED_STONE_CHARGED = new GeneralItemEnchanted("attunded_stone_charged");
	public static final ItemBatBall BAT_BALL = new ItemBatBall("bat_ball");
	public static final GeneralItem SPHERE_QUARTZ = new GeneralItem("sphere_quartz");
	public static final GeneralItem THREAD_FANCIFUL = new GeneralItem("thread_fanciful");
	public static final GeneralItem THREAD_TORMENTED = new GeneralItem("thread_tormented");
	public static final GeneralItem THREAD_GOLDEN = new GeneralItem("thread_golden");
	public static final GeneralItem CHARM_DISRUPTED_DREAMS = new GeneralItem("charm_disrupted_dreams");
	public static final GeneralItem DUST_GRAVEYARD = new GeneralItem("dust_graveyard");
	public static final GeneralItem NULL_CATALYST = new GeneralItem("null_catalyst");
	public static final GeneralItem NULLIFIED_LEATHER = new GeneralItem("null_leather");
	public static final GeneralItem DUST_KOBOLD = new GeneralItem("dust_kobold");
	public static final GeneralItem INGOT_KOBOLD = new GeneralItem("item_kobold");
	public static final GeneralItem NUGGET_KOBOLD = new GeneralItem("nugget_kobold");
	public static final KobolditePentacle KOBOLD_PENTACLE = new KobolditePentacle("kobold_pentacle");
	public static final GeneralItem WAYSTONE_BOUND_PLAYER = new GeneralItem("waystone_bound_player");
	public static final AnnointingPaste ANNOINTING_PASTE = new AnnointingPaste("annointing_paste");
	public static final GeneralItem DUST_SILVER = new GeneralItem("dust_silver");
	public static final GeneralItem CLOTH_DARK = new GeneralItem("cloth_dark");
	public static final GeneralItem STAKE = new GeneralItem("stake");
	public static final ItemChalice CHALICE = new ItemChalice("chalice");

	public static final FrozenHeart HEART_ICY = new FrozenHeart("heart_frozen", EnumAction.EAT,
			new PotionEffect[] { new PotionEffect(MobEffects.FIRE_RESISTANCE, 20) });

	// Plants
	public static final ItemMutandis MUTANDIS = new ItemMutandis("mutandis", false);
	public static final ItemMutandis MUTANDIS_EXTREME = new ItemMutandis("mutandis_extreme", true);

	public static final GeneralItem BELLADONNA = new GeneralItem("belladona_flower");
	public static final GeneralItem MANDRAKE_ROOT = new GeneralItem("mandrake_root");
	public static final ItemArtichoke ARTICHOKE = new ItemArtichoke("artichoke", 20, 0.0f, false);
	public static final TreefydSeed SEED_TREEFYD = new TreefydSeed("seed_treefyd");
	public static final GeneralItem COTTON_DISTURBED = new GeneralItem("cotton_disturbed");
	public static final GeneralItem WORMWOOD = new GeneralItem("wormwood");
	public static final GeneralItem WOLFSBANE = new GeneralItem("wolfsbane");

	// Drops
	public static final GeneralItem BAT_WOOL = new GeneralItem("bat_wool");
	public static final GeneralItem DOG_TONGUE = new GeneralItem("dog_tongue");
	public static final DemonHeart HEART_DEMON = new DemonHeart("heart_demon",
			new PotionEffect[] { new PotionEffect(MobEffects.HEALTH_BOOST, 2400, 4),
					new PotionEffect(MobEffects.REGENERATION, 2400, 1), new PotionEffect(MobEffects.STRENGTH, 2400, 2),
					new PotionEffect(MobEffects.SPEED, 2400, 2), new PotionEffect(MobEffects.FIRE_RESISTANCE, 2400, 2),
					new PotionEffect(MobEffects.NAUSEA, 2400), new PotionEffect(MobEffects.HUNGER, 3600, 1) });
	public static final CreeperHeart HEART_CREEPER = new CreeperHeart("heart_creeper", EnumAction.EAT,
			new PotionEffect[] { new PotionEffect(MobEffects.FIRE_RESISTANCE, 20, 0) });
	public static final GeneralItem ENT_BRANCH = new GeneralItem("ent_branch");
	public static final GeneralItem OWLET_WING = new GeneralItem("owlet_wing");
	public static final GeneralItem FROG_TOE = new GeneralItem("frog_toe");
	public static final GeneralItem MELLIFLUOUS_HUNGER = new GeneralItem("mellifluous_hunger");
	public static final SubduedSpirit SPIRIT_SUBDUED = new SubduedSpirit("subdued_spirit");
	public static final GeneralItem HEART_GOLD = new GeneralItem("heart_gold");
	public static final BinkySkull BINKY_SKULL = new BinkySkull("death_horse_skull");

	// Foods
	public static final Edible ODD_PORK_RAW = new Edible("odd_pork_raw", 3, 0.3f, true);
	public static final Edible ODD_PORK_COOKED = new Edible("odd_pork_cooked", 8, 0.8f, false);
	public static final Edible BERRIES_ROWAN = new Edible("berries_rowan", 1, 0.6f, false);
	public static final Edible APPLE_SLEEPING = new Edible("apple_sleeping", 3, 3.0f, false, true);
	public static final Drinkable APPLE_WORMY = new Drinkable("apple_wormy", EnumAction.EAT,
			new PotionEffect[] { new PotionEffect(MobEffects.POISON, 60) });

	// Fumes & Brews
	public static final GeneralItem FOUL_FUME = new GeneralItem("fume_foul");
	public static final GeneralItem DIAMOND_VAPOUR = new GeneralItem("fume_diamond");
	public static final GeneralItem VITRIOL_OIL = new GeneralItem("fume_oil_vitriol");
	public static final GeneralItem HORNED_EXHALE = new GeneralItem("fume_horned_exhale");
	public static final GeneralItem GODDESS_BREATH = new GeneralItem("fume_goddess_breath");
	public static final GeneralItem HINT_REBIRTH = new GeneralItem("fume_hint_rebirth");
	public static final GeneralItem MAGIC_WHIFF = new GeneralItem("fume_magic_whiff");
	public static final GeneralItem REEK_MISFORTUNE = new GeneralItem("fume_reek_misfortune");
	public static final GeneralItem ODUR_PURITY = new GeneralItem("fume_odur_purity");
	public static final GeneralItem GODDESS_TEAR = new GeneralItem("fume_goddess_tear");
	public static final GeneralItem REFINED_EVIL = new GeneralItem("fume_refined_evil");
	public static final GeneralItem DROP_LUCK = new GeneralItem("fume_drop_luck");
	public static final InfernalBlood INFERNAL_BLOOD = new InfernalBlood("fume_infernal_blood");
	public static final Drinkable MILK_PURIFIED = new Drinkable("milk_purified");

	public static final Drinkable SOUP_REDSTONE = new Drinkable("soup_redstone",
			new PotionEffect[] { new PotionEffect(MobEffects.HEALTH_BOOST, 2400, 1) });
	public static final Drinkable OINTMENT_FLYING = new Drinkable("ointment_flying",
			new PotionEffect[] { new PotionEffect(MobEffects.POISON, 1200, 2) });
	public static final Drinkable GHOST_LIGHT = new Drinkable("ghost_light",
			new PotionEffect[] { new PotionEffect(MobEffects.POISON, 1200, 1) });
	public static final Drinkable WORLD_SOUL = new Drinkable("world_soul",
			new PotionEffect[] { new PotionEffect(MobEffects.POISON, 1200, 1) });
	public static final Drinkable OTHERWHERE_SPIRIT = new Drinkable("otherwhere_spirit",
			new PotionEffect[] { new PotionEffect(MobEffects.POISON, 1200, 1) });
	public static final Drinkable INFERNAL_ANIMUS = new Drinkable("infernal_animus", new PotionEffect[] {
			new PotionEffect(MobEffects.POISON, 1200, 1), new PotionEffect(MobEffects.WITHER, 3600, 2) });
	public static final Drinkable MYSTIC_UNGUENT = new Drinkable("mystic_unguent",
			new PotionEffect[] { new PotionEffect(MobEffects.WEAKNESS, 1200, 1) });
	public static final Drinkable OIL_HAPPENSTANCE = new Drinkable("oil_happenstance",
			new PotionEffect[] { new PotionEffect(MobEffects.NIGHT_VISION, 1200) });
	public static final GeneralItem FOCUSED_WILL = new GeneralItem("focused_will");
	public static final GeneralItem CONDENSED_FEAR = new GeneralItem("condensed_fear");
	public static final Drinkable INFUSION_BASE = new Drinkable("infusion_base",
			new PotionEffect(MobEffects.WITHER, 200, 3));
	public static final Drinkable CONGEALED_SPIRIT = new Drinkable("congealed_spirit",
			new PotionEffect(MobEffects.NIGHT_VISION, TimeUtilities.secsToTicks(30), 1));

	public static final BrewGrotesque BREW_GROTESQUE = new BrewGrotesque("grotesque");
	public static final Brew BREW_VINES = new Brew("vines");
	public static final Brew BREW_WEBS = new Brew("webs");
	public static final Brew BREW_THORNS = new Brew("thorns");
	public static final Brew BREW_INK = new Brew("ink");
	public static final Brew BREW_SPROUTING = new Brew("sprouting");
	public static final Brew BREW_EROSION = new Brew("erosion");
	public static final Brew BREW_RAISING = new Brew("raising");
	public static final Brew BREW_LOVE = new Brew("love");
	public static final Brew BREW_ICE = new Brew("ice");
	public static final Brew BREW_DEPTHS = new Brew("depths");
	public static final Brew BREW_FROG = new Brew("frog_tongue");
	public static final Brew BREW_LEAPING = new Brew("leaping");
	public static final Brew BREW_HITCHCOCK = new Brew("hitchcock");
	public static final Brew BREW_INFECTION = new Brew("infection");
	public static final BrewSleeping BREW_SLEEPING = new BrewSleeping("sleeping");
	public static final Brew BREW_WASTING = new Brew("wasting");
	public static final Brew BREW_BATS = new Brew("bats");
	public static final BrewRevealing BREW_REVEALING = new BrewRevealing("revealing");
	public static final BrewSoul BREW_SOUL_HUNGER = new BrewSoul("hunger", SymbolInit.CARNOSA_DIEM);
	public static final BrewSoul BREW_SOUL_ANGUISH = new BrewSoul("anguish", SymbolInit.IGNIANIMA);
	public static final BrewSoul BREW_SOUL_FEAR = new BrewSoul("fear", SymbolInit.MORSMORDRE);
	public static final BrewSoul BREW_SOUL_TORMENT = new BrewSoul("torment", SymbolInit.TORMENTUM);

	public static final BrewFluid BREW_FLOWING_SPIRIT = new BrewFluid("flowing_spirit", FluidInit.FLOWING_SPIRIT);
	public static final BrewFluid BREW_HOLLOW_TEARS = new BrewFluid("hollow_tears", FluidInit.HOLLOW_TEARS);

	public static final BrewSolid BREW_ROCK = new BrewSolid("rock", Blocks.STONE);
	public static final BrewSolid BREW_DIRT = new BrewSolid("dirt", Blocks.DIRT);
	public static final BrewSolid BREW_SAND = new BrewSolid("sand", Blocks.SAND);
	public static final BrewSolid BREW_SANDSTONE = new BrewSolid("sandstone", Blocks.SANDSTONE);
	public static final BrewSolid BREW_EROSION_SOLID = new BrewSolid("erosion", null);

	public static final BrewInfused BREW_SOARING = new BrewInfused("soaring", InfusionBrewEffect.Soaring);
	public static final BrewInfused BREW_GRAVE = new BrewInfused("grave", InfusionBrewEffect.Grave);

	// Books
	public static final ArrayList<GeneralItem> BOOKS = new ArrayList<GeneralItem>();

	public static final BookVampire BOOK_VAMPIRE = new BookVampire();
	public static final GeneralItem BOOK_OVEN = new GeneralItem("book_oven");
	public static final GeneralItem BOOK_DISTILLING = new GeneralItem("book_distilling");
	public static final GeneralItem BOOK_CIRLE_MAGIC = new GeneralItem("book_magic_circles");
	public static final GeneralItem BOOK_INFUSIONS = new GeneralItem("book_infusions");
	public static final GeneralItem BOOK_HERBOLOGY = new GeneralItem("book_herbology");
	public static final GeneralItem BOOK_BIOMES = new GeneralItem("book_biomes");
	public static final GeneralItem BOOK_WANDS = new GeneralItem("book_wands");
	public static final GeneralItem BOOK_BURNING = new GeneralItem("book_burning");
	public static final GeneralItem BOOK_VAMPIRE_PAGE = new GeneralItem("book_vampire_page");

	static {
		BOOKS.add(BOOK_OVEN);
		BOOKS.add(BOOK_DISTILLING);
		BOOKS.add(BOOK_CIRLE_MAGIC);
		BOOKS.add(BOOK_INFUSIONS);
		BOOKS.add(BOOK_HERBOLOGY);
		BOOKS.add(BOOK_BIOMES);
		BOOKS.add(BOOK_WANDS);
		BOOKS.add(BOOK_BURNING);

	}

	// Contracts
	public static final Contract CONTRACT_OWNERSHIP = new Contract("ownership");
	public static final Contract CONTRACT_TORMENT = new Contract("torment");
	public static final ContractBlaze CONTRACT_BLAZE = new ContractBlaze("blaze");
	public static final ContractFireResist CONTRACT_FIRERESIST = new ContractFireResist("fire_resist");
	public static final ContractEvaporate CONTRACT_EVAPORATE = new ContractEvaporate("evaporate");
	public static final ContractFiery CONTRACT_FIERY = new ContractFiery("fiery");
	public static final ContractSmelting CONTRACT_SMELTING = new ContractSmelting("smelting");

	// Dream Weaves
	public static final DreamWeave WEAVE_FAST_MOVE = new DreamWeave("weave_fast_move", MobEffects.SPEED,
			MobEffects.SLOWNESS, 7200, 0);
	public static final DreamWeave WEAVE_DIG = new DreamWeave("weave_dig", MobEffects.HASTE, MobEffects.MINING_FATIGUE,
			7200, 0);
	public static final DreamWeave WEAVE_SATURATION = new DreamWeave("weave_saturation", MobEffects.SATURATION,
			MobEffects.HUNGER, 4800, 0);
	public static final DreamWeave WEAVE_NIGHTMARE = new DreamWeave("weave_nightmare", MobEffects.WEAKNESS,
			MobEffects.BLINDNESS, 1200, 0);
	public static final DreamWeave WEAVE_INTENSITY = new DreamWeave("weave_intensity", MobEffects.NIGHT_VISION,
			MobEffects.BLINDNESS, 300, 0);

	// Tools & Weapons
	public static final HuntsmanSpear HUNTSMAN_SPEAR = new HuntsmanSpear("huntsman_spear");
	public static final GeneralItem BROOM = new GeneralItem("broom_mundane");
	public static final BroomEnchanted BROOM_ENCHANTED = new BroomEnchanted("broom_enchanted");
	public static final SeerStone SEER_STONE = new SeerStone("seer_stone");

	// Bolts
	public static final Bolt BOLT_STAKE = new Bolt("stake");
	public static final Bolt BOLT_ANTIMAGIC = new Bolt("antimagic");
	public static final Bolt BOLT_HOLY = new Bolt("holy");
	public static final Bolt BOLT_SPLINTERING = new Bolt("splintering");
	public static final Bolt BOLT_SILVER = new Bolt("silver");

	// Armors

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
