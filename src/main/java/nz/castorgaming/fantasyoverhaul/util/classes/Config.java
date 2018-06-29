package nz.castorgaming.fantasyoverhaul.util.classes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.common.config.Configuration;
import nz.castorgaming.fantasyoverhaul.util.Reference;

public class Config {

	private static final Config INSTANCE;
	public Configuration configuration;

	/** Debugging */
	private boolean debugging;
	private boolean traceRitesEnabled;
	public boolean dupStaffSaveTemplate;

	/** General */
	public boolean guiOnLeft;
	private List<BlockMeta> naturePowerReplaceableBlocks;
	public boolean allowNamePlateMasquerading;
	public boolean allowChatMasquerading;
	public boolean allowCovenWitchVisits;
	public boolean allowHellOnEarthFire;
	public int branchIconSet;
	public int percentageOfPlayersSleepingForBuff;
	public boolean render3dGlintEffect;
	public boolean renderHuntsmanGlintEffect;
	public int decurseTeleportPullRadius;
	public int decurseDirectedRadius;
	public boolean allowDecurseTeleport;
	public boolean allowDecurseDirected;
	public boolean allowDeathHoodFreezeVictims;
	public boolean allowExplodingCreeperHearts;
	public int diseaseRemovalChance;
	public float mantrapAlpha;
	public String[] mutandisExtras;
	public boolean allowVolatilityPotionBlockDamage;
	public boolean restrictPoppetShelvesToVanillaAndSpiritDimensions;
	public boolean restrictTaglockCollectionOnNonPVP;
	public boolean restrictTaglockCollectionForStaffMembers;

	/** Integration */
	public boolean allowModIntegration;
	public boolean allowBlockBreakEvents;
	public boolean allowThaumcraft;
	public boolean allowMineFactoryReloaded;
	public boolean allowArsMagica2;
	public boolean allowForestry;
	public boolean allowJustEnoughItems;
	public boolean respectOtherDeathChestMods;

	/** Extended Player Options */
	public boolean allowPlayerToPlayerWolfInfection;
	public boolean allowVampireWerewolfHybrids;
	/** Vampire */
	public boolean allowVampireRitual;
	public boolean allowVampireQuests;
	public boolean allowStakingVampires;
	public int vampireDeathItemKeepAliveMins;
	public boolean hudShowVampireTargetBloodText;

	/** Werewolf */
	public boolean allowWerewolfQuests;
	public boolean allowWerewolfRitual;
	public boolean allowGoddessStatueRecipe;

	/** Crafting */
	public boolean smeltAllSaplingsToWoodAsh;
	public boolean doubleFumeFilterChance;
	public boolean allowVoidBrambleRecipe;

	/** Dimensions */
	public int dimensionDreamID;
	public int dimensionTormentID;
	public int dimensionMirrorID;
	public boolean shrinkMirrorWorld;

	/** Spawning */
	public int covenWitchSpawnWeight;
	public int goblinSpawnWeight;
	public int goblinSpawnRate;
	public boolean goblinDespawnBlock;
	public int hobgoblinGodSpawnChance;
	public String[] strawmanSpawnerRules;
	public int hellhoundSpawnRate;
	public float vampireHunterSpawnChance;
	public float fairestSpawnChance;
	public int spawnSpiritWeight;

	/** Towns & Building */
	public int townZombieMode;

	public int townWallChance;
	public int townWallWeight;

	public int townKeepChance;
	public int townKeepWeight;

	public boolean townAllowSandy, townAllowPlains, townAllowMountian, townAllowHills, townAllowForest, townAllowSnowy,
			townAllowWasteland, townAllowMesa, townAllowJungle;

	public List<Building> townParts;

	/** Generation */

	public boolean generateApothecaries;
	public boolean generateWitchHuts;
	public boolean generateBookShops;
	public boolean generateCovens;
	public boolean generateWickerMen;
	public boolean generateShacks;
	public boolean generateGoblinHuts;
	public boolean worldGenTwilightForest;
	public int worldGenFrequency;

	/** Rites And Rituals */
	public boolean allowDeathItemRecoveryRite;
	public boolean allowBiomeChanging;
	public int riteOfEclipseCooldownInSecs;

