package nz.castorgaming.fantasyoverhaul.util;

import java.util.UUID;

import net.minecraft.util.ResourceLocation;
import nz.castorgaming.fantasyoverhaul.integration.ModHookManager;
import nz.castorgaming.fantasyoverhaul.util.handlers.PacketHandler;

public class Reference {

	public static final String MODID = "fantasyoverhaul";
	public static final String NAME = "Fantasy Overhaul";
	public static final String VERSION = "1.0-A";
	public static final String ACCEPTED_VERSION = "[1.10.2]";

	public static final String CLIENT = "nz.castorgaming.fantasyoverhaul.proxy.ClientProxy";
	public static final String COMMON = "nz.castorgaming.fantasyoverhaul.proxy.CommonProxy";

	public static final String DEPEND_STRING = "";

	public static final PacketHandler PACKET_HANDLER = new PacketHandler();

	public static final ModHookManager modHooks = new ModHookManager();

	// NBT Keys
	public static final String INFUSION_CHARGES_KEY = "InfusionCharges";
	public static final String INFUSION_ID_KEY = "InfusionID";
	public static final String MAX_CHARGES_KEY = "InfusionChargesMax";
	public static final String INFUSION_NEXTSYNC = "InfuseResyncLook";
	public static final String INFUSION_GROTESQUE = "InfuseGrotesque";
	public static final String INFUSION_DEPTHS = "InfuseDepths";
	public static final String INFUSION_CURSED = "InfuseCursed";
	public static final String INFUSION_INSANITY = "InfuseInsanity";
	public static final String INFUSION_SINKING = "InfuseSinking";
	public static final String INFUSION_OVERHEAT = "InfuseOverheating";
	public static final String INFUSION_NIGHTMARE = "InfuseWakingNightmare";
	public static final String COOLDOWN = "FOCooldown";
	public static final String PLAYER_PERSISTED = "PlayerPersisted";
	public static final String COVEN = "FOCoven";
	public static final String LAST_POS = "FOLastPos";
	public static final String BEAST_POWER_KEY = "FOBeastPower";
	public static final String BEAST_POWER_CHARGES_KEY = "FOBeastPowerCharges";
	public static final String PLAYER_EFFECT_KEY = "FOPlayerEffect";
	public static final String EXT_PLAYER_PROP = "FOExtendedPlayer";
	public static final String NO_DROPS = "FONoDrops";
	public static final String BREW_TYPE_KEY = "FOBrewEffect";
	public static final String BREW_START_KEY = "FOBrewStart";
	public static final String BREW_REMAINING_KEY = "FOBrewRemaining";
	public static final String SPELL_BOOK_KEY = "FOSpellBook";
	public static final String WAKING_NIGHTMARE = "FOWakingNightmare";
	public static final String SHOP_STOCK = "FOShopStock";

	// Resource Keys
	public static final String INFUSION_REQUIRED = "infuse.infusionrequired";
	public static final String INFUSION_NOCHARGE = "infuse.nocharges";
	public static final String BREW_SKILL_INCREASE = "brew.skillincrease";

	// Config Keys
	public static final String DEBUG = "debug";
	public static final String GENERAL = "general";
	public static final String INTEGRATION = "integration";
	public static final String EXTENDED_PLAYER = "extendedplayer";
	public static final String VAMPIRE = "vampire";
	public static final String WEREWOLF = "werewolf";
	public static final String CRAFTING = "crafting";
	public static final String DIMENSIONS = "dimensions";
	public static final String SPAWNING = "spawning";
	public static final String TOWN = "town";
	public static final String GENERATION = "generation";
	public static final String RITUAL_RITE = "ritualsrites";

	// Chat Util Keys
	public static final String WERE_CHUNK_VISITED = "fo.werewolf.chunkvisited";
	public static final String WERE_INFECTION = "fo.werewolf.infection";
	public static final String MANIFESTATION_COUNTDOWN = "fo.rite.manifestation.countdown";
	public static final String TORMENT_NOSTONE = "fo.tormentContract.nostones";

	// Constants
	public static final int TICKS_PER_SECOND = 20;
	public static final int TICKS_PER_MINUTE = 1200;
	public static final long TICKS_PER_MINECRAFT_DAY = 24000L;
	public static final String EMPTY_STRING = "";
	public static final String BOOK_NEWLINE = "\n";
	public static final String x2_BOOK_NEWLINE = "\n\n";
	public static final String x3_BOOK_NEWLINE = "\n\n\n";
	public static final int CIRCLE_SMALL = 16;
	public static final int CIRCLE_MEDIUM = 28;
	public static final int CIRCLE_LARGE = 40;
	public static final int PLAYER_AIR_FULL = 300;
	public static final int MILLISECS_PER_SECOND = 1000;
	public static final int MILLISECS_PER_MINUTE = 60000;
	public static final long MILLISECS_PER_TICK = 50L;
	public static final UUID BLANK_UUID = new UUID(0L, 0L);
	public static final String BOUND_TO = "item.taglock_kit.boundto";

	public static ResourceLocation location(String path) {
		return new ResourceLocation(MODID, path);
	}
}
