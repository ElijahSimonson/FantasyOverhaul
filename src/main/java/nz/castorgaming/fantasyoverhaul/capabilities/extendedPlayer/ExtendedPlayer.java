package nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.capabilities.PlayerCapabilityMaster;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.PlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.client.renderer.RenderReflection;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.ChatUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.Coord;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketPlayerStyle;

public class ExtendedPlayer extends PlayerCapabilityMaster implements IExtendPlayer {

	private static final int MAX_SKILL_LEVEL_POTION_BOTTLING = 100;
	private static final int MAX_SKILL_LEVEL_POTION_THROWING = 100;
	
	public PlayerVampire vampire;
	public PlayerWerewolf werewolf;

	static final long COOLDOWN_ESCAPE_1_TICKS;
	static final long COOLDOWN_ESCAPE_2_TICKS;

	static {
		COOLDOWN_ESCAPE_1_TICKS = TimeUtilities.minsToTicks(3);
		COOLDOWN_ESCAPE_2_TICKS = TimeUtilities.minsToTicks(3);
	}

	public static void loadProxyData(EntityPlayer player) {
		if (player != null) {
			ExtendedPlayer playerEx = ExtendedPlayer.get(player);
			playerEx.sync();
		}
	}

	private Hashtable<Potion, PotionEffect> incurablePotionEffectCache;
	private int skillLevelPotionBottling;
	private int skillLevelPotionThrowing;
	private TransformCreatures creatureType;

	@SideOnly(Side.CLIENT)
	private ThreadDownloadImageData downloadImageSkin;

	private ResourceLocation locationSkin;
	private UUID lastPlayerSkin;
	private NBTTagList cachedInventory;

	private boolean inventoryCanBeRestored;
	public int highlightTicks;

	public int cachedWorship = -1;
	boolean getPlayerData;
	boolean resetSleep;
	int cachedSky;

	private Coord mirrorWorldEntryPoint;

	long mirrorWorldEscapeCooldown1;
	long mirrorWorldEscapeCooldown2;

	public ExtendedPlayer() {
		incurablePotionEffectCache = new Hashtable<Potion, PotionEffect>();
		cachedWorship = -1;
		mirrorWorldEscapeCooldown1 = Long.MIN_VALUE;
		mirrorWorldEscapeCooldown2 = Long.MAX_VALUE;
	}

	@Override
	public void addWorship(int level) {
		cachedWorship = level;
	}

	@Override
	public void backupPlayerInventory() {
		NBTTagList nbtInventory = new NBTTagList();
		player.inventory.writeToNBT(nbtInventory);
		cachedInventory = nbtInventory;
	}

	@Override
	public boolean cacheIncurablePotionEffect(Collection<PotionEffect> activePotionEffects) {
		boolean cached = false;

		for (PotionEffect effect : activePotionEffects) {
			if (effect.getPotion() instanceof PotionBase && effect.getDuration() > 5) {
				PotionBase potion = (PotionBase) effect.getPotion();
				if (potion.isCurable()) {
					continue;
				}
				incurablePotionEffectCache.put(effect.getPotion(), effect);
				cached = true;
			}
		}

		return cached;
	}

	@Override
	public void cachePlayerInventory() {
		inventoryCanBeRestored = true;
	}

	@Override
	public boolean canEscapeMirrorWorld(int slot) {
		if (slot == 1) {
			return player.worldObj.getTotalWorldTime() >= mirrorWorldEscapeCooldown1 + COOLDOWN_ESCAPE_1_TICKS;
		}
		return slot == 2 && player.worldObj.getTotalWorldTime() >= mirrorWorldEscapeCooldown2 + COOLDOWN_ESCAPE_2_TICKS;
	}

	@Override
	public void clearCachedIncurablePotionEffect(Potion potion) {
		incurablePotionEffectCache.remove(potion);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		skillLevelPotionBottling = nbt.getInteger("PotionBottling");
		skillLevelPotionThrowing = nbt.getInteger("PotionThrowing");
		creatureType = TransformCreatures.fromInt(nbt.getInteger("CreatureType"));
		if (nbt.hasKey("CachedInventory")) {
			cachedInventory = nbt.getTagList("CachedInvetory", 10);
			inventoryCanBeRestored = nbt.getBoolean("CanRestoreInventory");
		}
		if (mirrorWorldEntryPoint != null) {
			mirrorWorldEntryPoint = Coord.fromTagNBT(nbt.getCompoundTag("mirrorWorldEntryPoint"));
		}
		if (lastPlayerSkin != null) {
			lastPlayerSkin = nbt.getUniqueId("lastPlayerSkin");
		}
		mirrorWorldEscapeCooldown1 = nbt.getLong("MirrorEscape1");
		mirrorWorldEscapeCooldown2 = nbt.getLong("MirrorEscape2");
	}

	@Override
	public void escapedMirrorWorld(int slot) {

	}

	@Override
	public long getCooldownSecs(int i) {
		if (i == 1) {
			return mirrorWorldEscapeCooldown1 + COOLDOWN_ESCAPE_1_TICKS - player.worldObj.getTotalWorldTime() / 20L;
		}
		if (i == 2) {
			return mirrorWorldEscapeCooldown2 + COOLDOWN_ESCAPE_2_TICKS - player.worldObj.getTotalWorldTime() / 20L;
		}
		return 0L;
	}

	@Override
	public TransformCreatures getCreatureType() {
		return creatureType;
	}