	public void sync() {
		syncGeneral();
		syncIntegration();
		syncExtenedPlayerOptions();
		syncVampire();
		syncWerewolf();
		syncCrafting();
		syncDimensions();
		syncSpawning();
		syncBuildings();
		syncGeneration();
		syncRitualsRites();
		saveIfChanged();
	}

	private void syncGeneral() {
		String category = Reference.GENERAL;

		guiOnLeft = configuration.getBoolean("GUIOnLeft", category, true, "Set GUI on Left");

		allowNamePlateMasquerading = configuration.getBoolean("AllowNamePlateMasquerading", category, true,
				"Allow Name Plate Masquerading");
		allowChatMasquerading = configuration.getBoolean("AllowChatMasquerading", category, true,
				"Allow Chat Masquerading");

		allowCovenWitchVisits = configuration.getBoolean("AllowCovenWitchVisits", category, true,
				"Allow Coven Witch Visits");

		allowHellOnEarthFire = configuration.getBoolean("AllowHellOnEarthFires", category, true,
				"Allow Hell on Earth Fires");

		branchIconSet = configuration.get(category, "BranchIconSet", 0).getInt();

		percentageOfPlayersSleepingForBuff = configuration.getInt("PlayerSleepingBuffPercentage", category, 100, 1, 100,
				"Percentage of Players sleeping for buffs");

		render3dGlintEffect = configuration.getBoolean("Render3DGlintEffect", category, true, "Render 3D Glint Effect");

		renderHuntsmanGlintEffect = configuration.getBoolean("RenderHuntsmanGlintEffect", category, true,
				"Render Glint Effect ong Huntsman");

		decurseTeleportPullRadius = configuration.getInt("DecurseTeleportPullRadius", category, 32, 0, 128,
				"Radius for Decurse Teleport");
		decurseDirectedRadius = configuration.getInt("DecurseDirectedRadius", category, 32, 0, 128,
				"Radius of Directed Decurse Radius");
		allowDecurseTeleport = configuration.getBoolean("DecurseTeleportPullEnabled", category, false,
				"Allow Decurse Teleport");
		allowDecurseDirected = configuration.getBoolean("DecurseDirectedEnabled", category, false,
				"Allow Directed Decurse");

		allowDeathHoodFreezeVictims = configuration.getBoolean("AllowDeathHoodToFreezeVictims", category, true,
				"Allow Death's Hood to freeze targets");

		allowExplodingCreeperHearts = configuration.getBoolean("AllowExplodingCreeperHearts", category, true,
				"Shoudl Creeper Hearts exlopde when eaten");

		diseaseRemovalChance = configuration.getInt("DiseaseBlockRemovalChance", category, 10, 0, 100,
				"Chance of Disease Removal");

		mantrapAlpha = configuration.getFloat("MantrapOpacity", category, 0.3f, 0.1f, 1.0f, "Mantrap Opacity");

		mutandisExtras = configuration.getStringList("NutandisAdditionalBlocks", category,
				new String[] { "fantasyoverhaul:glintweed", "tallgrass" },
				"Additional Blocks that mutandis should transmute");

		allowVolatilityPotionBlockDamage = configuration.getBoolean("AllowVolatilityPotionBlockDamage", category, true,
				"Allow volatility potion damage on blocks");

		restrictPoppetShelvesToVanillaAndSpiritDimensions = configuration.getBoolean(
				"RestrictPoppetsShelvesToVanillaAndSpiritDimensions", category, true,
				"Allow poppet shelves only in vanilla and Fantasy Overhaul Dimensions");

		restrictTaglockCollectionOnNonPVP = configuration.getBoolean("TaglockRestrictPVPServer", category, false,
				"Should Taglock Collection be restricted on PVE Server");

		restrictTaglockCollectionForStaffMembers = configuration.getBoolean("TaglockRestrictStaff", category, false,
				"Should taglock collection be restricted against staff");

	}

	private void syncIntegration() {
		String category = Reference.INTEGRATION;

		allowBlockBreakEvents = configuration.getBoolean("AllowInterModBlockBreakEvents", category, true,
				"Enable Mod Block Break Event Interactions");
		allowModIntegration = configuration.get(category, "AllowModIntegration", true).getBoolean(true);
		allowThaumcraft = configuration.get(category, "AllowThaumcraft", true).getBoolean(true);
		allowMineFactoryReloaded = configuration.get(category, "AllowMineFactoryReloaded", true).getBoolean(true);
		allowForestry = configuration.get(category, "AllowForestry", true).getBoolean(true);
		allowJustEnoughItems = configuration.get(category, "AllowJustEnoughItems", true).getBoolean(true);
		allowArsMagica2 = configuration.get(category, "AllowArsMagica2", true).getBoolean(true);

		respectOtherDeathChestMods = configuration.getBoolean("RespectOtherDeathChestMods", category, true,
				"Respect Other Death Chest Mods");
	}

