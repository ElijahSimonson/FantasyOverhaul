package nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;
import nz.castorgaming.fantasyoverhaul.util.classes.Coord;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;

public interface IExtendPlayer extends INBTSerializable<NBTTagCompound> {

	public void setPlayer(EntityPlayer player);

	public EntityPlayer getPlayer();

	public static void loadProxyData(EntityPlayer player) {
	}

	public static ExtendedPlayer get(EntityPlayer player) {
		return (ExtendedPlayer) player.getCapability(CapabilityInit.EXTENDED_PLAYER, null);
	}

	public void addWorship(int level);

	public void backupPlayerInventory();

	public boolean cacheIncurablePotionEffect(Collection<PotionEffect> activePotionEffects);

	public void cachePlayerInventory();

	public boolean canEscapeMirrorWorld(int slot);

	public void clearCachedIncurablePotionEffect(Potion potion);

	public void escapedMirrorWorld(int slot);

	public long getCooldownSecs(int i);

	public TransformCreatures getCreatureType();

	@SideOnly(Side.CLIENT)
	public ResourceLocation getLocationSkin();

	public Coord getMirrorWorldEntryPoint();

	public UUID getOtherPlayerSkin();

	public ResourceLocation getOtherPlayerSkinLocation();

	public int getSkillPotionBottling();

	public int getSkillPotionThrowing();

	public int increaseSkillPotionBottling();

	public int increaseSkillPotionThrowing();

	public boolean isMirrorWorldEntryPoint(int x, int y, int z);

	public void restoreIncurablePotionEffects();

	public void restorePlayerInventoryFrom(ExtendedPlayer original);

	public void setCreatureType(TransformCreatures type);

	public void setCreatureTypeOrdinal(int type);

	public void setMirrorWorldEntryPoint(int x, int y, int z);

	public void setOtherPlayerSkin(UUID uuid);

	@SideOnly(Side.CLIENT)
	public void setupCustomSkin();

	public void updateWorship();

	public Map<PlayerType, Integer> getLevels();

	public enum PlayerType {
		VAMPIRE, WEREWOLF, NONE;
	}

}