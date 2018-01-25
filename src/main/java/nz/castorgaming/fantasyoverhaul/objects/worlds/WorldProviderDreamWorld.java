package nz.castorgaming.fantasyoverhaul.objects.worlds;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;

public class WorldProviderDreamWorld extends WorldProvider {

	int nightmare;
	private static final String SPIRIT_WORLD_KEY = "FOSpiritWorld";
	private static final String SPIRIT_WORLD_WALKING_KEY = "FOSpiritWalking";
	private static final String SPIRIT_WORLD_NIGHTMARE_KEY = "Nightmare";
	private static final String SPIRIT_WORLD_DEMONIC_KEY = "Demonic";
	private static final String SPIRIT_WORLD_OVERWORLD_BODY_KEY = "OverworldBody";
	private static final String SPIRIT_WORLD_OVERWORLD_HEALTH_KEY = "OverworldHealth";
	private static final String SPIRIT_WORLD_SPIRIT_HEALTH_KEY = "SpiritHealth";
	private static final String SPIRIT_WORLD_OVERWORLD_HUNGER_FOOD_KEY = "OverworldHunger";
	private static final String SPIRIT_WORLD_SPIRIT_HUNGER_FOOD_KEY = "SpiritHunger";
	private static final String SPIRIT_WORLD_OVERWORLD_INVENTORY_KEY = "OverworldInventory";
	private static final String SPIRIT_WORLD_SPIRIT_INVENTORY_KEY = "SpiritInventory";
	private static final String SPIRIT_WORLD_MANIFEST_GHOST_KEY = "FOManifested";
	public static final String SPIRIT_WORLD_MANIFEST_TIME_KEY = "FOManifestDuration";
	public static final String SPIRIT_WORLD_AWAKEN_PLAYER_KEY = "FOForceAwaken";
	private static final String SPIRIT_WORLD_LAST_NIGHTMARE_KILL_KEY = "LastNightmareKillTime";
	public static final String SPIRIT_WORLD_MANIFEST_SKIP_TIME_TICK_KEY = "FOManifestSkipTick";

	public WorldProviderDreamWorld() {
		nightmare = 0;
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		WorldServer overworld = DimensionManager.getWorld(0);
		return overworld.getWorldType().getChunkGenerator(worldObj, worldObj.getWorldInfo().getGeneratorOptions());
	}

	@Override
	protected void createBiomeProvider() {
		super.createBiomeProvider();
		setDimension(Config.instance().dimensionDreamID);
	}

	@Override
	public String getWelcomeMessage() {
		if (this instanceof WorldProviderDreamWorld) {
			return "Entering the Spirit World";
		}
		return null;
	}

	@Override
	public String getDepartMessage() {
		if (this instanceof WorldProviderDreamWorld) {
			return "Departing the Spirit World";
		}

		return null;
	}