	private void syncExtenedPlayerOptions() {
		String category = Reference.EXTENDED_PLAYER;

		allowVampireWerewolfHybrids = configuration.getBoolean("AllowVampireWerewolfHybrids", category, true,
				"Allow Vampire Werewolf Hybrid Creation");
	}

	private void syncVampire() {
		String category = Reference.VAMPIRE;
		allowVampireQuests = configuration.getBoolean("AllowVampireQuests", category, true,
				"Allow Vampire Quest Progression");
		allowVampireRitual = configuration.getBoolean("AllowVampireRitual", category, true,
				"Allow Vampire Creation Ritual");
		allowStakingVampires = configuration.getBoolean("AllowVampireStaking", category, true,
				"Allow Vampires to be staked");
		vampireDeathItemKeepAliveMins = configuration.get(category, "VampireDeathItemKeepAliveMins", 12).getInt();
		hudShowVampireTargetBloodText = configuration.getBoolean("HUDShowVampireTargetBloodText", category, false,
				"Show Vampire Blood Text Hud");

	}

	private void syncWerewolf() {
		String category = Reference.WEREWOLF;
		allowPlayerToPlayerWolfInfection = configuration.getBoolean("AllowPlayerToPlayerWolfInfection", category, true,
				"Allow PVP Wolf infection");
		allowWerewolfQuests = configuration.getBoolean("AllowWerewolfQuests", "Werewolf", true,
				"Allow Werewolf Quest Progression");

		allowGoddessStatueRecipe = configuration.getBoolean("AllowGodessStatueRecipe", category, true,
				"Allow Crafting of Goddess Statue");
	}

	private void syncCrafting() {
		String category = Reference.CRAFTING;
		smeltAllSaplingsToWoodAsh = configuration.getBoolean("AddSmeltingForAllSaplingsToWoodAsh", category, true,
				"Enable to allow all saplings to be smelted to wood ash");
		doubleFumeFilterChance = configuration.getBoolean("DoubleFumeFilterChance", category, false,
				"Enable to double fume filter chance");
		allowVoidBrambleRecipe = configuration.getBoolean("AllowVoidBrambleRecipe", category, false,
				"Allow Void Bramble Recipe");

	}

	private void syncDimensions() {
		String category = Reference.DIMENSIONS;

		dimensionDreamID = configuration.get(category, "DreamDimensionID", -37).getInt();
		dimensionTormentID = configuration.get(category, "TormentDimensionID", -38).getInt();
		dimensionMirrorID = configuration.get(category, "MirrorDimensionID", -39).getInt();

		shrinkMirrorWorld = configuration.getBoolean("ShrinkMirrorWorld", category, false,
				"Should Mirror world be shrunk");
	}

	private void syncSpawning() {
		String category = Reference.SPAWNING;

		covenWitchSpawnWeight = configuration.getInt("CovenWitchSpawnWeight", category, 2, 0, 10,
				"Coven Spawning Weight");

		goblinSpawnWeight = configuration.getInt("HobgoblinSpawnWeight", category, 2, 0, 10, "Goblin Spawn Weighting");
		goblinSpawnRate = configuration.getInt("HobgoblinSpawnRate", category, 2, 0, 10, "Goblin Spawn Rate");
		goblinDespawnBlock = configuration.getBoolean("HobgoblinDespawnBlock", category, true,
				"Hobgoblin Despawn Block Enabled");
		hobgoblinGodSpawnChance = configuration.getInt("HobgoblinGodSpawnChance", category, 10, 0, 100,
				"Hobgoblin God Spawn Chance");

		strawmanSpawnerRules = configuration.getStringList("StrawmanSpawnerRules", category,
				new String[] { "Zombie", "Zombie", "Skeleton" }, "Strawman Allowed Spawners");

		hellhoundSpawnRate = configuration.getInt("HellhoundSpawnWeight", category, 25, 0, 100, "Hellhound Spawn Rate");

		vampireHunterSpawnChance = configuration.getFloat("VamprieHunterSpawnChance", category, 0.01f, 0.0f, 1.0f,
				"Vampire Hunter Spawn Chance");

		fairestSpawnChance = configuration.getFloat("NewFairestSpawnChance", category, 0.01f, 0.1f, 1.0f,
				"Fairest SpawN Chance");

		spawnSpiritWeight = configuration.getInt("SpawnSpiritWeight", category, 1, 1, 1000, "Spirit Spawn Weighting");

	}

