package nz.castorgaming.fantasyoverhaul.objects.worlds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.blocks.BlockDreamCatcher;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityCorpse;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityDemon;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityNightmare;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Log;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.ChatUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.classes.Coord;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketPlayerStyle;
import nz.castorgaming.fantasyoverhaul.util.packets.PushTarget;

public class WorldProviderDreamWorld extends WorldProvider {

	int nightmare;
	private static final String SPIRIT_DIM = "FOSpiritWorld";
	private static final String SPIRIT_WALKING = "FOSpiritWalking";
	private static final String SW_NIGHTMARE_KEY = "Nightmare";
	private static final String SW_DEMONIC_KEY = "Demonic";
	private static final String SW_OVERWORLD_BODY = "OverworldBody";
	private static final String SW_OVERWORLD_HEALTH = "OverworldHealth";
	private static final String SW_HEALTH = "SpiritHealth";
	private static final String SW_OVERWORLD_HUNGER = "OverworldHunger";
	private static final String SW_HUNGER = "SpiritHunger";
	private static final String SW_OVERWORLD_INVENTORY = "OverworldInventory";
	private static final String SW_INVENTORY = "SpiritInventory";
	private static final String SW_MANIFEST_GHOST = "FOManifested";
	public static final String SW_MANIFEST_TIME = "FOManifestDuration";
	public static final String SW_AWAKEN_PLAYER = "FOForceAwaken";
	private static final String SW_LAST_NIGHTMARE_KILL = "LastNightmareKillTime";
	public static final String SW_MANIFEST_SKIP_TIME_TICK = "FOManifestSkipTick";

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
		} else if (this.nightmare == 1) {
			var4 = 0.0f;
			var5 = 1.0f;
			var6 = 0.0f;
		} else {
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
		if (!compound.hasKey(SPIRIT_DIM)) {
			return 0;
		}

		NBTTagCompound spirit = compound.getCompoundTag(SPIRIT_DIM);
		boolean nightmare = spirit.getBoolean(SW_NIGHTMARE_KEY);
		boolean demonic = spirit.getBoolean(SW_DEMONIC_KEY);
		return (nightmare && demonic) ? 2 : (nightmare ? 1 : 0);
	}

	public static void setPlayerHasNightmare(EntityPlayer player, boolean nightmare, boolean demonic) {
		setPlayerHasNightmare(Infusion.getNBT(player), nightmare, demonic);
	}

	private static void setPlayerHasNightmare(NBTTagCompound nbt, boolean nightmare, boolean demonic) {
		if (!nbt.hasKey(SPIRIT_DIM)) {
			nbt.setTag(SPIRIT_DIM, new NBTTagCompound());
		}
		NBTTagCompound spirit = nbt.getCompoundTag(SPIRIT_DIM);
		spirit.setBoolean(SW_NIGHTMARE_KEY, nightmare);
		spirit.setBoolean(SW_DEMONIC_KEY, demonic);
	}

	public static void setPlayerLastNightmareKillNow(EntityPlayer player) {
		if (player != null) {
			setPlayerLastNightmareKill(Infusion.getNBT(player), MinecraftServer.getCurrentTimeMillis());
		}
	}

	private static void setPlayerLastNightmareKill(NBTTagCompound nbt, long currentTimeMillis) {
		if (!nbt.hasKey(SPIRIT_DIM)) {
			nbt.setTag(SPIRIT_DIM, new NBTTagCompound());
		}
		NBTTagCompound spirit = nbt.getCompoundTag(SPIRIT_DIM);
		spirit.setLong(SW_LAST_NIGHTMARE_KILL, currentTimeMillis);
	}

	public static long getPlayerLastNightmareKill(NBTTagCompound nbt) {
		if (!nbt.hasKey(SPIRIT_DIM)) {
			return 0L;
		}
		NBTTagCompound spirit = nbt.getCompoundTag(SPIRIT_DIM);
		if (!spirit.hasKey(SW_LAST_NIGHTMARE_KILL)) {
			return 0L;
		}
		return spirit.getLong(SW_LAST_NIGHTMARE_KILL);
	}

	public static boolean isSpiritWalking(EntityPlayer player) {
		return isSpiritWalking(Infusion.getNBT(player));
	}

	public static boolean isSpiritWalking(NBTTagCompound nbt) {
		return nbt.getBoolean(SPIRIT_WALKING);
	}

	public static void setSpiritWalking(EntityPlayer player, boolean walking) {
		setSpiritWalking(Infusion.getNBT(player), walking);
	}

	public static void setSpiritWalking(NBTTagCompound nbt, boolean walking) {
		nbt.setBoolean(SPIRIT_WALKING, walking);
	}

	private static void addItemToInventory(EntityPlayer player, ItemStack stack, int quantity) {
		if (quantity > 0) {
			int itemsRemaining = quantity;
			int maxStack = stack.getMaxStackSize();
			while (itemsRemaining > 0) {
				int q = (itemsRemaining > maxStack) ? maxStack : itemsRemaining;
				itemsRemaining -= q;
				ItemStack newStack = new ItemStack(stack.getItem(), quantity, stack.getItemDamage());
				player.inventory.addItemStackToInventory(newStack);
			}
		}
	}

	private static void addItemToInventory(EntityPlayer player, ArrayList<ItemStack> stacks) {
		for (ItemStack stack : stacks) {
			if (!player.inventory.addItemStackToInventory(stack)) {
				player.worldObj.spawnEntityInWorld(
						new EntityItem(player.worldObj, player.posX, 0.5 + player.posY, player.posZ, stack));
			}
		}
	}

	public static void sendPlayerToSpiritWorld(EntityPlayer player, double nightmareChance) {
		if (player != null && !player.worldObj.isRemote) {
			NBTTagCompound nbtPlayer = Infusion.getNBT(player);
			if (!nbtPlayer.hasKey(SPIRIT_DIM)) {
				nbtPlayer.setTag(SPIRIT_DIM, new NBTTagCompound());
			}
			NBTTagCompound nbtSpirit = nbtPlayer.getCompoundTag(SPIRIT_DIM);
			Coord posBody = new Coord(player);
			posBody.setNBT(nbtSpirit, SW_OVERWORLD_BODY);
			int fireFound = 0;
			int heartsFound = 0;
			int spiritPoolFound = 0;
			int cottonFound = 0;
			boolean nightmareCatcherFound = false;
			double modifiedNightmareChance = nightmareChance;
			if (nightmareChance > 0.0 && nightmareChance < 1.0) {
				int R = 8;
				int posX = MathHelper.floor_double(player.posX);
				int posY = MathHelper.floor_double(player.posY);
				int posZ = MathHelper.floor_double(player.posZ);
				for (int x = posX - R; x <= posX + R; x++) {
					for (int z = posZ - R; z <= posZ + R; z++) {
						for (int y = posY - R; y <= posY + R; y++) {
							Block block = player.worldObj.getBlockState(new BlockPos(x, y, z)).getBlock();
							if (!nightmareCatcherFound && block == BlockInit.DREAM_CATCHER) {
								BlockDreamCatcher dreamCatcher = BlockInit.DREAM_CATCHER;
								if (BlockDreamCatcher.causesNightmares(player.worldObj, new BlockPos(x, y, z))) {
									modifiedNightmareChance -= 0.5;
									nightmareCatcherFound = true;
								}
							}
							if (spiritPoolFound < 3 && block == FluidInit.FLOWING_SPIRIT.getBlock()
									&& block.getMetaFromState(player.worldObj.getBlockState(new BlockPos(x, y, z)))) {
								spiritPoolFound++;
								modifiedNightmareChance -= 0.1;
							}
							if (cottonFound < 2 && block == BlockInit.WISPY_COTTON) {
								cottonFound++;
								modifiedNightmareChance -= 0.1;
							}
							if (heartsFound < 2 && block == BlockInit.HEART_DEMON) {
								heartsFound++;
								modifiedNightmareChance += 0.35;
							}
							if (fireFound < 3 && block == Blocks.FIRE) {
								fireFound++;
								modifiedNightmareChance += 0.1;
							}
						}
					}
				}
				modifiedNightmareChance = (nightmareCatcherFound ? Math.min(Math.max(modifiedNightmareChance, 0.0), 1.0)
						: nightmareChance);
			}
			boolean nightmare = modifiedNightmareChance != 0.0
					&& (modifiedNightmareChance == 1.0 || player.worldObj.rand.nextDouble() < modifiedNightmareChance);
			boolean demonic = nightmare && nightmareCatcherFound && spiritPoolFound > 0 && heartsFound > 0
					&& player.worldObj.rand.nextDouble() < heartsFound * 0.35 + fireFound * 0.1;
			setPlayerHasNightmare(nbtPlayer, nightmare, demonic);
			setSpiritWalking(nbtPlayer, true);
			EntityCorpse corpse = new EntityCorpse(player.worldObj);
			corpse.setHealth(player.getHealth());
			corpse.setCustomNameTag(player.getCommandSenderEntity().getName());
			corpse.setOwner(player.getCommandSenderEntity().getUniqueID());
			corpse.setLocationAndAngles(0.5 + MathHelper.floor_double(player.posX), player.posY,
					0.5 + MathHelper.floor_double(player.posZ), 0.0f, 0.0f);
			player.worldObj.spawnEntityInWorld(corpse);
			int boneNeedles = player.inventory.clearMatchingItems(ItemInit.NEEDLE_ICY, -1, 0, null);
			int mutandis = player.inventory.clearMatchingItems(ItemInit.MUTANDIS, -1, 0, null);
			dropBetterBackpacks(player);
			NBTTagList nbtOverworldInventory = new NBTTagList();
			player.inventory.writeToNBT(nbtOverworldInventory);
			nbtSpirit.setTag(SW_OVERWORLD_INVENTORY, nbtOverworldInventory);
			if (nbtSpirit.hasKey(SW_INVENTORY)) {
				NBTTagList nbtSpiritInventory = nbtSpirit.getTagList(SW_INVENTORY, 10);
				player.inventory.readFromNBT(nbtSpiritInventory);
				nbtSpirit.removeTag(SW_INVENTORY);
			} else {
				player.inventory.clear();
			}
			addItemToInventory(player, ItemInit.NEEDLE_ICY.createStack(), boneNeedles);
			addItemToInventory(player, ItemInit.MUTANDIS.createStack(), mutandis);
			nbtSpirit.setFloat(SW_OVERWORLD_HEALTH, Math.max(player.getHealth(), 1.0f));
			if (nbtSpirit.hasKey(SW_HEALTH)) {
				float health = Math.max(player.getHealth(), 10.f);
				player.setHealth(health);
				nbtSpirit.removeTag(SW_HEALTH);
			}
			NBTTagCompound nbtOverworldFood = new NBTTagCompound();
			player.getFoodStats().writeNBT(nbtOverworldFood);
			nbtSpirit.setTag(SW_OVERWORLD_HUNGER, nbtOverworldFood);
			if (nbtSpirit.hasKey(SW_HUNGER)) {
				NBTTagCompound nbtSpiritFood = nbtSpirit.getCompoundTag(SW_HUNGER);
				player.getFoodStats().readNBT(nbtSpiritFood);
				player.getFoodStats().addStats(16, 0.8f);
				nbtSpirit.removeTag(SW_HUNGER);
			}
			changeDimension(player, Config.instance().dimensionDreamID);
			findTopAndSetPosition(player.worldObj, player);
			Reference.PACKET_HANDLER.sendToAll(new PacketPlayerStyle(player));
			Reference.PACKET_HANDLER.sendTo(new PushTarget(0.0, 0.1, 0.0), player);
		}
	}

	private static void dropBetterBackpacks(EntityPlayer player) {
		if (FantasyOverhaul.LoadedMod.BACKPACK.getLoaded()) {
			IBackpack backpack = BackpackHelper.getBackpack(player);
			ItemStack stackBackpack = backpack.getStack();
			World w = player.worldObj;
			int x = MathHelper.floor_double(player.posX);
			int y = MathHelper.floor_double(player.posY);
			int z = MathHelper.floor_double(player.posZ);
			boolean found = true;
			if (isReplaceable(w, x + 1, y, z)) {
				++x;
			} else if (isReplaceable(w, x - 1, y, z)) {
				--x;
			} else if (isReplaceable(w, x, y, z + 1)) {
				++z;
			} else if (isReplaceable(w, x - 1, y, z - 1)) {
				--z;
			} else if (isReplaceable(w, x + 1, y, z + 1)) {
				++x;
				++z;
			} else if (isReplaceable(w, x - 1, y, z + 1)) {
				--x;
				++z;
			} else if (isReplaceable(w, x + 1, y, z - 1)) {
				++x;
				--z;
			} else if (isReplaceable(w, x - 1, y, z - 1)) {
				--x;
				--z;
			} else {
				found = false;
			}
			if (found) {
				if (!w.getBlockState(new BlockPos(x, y - 1, z)).isOpaqueCube()) {
					w.setBlockState(new BlockPos(x, y - 1, z), Blocks.STONE.getDefaultState());
				}
			} else {
				found = true;
				y++;
				if (isReplaceable(w, x + 1, y, z)) {
					++x;
				} else if (isReplaceable(w, x - 1, y, z)) {
					--x;
				} else if (isReplaceable(w, x, y, z + 1)) {
					++z;
				} else if (isReplaceable(w, x - 1, y, z - 1)) {
					--z;
				} else if (isReplaceable(w, x + 1, y, z + 1)) {
					++x;
					++z;
				} else if (isReplaceable(w, x - 1, y, z + 1)) {
					--x;
					++z;
				} else if (isReplaceable(w, x + 1, y, z - 1)) {
					++x;
					--z;
				} else if (isReplaceable(w, x - 1, y, z - 1)) {
					--x;
					--z;
				} else {
					found = false;
				}
				if (!found) {
					x++;
					y++;
					w.setBlockToAir(new BlockPos(z, y, z));
					if (!w.getBlockState(new BlockPos(x, y - 1, z)).isOpaqueCube()) {
						w.setBlockState(new BlockPos(x, y - 1, z), Blocks.STONE.getDefaultState());
					}
				}
			}
			Boolean result = BackpackHelper.placeBackpack(w, new BlockPos(x, y, z), stackBackpack, player, false);
			if (!result) {
				Log.instance().debug("Backpack could not be placed");
			} else {
				BackpackHelper.setEquippedBackpack(player, stackBackpack, backpack.getData());
			}
		}
	}

	private static boolean isReplaceable(World world, int x, int y, int z) {
		Material m = world.getBlockState(new BlockPos(x, y, z)).getMaterial();
		return m != null && m.isReplaceable();
	}

	public static void changeDimension(EntityPlayer player, int dimension) {
		dismountEntity(player);
		EntityUtil.travelToDimension(player, dimension);
	}

	private static void dismountEntity(EntityPlayer player) {
		if (player.isRiding()) {
			player.getRidingEntity().dismountRidingEntity();
		}
	}

	public static void findTopAndSetPosition(World world, EntityPlayer player) {
		findTopAndSetPosition(world, player, player.getPosition());
	}

	private static void findTopAndSetPosition(World world, EntityPlayer player, BlockPos pos) {
		int x = MathHelper.floor_double(pos.getX());
		int y = MathHelper.floor_double(pos.getY());
		int z = MathHelper.floor_double(pos.getZ());

		if (!isValidSpawnPoint(world, x, y, z)) {
			for (int i = 1; i <= 256; i++) {
				int yPlus = y + i;
				int yMinus = y - 1;
				if (yPlus < 256 && isValidSpawnPoint(world, x, yPlus, z)) {
					y = yPlus;
					break;
				}
				if (yMinus > 2 && isValidSpawnPoint(world, x, yMinus, z)) {
					y = yMinus;
					break;
				}
				if (yMinus <= 2 && yPlus >= 255) {
					break;
				}
			}
		}
		player.setPositionAndUpdate(0.5 + x, 0.1 + y, 0.5 + z);
	}

	private static boolean isValidSpawnPoint(World world, int x, int y, int z) {
		Material matBelow = world.getBlockState(new BlockPos(x, y - 1, z)).getMaterial();
		return !world.isAirBlock(new BlockPos(x, y - 1, z)) && matBelow != Material.LAVA
				&& world.isAirBlock(new BlockPos(x, y, z)) && world.isAirBlock(new BlockPos(x, y + 1, z));
	}

	public static void returnPlayerToOverworld(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			if (player.dimension != Config.instance().dimensionDreamID) {
				Log.instance()
						.warning("Player " + player.getDisplayNameString()
								+ "is in incorrect dimension when returning from spirit world, dimension = "
								+ player.dimension);
			}
			NBTTagCompound nbtPlayer = Infusion.getNBT(player);
			if (!nbtPlayer.hasKey(SPIRIT_DIM)) {
				nbtPlayer.setTag(SPIRIT_DIM, new NBTTagCompound());
			}
			NBTTagCompound nbtSpirit = nbtPlayer.getCompoundTag(SPIRIT_DIM);
			boolean isSpiritWorld = player.dimension == Config.instance().dimensionDreamID;
			int cottonRemoved = isSpiritWorld ? player.inventory.clearMatchingItems(BlockInit.WISPY_COTTON, -1, 0, null)
					: 0;
			int disturbedCottonRemoved = isSpiritWorld
					? player.inventory.clearMatchingItems(ItemInit.DISTURBED_COTTON, -1, 0, null)
					: 0;
			int hunger = isSpiritWorld ? player.inventory.clearMatchingItems(ItemInit.MELLIFLUOUS_HUNGER, -1, 0, null)
					: 0;
			int spirit = isSpiritWorld ? player.inventory.clearMatchingItems(ItemInit.BREW_FLOWING_SPIRIT, -1, 0, null)
					: 0;
			int subudedSpirits = player.inventory.clearMatchingItems(ItemInit.SUBDUED_SPIRIT, -1, 0, null);
			int needles = player.inventory.clearMatchingItems(ItemInit.NEEDLE_ICY, -1, 0, null);
			dropBetterBackpacks(player);
			if (player.dimension == Config.instance().dimensionDreamID) {
				NBTTagList nbtSpiritInventory = new NBTTagList();
				player.inventory.writeToNBT(nbtSpiritInventory);
				nbtSpirit.setTag(SW_INVENTORY, nbtSpiritInventory);
			}

			if (nbtSpirit.hasKey(SW_OVERWORLD_INVENTORY)) {
				NBTTagList nbtOverworldInventory = nbtSpirit.getTagList(SW_OVERWORLD_INVENTORY, 10);
				player.inventory.readFromNBT(nbtOverworldInventory);
				nbtSpirit.removeTag(SW_OVERWORLD_INVENTORY);
			} else {
				player.inventory.clear();
			}
			addItemToInventory(player, new ItemStack(BlockInit.WISPY_COTTON, 1, 0), cottonRemoved);
			addItemToInventory(player, ItemInit.DISTURBED_COTTON, disturbedCottonRemoved);
			addItemToInventory(player, ItemInit.NEEDLE_ICY, needles);
			addItemToInventory(player, ItemInit.BREW_FLOWING_SPIRIT, spirit);
			addItemToInventory(player, ItemInit.MELLIFLUOUS_HUNGER, hunger);
			addItemToInventory(player, ItemInit.SUBDUED_SPIRITS, subudedSpirits);

			nbtSpirit.setFloat(SW_HEALTH, Math.max(player.getHealth(), 10.0f));
			if (nbtSpirit.hasKey(SW_OVERWORLD_HEALTH)) {
				player.setHealth(nbtSpirit.getFloat(SW_OVERWORLD_HEALTH));
				nbtSpirit.removeTag(SW_OVERWORLD_HEALTH);
			}
			NBTTagCompound nbtSpiritFood = new NBTTagCompound();
			player.getFoodStats().writeNBT(nbtSpiritFood);
			nbtSpirit.setTag(SW_HUNGER, nbtSpiritFood);
			if (nbtSpirit.hasKey(SW_OVERWORLD_HUNGER)) {
				player.getFoodStats().readNBT(nbtSpirit.getCompoundTag(SW_OVERWORLD_HUNGER));
				nbtSpirit.removeTag(SW_OVERWORLD_HUNGER);
			}
			setPlayerHasNightmare(nbtPlayer, false, false);
			playerIsGhost(nbtPlayer, false);
			setSpiritWalking(nbtPlayer, false);
			player.extinguish();
			Coord posBody = Coord.createFrom(nbtSpirit, SW_OVERWORLD_BODY);
			if (player.dimension != 0) {
				if (posBody != null) {
					dismountEntity(player);
					player.setPositionAndUpdate(posBody.x, posBody.y, posBody.z);
				}
				changeDimension(player, 0);
			}
			if (posBody != null) {
				findTopAndSetPosition(player.worldObj, player, new BlockPos(posBody.x, posBody.y, posBody.z));
				nbtSpirit.removeTag(SW_OVERWORLD_BODY);
			} else {
				findTopAndSetPosition(player.worldObj, player);
			}
			for (Entity entity : player.worldObj.loadedEntityList) {
				if (entity instanceof EntityCorpse) {
					EntityCorpse corpse = (EntityCorpse) entity;
					UUID owner = corpse.getOwner();
					if (owner.equals(Reference.BLANK_UUID) || !owner.equals(player.getUniqueID())) {
						continue;
					}
					player.worldObj.removeEntity(corpse);
				}
			}
			Reference.PACKET_HANDLER.sendToAll(new PacketPlayerStyle(player));
			Reference.PACKET_HANDLER.sendTo(new PushTarget(0.0, 0.1, 0.1), player);
		}
	}

	public static void manifestPlayerInOverworldAsGhost(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			NBTTagCompound nbtPlayer = Infusion.getNBT(player);
			if (!nbtPlayer.hasKey(SPIRIT_DIM)) {
				nbtPlayer.setTag(SPIRIT_DIM, new NBTTagCompound());
			}
			NBTTagCompound nbtSpirit = nbtPlayer.getCompoundTag(SPIRIT_DIM);
			int needles = player.inventory.clearMatchingItems(ItemInit.NEEDLE_ICY, -1, 0, null);
			dropBetterBackpacks(player);
			NBTTagList nbtSpiritInventory = new NBTTagList();
			player.inventory.writeToNBT(nbtSpiritInventory);
			nbtSpirit.setTag(SW_INVENTORY, nbtSpiritInventory);
			player.inventory.clear();
			addItemToInventory(player, ItemInit.NEEDLE_ICY, needles);
			nbtSpirit.setFloat(SW_HEALTH, Math.max(player.getHealth(), 1.0f));
			playerIsGhost(nbtPlayer, true);
			changeDimension(player, 0);
			findTopAndSetPosition(player.worldObj, player);
			Reference.PACKET_HANDLER.sendToAll(new PacketPlayerStyle(player));
		}
	}

	public static void returnGhostPlayerToSpiritWorld(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
			if (!nbtPlayer.hasKey(SPIRIT_DIM)) {
				nbtPlayer.setTag(SPIRIT_DIM, new NBTTagCompound());
			}
			final NBTTagCompound nbtSpirit = nbtPlayer.getCompoundTag("FOSpiritWorld");
			final int boneNeedles = player.inventory.clearMatchingItems(ItemInit.NEEDLE_ICY, -1, 0, null);
			final ArrayList<ItemStack> fetishes = getBoundFetishes(player.inventory);
			player.inventory.dropAllItems();
			dropBetterBackpacks(player);
			if (nbtSpirit.hasKey("SpiritInventory")) {
				final NBTTagList nbtSpiritInventory = nbtSpirit.getTagList("SpiritInventory", 10);
				player.inventory.readFromNBT(nbtSpiritInventory);
				nbtSpirit.removeTag("SpiritInventory");
			}
			addItemToInventory(player, ItemInit.NEEDLE_ICY.createStack(), boneNeedles);
			addItemToInventory(player, fetishes);
			playerIsGhost(nbtPlayer, false);
			changeDimension(player, Config.instance().dimensionDreamID);
			findTopAndSetPosition(player.worldObj, player);
			Reference.PACKET_HANDLER.sendToAll(new PacketPlayerStyle(player));
		}
	}

	private static ArrayList<ItemStack> getBoundFetishes(InventoryPlayer inventory) {
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() instanceof BlockFetish.ClassItemBlock
					&& InfusedSpiritEffect.getEffectID(stack) != null && !InfusedSpiritEffect.getEffectID().isEmpty()) {
				stacks.add(stack);
			}
		}
		return stacks;
	}

	public static void updatePlayerEffects(World world, EntityPlayer player, NBTTagCompound nbtPlayer, long time,
			long counter) {
		if (!world.isRemote) {
			boolean done = false;
			if (counter % 20L == 0L) {
				boolean mustAwaken = getPlayerMustAwaken(Infusion.getNBT(player));
				if (mustAwaken) {
					setPlayerMustAwaken(nbtPlayer, false);
					if (player.dimension != Config.instance().dimensionDreamID && isSpiritWalking(player)
							&& !isPlayerGhost(player)) {
						returnPlayerToOverworld(player);
					} else if (player.dimension == Config.instance().dimensionDreamID) {
						returnPlayerToOverworld(player);
					}
				}
			}
			if (!done && counter % 100L == 0L) {
				int nightmareLevel = getPlayerHasNightmare(nbtPlayer);
				if (player.dimension == Config.instance().dimensionDreamID && nightmareLevel > 0) {
					double R = 18.0;
					double H = 18.0;
					AxisAlignedBB bounds = new AxisAlignedBB(player.posX - R, player.posY - R, player.posZ - R,
							player.posX + R, player.posY + R, player.posZ + R);
					if (nightmareLevel > 1) {
						double chance = world.rand.nextDouble();
						if (chance < 0.5) {
							EntitySmallFireball fireball = new EntitySmallFireball(world,
									player.posX - 2 + world.rand.nextInt(5), player.posY + 15.0,
									player.posZ - 2.0 + world.rand.nextInt(5), 0.0, -0.2, 0.0);
							world.spawnEntityInWorld(fireball);
						} else if (chance < 0.65) {
							EntityLargeFireball fireball = new EntityLargeFireball(world);
							double x, y, z;
							x = player.posX - 2.0 + world.rand.nextInt(5);
							y = player.posY + 15;
							z = player.posZ - 2.0 + world.rand.nextInt(5);
							fireball.setLocationAndAngles(x, y, z, fireball.rotationYaw, fireball.rotationPitch);
							fireball.setPosition(x, y, z);
							double mod1, mod2, mod3;
							mod1 = mod3 = 0.0;
							mod2 = -0.2;
							double accelDenom = MathHelper.sqrt_double(mod1 * mod1 + mod2 * mod2 + mod3 * mod3);
							fireball.accelerationX = mod1 / accelDenom * 0.1;
							fireball.accelerationY = mod2 / accelDenom * 0.1;
							fireball.accelerationZ = mod3 / accelDenom * 0.1;
							world.spawnEntityInWorld(fireball);
						} else if (chance < 0.75) {
							List<EntityMob> entities = world.getEntitiesWithinAABB(EntityMob.class, bounds);
							if (entities.size() < 10 && !containsDemons(entities, 2)) {
								EntityDemon blaze = new EntityDemon(world);
								Infusion.spawnCreature(world, EntityDemon.class, MathHelper.floor_double(player.posX),
										MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ),
										player, 4, 8, ParticleEffect.SMOKE, SoundEffect.MOB_WITHER_DEATH);
							}
						}
					}
					List<EntityNightmare> entities = world.getEntitiesWithinAABB(EntityNightmare.class, bounds);
					for (EntityNightmare nightmare : entities) {
						if (nightmare.getVictimUUID().equals(player.getUniqueID())) {
							return;
						}
					}
					long currentTime = MinecraftServer.getCurrentTimeMillis();
					long lastKillTime = getPlayerLastNightmareKill(nbtPlayer);
					if (lastKillTime < currentTime - 30000L) {
						Infusion.spawnCreature(world, EntityNightmare.class, (int) player.posX, (int) player.posY,
								(int) player.posZ, player, 2, 6);
					}
				} else if (player.dimension != Config.instance().dimensionDreamID && isPlayerGhost(nbtPlayer)) {
					int timeRemaining = 0;
					boolean skipNext = getSkipNextManifestationReductions(nbtPlayer);
					if (nbtPlayer.hasKey(SW_MANIFEST_TIME)) {
						timeRemaining = nbtPlayer.getInteger(SW_MANIFEST_TIME);
						timeRemaining = Math.max(0, timeRemaining - 5);
						if (((timeRemaining >= 60 && timeRemaining <= 64)
								|| (timeRemaining >= 30 && timeRemaining <= 34)
								|| (timeRemaining >= 15 && timeRemaining <= 19)) && !skipNext) {
							ChatUtilities.sendTranslated(TextFormatting.LIGHT_PURPLE, player,
									Reference.MANIFESTATION_COUNTDOWN, Integer.valueOf(timeRemaining).toString());
						}
					}
					if (timeRemaining == 0) {
						if (nbtPlayer.hasKey(SW_MANIFEST_TIME)) {
							nbtPlayer.removeTag(SW_MANIFEST_TIME);
						}
						returnGhostPlayerToSpiritWorld(player);
					} else if (!skipNext) {
						nbtPlayer.setInteger(SW_MANIFEST_TIME, timeRemaining);
					} else {
						skipNextManifestationReduction(nbtPlayer, false);
					}
				}
			}
		}
	}

	public static void skipNextManifestationReduction(EntityPlayer player) {
		skipNextManifestationReduction(Infusion.getNBT(player), true);
	}

	public static void skipNextManifestationReduction(NBTTagCompound nbtPlayer, boolean skip) {
		nbtPlayer.setBoolean(SW_MANIFEST_SKIP_TIME_TICK, skip);
	}

	public static boolean getSkipNextManifestationReductions(NBTTagCompound nbtPlayer) {
		return nbtPlayer.getBoolean(SW_MANIFEST_SKIP_TIME_TICK);
	}

	public static void playerIsGhost(NBTTagCompound nbtPlayer, boolean ghost) {
		nbtPlayer.setBoolean(SW_MANIFEST_GHOST, ghost);
	}

	public static boolean isPlayerGhost(EntityPlayer player) {
		return isPlayerGhost(Infusion.getNBT(player));
	}

	public static boolean isPlayerGhost(NBTTagCompound nbtPlayer) {
		return nbtPlayer.getBoolean(SW_MANIFEST_GHOST);
	}

	private static boolean containsDemons(List entities, int max) {
		int count = 0;
		for (Object obj : entities) {
			if (obj instanceof EntityDemon && ++count >= max) {
				return true;
			}
		}
		return false;
	}

	public static void setPlayerMustAwaken(EntityPlayer player, boolean awaken) {
		setPlayerMustAwaken(Infusion.getNBT(player), awaken);
	}

	public static void setPlayerMustAwaken(NBTTagCompound nbtPlayer, boolean ghost) {
		nbtPlayer.setBoolean(SW_AWAKEN_PLAYER, ghost);
	}

	public static boolean setPlayerMustAwaken(EntityPlayer player) {
		return getPlayerMustAwaken(Infusion.getNBT(player));
	}

	public static boolean getPlayerMustAwaken(NBTTagCompound nbtPlayer) {
		return nbtPlayer.getBoolean(SW_AWAKEN_PLAYER);
	}

	public static boolean canPlayerManifest(EntityPlayer player) {
		NBTTagCompound nbtPlayer = Infusion.getNBT(player);
		int timeRemaining = 0;
		if (nbtPlayer != null && nbtPlayer.hasKey(SW_MANIFEST_TIME)) {
			timeRemaining = nbtPlayer.getInteger(SW_MANIFEST_TIME);
		}
		return timeRemaining >= 5;
	}
}