	@Override
	public float getStarBrightness(float par1) {
		return 0.0f;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public double getMovementFactor() {
		return 1.0;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return (nightmare > 0) ? 0.5f : 1.0f;
	}

	@Override
	public float getCloudHeight() {
		return 0.0f;
	}

	@Override
	public DimensionType getDimensionType() {
		return DimensionType.register("Spirit World", "_dream", getDimension(), WorldProviderDreamWorld.class, false);
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		return worldObj.getTopSolidOrLiquidBlock(new BlockPos(x, 1, z)).getY() > 0;
	}

	@Override
	public BlockPos getSpawnCoordinate() {
		return new BlockPos(100, 50, 0);
	}

	@Override
	public int getAverageGroundLevel() {
		return 64;
	}

	@Override
	public double getHorizon() {
		return 0.4;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getVoidFogYFactor() {
		return 1.0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getFogColor(float par1, float par2) {
		float var3 = MathHelper.cos(par1 * 3.1415927f * 2.0f) * 2.0f + 0.5f;
		if (var3 < 0.0f) {
			var3 = 0.0f;
		}
		if (var3 > 1.0f) {
			var3 = 1.0f;
		}
		float var4;
		float var5;
		float var6;
		if (this.nightmare == 0) {
			var4 = 0.8f;
			var5 = 0.2f;
			var6 = 0.6f;
		}
		else if (this.nightmare == 1) {
			var4 = 0.0f;
			var5 = 1.0f;
			var6 = 0.0f;
		}
		else {
			var4 = 1.0f;
			var5 = 0.0f;
			var6 = 0.0f;
		}
		var4 *= var3 * 0.94f + 0.06f;
		var5 *= var3 * 0.94f + 0.06f;
		var6 *= var3 * 0.91f + 0.09f;
		return new Vec3d(var4, var5, var6);
	}

	@Override
	public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {
		allowPeaceful = true;
	}

	@Override
	public void updateWeather() {
		if (worldObj != null && worldObj.rand.nextInt(20) == 0) {
			int playerHasNightmare = 0;
			for (EntityPlayer player : worldObj.playerEntities) {
				int level = getPlayerHasNightmare(player);
				if (level > playerHasNightmare) {
					playerHasNightmare = level;
					break;
				}
			}
			if (nightmare != playerHasNightmare) {
				nightmare = playerHasNightmare;
			}
		}
		super.updateWeather();
	}

	public boolean isNightmare() {
		return nightmare > 0;
	}

	public boolean isDemonicNightmare() {
		return nightmare > 1;
	}

	public static int getPlayerHasNightmare(EntityPlayer player) {
		return getPlayerHasNightmare(Infusion.getNBT(player));
	}

	public static int getPlayerHasNightmare(NBTTagCompound compound) {
		if (!compound.hasKey(SPIRIT_WORLD_KEY)) {
			return 0;
		}

		NBTTagCompound spirit = compound.getCompoundTag(SPIRIT_WORLD_KEY);
		boolean nightmare = spirit.getBoolean(SPIRIT_WORLD_NIGHTMARE_KEY);
		boolean demonic = spirit.getBoolean(SPIRIT_WORLD_DEMONIC_KEY);
		return (nightmare && demonic) ? 2 : (nightmare ? 1 : 0);
	}

	public static void setPlayerHasNightmare(EntityPlayer player, boolean nightmare, boolean demonic) {
		setPlayerHasNightmare(Infusion.getNBT(player), nightmare, demonic);
	}

	private static void setPlayerHasNightmare(NBTTagCompound nbt, boolean nightmare, boolean demonic) {
		if (!nbt.hasKey(SPIRIT_WORLD_KEY)) {
			nbt.setTag(SPIRIT_WORLD_KEY, new NBTTagCompound());
		}
		NBTTagCompound spirit = nbt.getCompoundTag(SPIRIT_WORLD_KEY);
		spirit.setBoolean(SPIRIT_WORLD_NIGHTMARE_KEY, nightmare);
		spirit.setBoolean(SPIRIT_WORLD_DEMONIC_KEY, demonic);
	}

	public static void setPlayerLastNightmareKillNow(EntityPlayer player) {
		if (player != null) {
			setPlayerLastNightmareKill(Infusion.getNBT(player), MinecraftServer.getCurrentTimeMillis());
		}
	}

	private static void setPlayerLastNightmareKill(NBTTagCompound nbt, long currentTimeMillis) {
		if (!nbt.hasKey(SPIRIT_WORLD_KEY)) {
			nbt.setTag(SPIRIT_WORLD_KEY, new NBTTagCompound());
		}
		NBTTagCompound spirit = nbt.getCompoundTag(SPIRIT_WORLD_KEY);
		spirit.setLong(SPIRIT_WORLD_LAST_NIGHTMARE_KILL_KEY, currentTimeMillis);
	}

	public static long getPlayerLstNightmareKill(NBTTagCompound nbt) {
		if (!nbt.hasKey(SPIRIT_WORLD_KEY)) {
			return 0L;
		}
		NBTTagCompound spirit = nbt.getCompoundTag(SPIRIT_WORLD_KEY);
		if (!spirit.hasKey(SPIRIT_WORLD_LAST_NIGHTMARE_KILL_KEY)) {
			return 0L;
		}
		return spirit.getLong(SPIRIT_WORLD_LAST_NIGHTMARE_KILL_KEY);
	}

}