	private void syncBuildings() {
		String category = Reference.TOWN;
		townZombieMode = configuration.getInt("TownZombieAttackReductionMode", category, 1, 0, 2, "Town Zombie Mode");

		townWallChance = configuration.getInt("TownWallMode", category, 1, 0, 2, "Town Wall Generation Mode");
		townWallWeight = configuration.getInt("TownWallWeight", category, 100, 0, 1000, "Town Wall Weight");

		townKeepChance = configuration.getInt("TownKeepChance", category, 1, 0, 2, "Town Keep Chance");
		townKeepWeight = configuration.getInt("TownKeepWeight", category, 100, 0, 1000, "Town Keep Weight");

		townAllowSandy = configuration.getBoolean("TownBiomeSandyAllowed", category, true, "Sandy Biome Towns Allowed");
		townAllowPlains = configuration.getBoolean("TownBiomePlainsAllow", category, true, "Plains Biome Towns Allow");
		townAllowMountian = configuration.getBoolean("TownBiomeMountainAllow", category, true,
				"Mountian Biome Towns Allow");
		townAllowHills = configuration.getBoolean("TownBiomeHillsAllow", category, true, "Hills Biome Towns Allow");
		townAllowForest = configuration.getBoolean("TownBiomeForestAllow", category, true, "Forest Biome Towns Allow");
		townAllowSnowy = configuration.getBoolean("TownBiomeSnowyAllow", category, true, "Snowy Biome Towns Allow");
		townAllowWasteland = configuration.getBoolean("TownBiomeWastelandAllow", category, true,
				"Wasteland Biome Towns Allow");
		townAllowMesa = configuration.getBoolean("TownBiomeMesaAllow", category, true, "Mesa Biome Town Allow");
		townAllowJungle = configuration.getBoolean("TownBiomeJungleAllow", category, true, "Jungle Biome Towns Allow");

		townParts = new ArrayList<Building>();
		new Building(StructureVillagePieces.House4Garden.class, "GardenHouse", 3, 20, 3, 5, this);
		new Building(StructureVillagePieces.House1.class, "House", 3, 20, 3, 5, this);
		new Building(StructureVillagePieces.WoodHut.class, "WoodHut", 3, 20, 3, 5, this);
		new Building(StructureVillagePieces.Hall.class, "Hall", 3, 20, 3, 5, this);
		new Building(StructureVillagePieces.House3.class, "House3", 3, 20, 3, 5, this);
		new Building(StructureVillagePieces.Field1.class, "SingleField", 3, 20, 3, 5, this);
		new Building(StructureVillagePieces.Field2.class, "DoubleField", 3, 20, 3, 5, this);
		new Building(StructureVillagePieces.House2.class, "Blacksmith", 1, 5, 0, 1, this);
		new Building(StructureVillagePieces.Church.class, "Church", 0, 10, 0, 1, this);

	}

	private void syncGeneration() {
		String category = Reference.GENERATION;

		generateApothecaries = configuration.getBoolean("GenerateApothecaries", category, true,
				"Enable Apothecary Generation");
		generateCovens = configuration.getBoolean("GenerateCovens", category, true, "Enable Coven Generation");
		generateWickerMen = configuration.getBoolean("GenerateWickerMen", category, true,
				"Enable Wickermen Generation");
		generateShacks = configuration.getBoolean("GenerateShacks", category, true, "Enable Shack Generation");
		generateGoblinHuts = configuration.getBoolean("GenerateGoblinHuts", category, true,
				"Enable Goblin Hut Generation");
		generateWitchHuts = configuration.getBoolean("GenerateWitchHuts", category, true,
				"Enable Witch Hut Generation");
		generateBookShops = configuration.getBoolean("GenerateBookShops", category, true, "Enable Bookshop Generation");
		worldGenTwilightForest = configuration.getBoolean("WorldGenTwilightForest", category, true,
				"Enable Twilight Generation");

		worldGenFrequency = configuration.get(category, "WorldGenFrequency", 12).getInt();

	}