	@SideOnly(Side.CLIENT)
	private ThreadDownloadImageData getDownloadImageSkin(ResourceLocation location, String name) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		Object object = textureManager.getTexture(location);
		if (object == null) {
			object = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(name)), RenderReflection.SKIN,
					new ImageBufferDownload());
			textureManager.loadTexture(location, (ITextureObject) object);
		}
		return (ThreadDownloadImageData) object;
	}

	@Override
	public ResourceLocation getLocationSkin() {
		if (locationSkin == null) {
			setupCustomSkin();
		}
		if (locationSkin != null) {
			return locationSkin;
		}

		return null;
	}

	@Override
	public Coord getMirrorWorldEntryPoint() {
		return mirrorWorldEntryPoint;
	}

	@Override
	public UUID getOtherPlayerSkin() {
		return lastPlayerSkin != null ? lastPlayerSkin : null;
	}

	@Override
	public ResourceLocation getOtherPlayerSkinLocation() {
		return getLocationSkin();
	}

	@Override
	public int getSkillPotionBottling() {
		return skillLevelPotionBottling;
	}

	@Override
	public int getSkillPotionThrowing() {
		return skillLevelPotionThrowing;
	}

	@Override
	public int increaseSkillPotionBottling() {
		skillLevelPotionBottling = Math.min(skillLevelPotionBottling + 1, MAX_SKILL_LEVEL_POTION_BOTTLING);
		if (skillLevelPotionBottling == 30 || skillLevelPotionBottling == 60) {
			ChatUtilities.sendTranslated(player, Reference.BREW_SKILL_INCREASE, new Object[0]);
		}
		return skillLevelPotionBottling;
	}

	@Override
	public int increaseSkillPotionThrowing() {
		skillLevelPotionThrowing = Math.min(skillLevelPotionThrowing + 1, MAX_SKILL_LEVEL_POTION_THROWING);
		return getSkillPotionThrowing();
	}

	@Override
	public boolean isMirrorWorldEntryPoint(int x, int y, int z) {
		return mirrorWorldEntryPoint != null && mirrorWorldEntryPoint.isMatch(x, y, z);
	}

	@Override
	public void restoreIncurablePotionEffects() {
		if (incurablePotionEffectCache.size() > 0) {
			Collection<PotionEffect> activeEffectList = player.getActivePotionEffects();
			for (PotionEffect effect : activeEffectList) {
				incurablePotionEffectCache.remove(effect.getPotion());
			}
			for (PotionEffect restoredEffect : incurablePotionEffectCache.values()) {
				player.addPotionEffect(restoredEffect);
			}
			incurablePotionEffectCache.clear();
		}
	}

	@Override
	public void restorePlayerInventoryFrom(ExtendedPlayer original) {
		if (original != null && cachedInventory != null && inventoryCanBeRestored) {
			player.inventory.readFromNBT(original.cachedInventory);
			inventoryCanBeRestored = false;
			cachedInventory = null;
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("PotionBottling", skillLevelPotionBottling);
		tag.setInteger("PotionThrowing", skillLevelPotionThrowing);
		tag.setInteger("CreatureType", TransformCreatures.toInt(creatureType));
		if (cachedInventory != null) {
			tag.setTag("CachedInvetory", cachedInventory.copy());
			tag.setBoolean("CanRestoreInventory", inventoryCanBeRestored);
		}
		if (mirrorWorldEntryPoint != null) {
			tag.setTag("mirrorWorldEntryPoint", mirrorWorldEntryPoint.toTagNBT());
		}
		if (lastPlayerSkin != null) {
			tag.setUniqueId("lastPlayerSkin", lastPlayerSkin);
		}
		tag.setLong("MirrorEscape1", mirrorWorldEscapeCooldown1);
		tag.setLong("MirrorEscape2", mirrorWorldEscapeCooldown2);
		return tag;
	}

	@Override
	public void setCreatureType(TransformCreatures type) {
		creatureType = type;
	}

	@Override
	public void setCreatureTypeOrdinal(int type) {
		if (type != creatureType.toInt()) {
			creatureType = TransformCreatures.fromInt(type);
			if (!player.worldObj.isRemote) {
				Reference.PACKET_HANDLER.sendToAll(new PacketPlayerStyle(player));
			}
		}
	}

	@Override
	public void setMirrorWorldEntryPoint(int x, int y, int z) {
		mirrorWorldEntryPoint = new Coord(x, y, z);
	}

	@Override
	public void setOtherPlayerSkin(UUID uuid) {
		lastPlayerSkin = uuid;
		locationSkin = null;
		sync();

	}

	@Override
	public void setupCustomSkin() {
		UUID ownerUUID = getOtherPlayerSkin();
		if (ownerUUID != null) {
			String name = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(ownerUUID).getName();
			locationSkin = AbstractClientPlayer.getLocationSkin(name);
			downloadImageSkin = getDownloadImageSkin(locationSkin, name);
		}
		else {
			locationSkin = null;
			downloadImageSkin = null;
		}
	}

	@Override
	public void updateWorship() {
		if (cachedWorship >= 0) {
			player.addPotionEffect(new PotionEffect(Potions.WORSHIP, TimeUtilities.secsToTicks(60), cachedWorship, true, false));
			cachedWorship = -1;
		}
		processSync();
	}

	@Override
	public Map<PlayerType, Integer> getLevels() {
		Map<PlayerType, Integer> levels = new HashMap<PlayerType, Integer>();

		levels.put(PlayerType.VAMPIRE, vampire.getVampireLevel());
		levels.put(PlayerType.VAMPIRE, werewolf.getWerewolfLevel());
		return levels;
	}
	
	public static ExtendedPlayer get(EntityPlayer player) {
		return (ExtendedPlayer) player.getCapability(CapabilityInit.EXTENDED_PLAYER, null);
	}

}