	private void syncRitualsRites() {
		String category = Reference.RITUAL_RITE;

		allowDeathItemRecoveryRite = configuration.getBoolean("AllowDeathItemRecoveryRite", category, true,
				"Allow Death Item Recovery Rite");

		allowBiomeChanging = configuration.getBoolean("AllowBiomeModificationRitual", category, true,
				"Allow Biome Modification Ritual");

		riteOfEclipseCooldownInSecs = configuration.getInt("RiteOfEclipseCooldownInSecs", category, 0, 0, 3600,
				"Rite of Eclipse Cooldown Secs");

		String[] replaceableBlocks = configuration.getStringList("NaturesPowerReplaceableBlocks", category,
				new String[] { "mycelium" }, "Blocks replaceable by Natures Power (modid:block)");
		for (String extra : replaceableBlocks) {
			try {
				naturePowerReplaceableBlocks.add(new BlockMeta(extra));
			} catch (Throwable t) {

			}
		}

	}

	/** Setup & Methods */

	public void init(Configuration configuration, Configuration configuration_debug) {
		(this.configuration = configuration).load();
		sync();
		configuration_debug.load();
		syncDebug(configuration_debug);
	}

	private void syncDebug(Configuration configuration_debug) {
		String category = Reference.DEBUG;
		traceRitesEnabled = configuration_debug.get(category, "TraceRites", false).getBoolean(false);
		debugging = configuration_debug.get(category, "Debugging", false).getBoolean(false);
		dupStaffSaveTemplate = configuration_debug.get(category, "SaveDupStaffTemplate", false).getBoolean(false);
	}

	public static Config instance() {
		return INSTANCE;
	}

	public void saveIfChanged() {
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	public boolean isDebugging() {
		return debugging;
	}

	public boolean traceRites() {
		return traceRitesEnabled;
	}

	public boolean isReduceZombieVillagerDamageActive() {
		return townZombieMode >= 1;
	}

	public boolean camReplaceNaturalBlock(Block block, int meta) {
		for (BlockMeta bm : naturePowerReplaceableBlocks) {
			if (bm.isMatch(block, meta)) {
				return true;
			}
		}
		return false;
	}

	public static class Building {
		private static final String TOWN = "Town";
		public final int groups, weight, min, max;
		public final Class<? extends StructureVillagePieces.Village> clazz;

		public Building(Class<? extends Village> clazz, String name, int groups, int weight, int min, int max,
				Config config) {
			this.clazz = clazz;
			this.groups = config.configuration.get("Buildings", TOWN + name + "ClusterGroups", groups).getInt();
			this.weight = config.configuration.get("Buildings", TOWN + name + "ClusterWeight", weight).getInt();
			this.min = config.configuration.get("Buildings", TOWN + name + "ClusterMin", min).getInt();
			this.max = config.configuration.get("Buildings", TOWN + name + "ClusterMax", max).getInt();
			config.townParts.add(this);
		}

	}

	public static class BlockMeta {
		private Block block;
		private IBlockState blockstate;

		public BlockMeta(String extra) {
			String name = extra;
			int meta = 32767;
			final int comma = extra.lastIndexOf(44);
			if (comma > 0) {
				name = extra.substring(0, comma);
				meta = Integer.parseInt(extra.substring(comma + 1));
			}
			block = Block.REGISTRY.getObject(new ResourceLocation(name));
			blockstate = block.getStateFromMeta(meta);
		}

		public boolean isMatch(Block block, int meta) {
			return this.block == block && meta == block.getMetaFromState(blockstate);
		}

	}

	public static class ItemMeta {
		private Item item;

		public ItemMeta(String extra) {
			String name = extra;
			int meta = 32767;
			final int comma = extra.lastIndexOf(44);
			if (comma > 0) {
				name = extra.substring(0, comma);
				meta = Integer.parseInt(extra.substring(comma + 1));
			}
			item = Item.REGISTRY.getObject(new ResourceLocation(name));
		}
	}

	static {
		INSTANCE = new Config();
	}

}
