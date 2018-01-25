package nz.castorgaming.fantasyoverhaul.util.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.powers.playereffect.PlayerEffect;
import nz.castorgaming.fantasyoverhaul.util.Reference;

@EventBusSubscriber
public class PlayerEffectHandler {
	private static final int TICKS_PER_UPDATE = 20;

	@SubscribeEvent
	public static void onDeath(final EntityPlayer player) {
		final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
		if (nbtPlayer != null && nbtPlayer.hasKey(Reference.PLAYER_EFFECT_KEY)) {
			final NBTTagCompound nbtEffects = nbtPlayer.getCompoundTag(Reference.PLAYER_EFFECT_KEY);
			for (final PlayerEffect effect : InitArrays.PLAYER_EFFECTS) {
				effect.removeFrom(nbtEffects);
			}
			if (nbtEffects.hasNoTags()) {
				nbtPlayer.removeTag(Reference.PLAYER_EFFECT_KEY);
			}
		}
	}

	@SubscribeEvent
	public static void onUpdate(final EntityPlayer player, final long ticks) {
		if (ticks % 20L == 3L) {
			final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
			if (nbtPlayer != null && nbtPlayer.hasKey(Reference.PLAYER_EFFECT_KEY)) {
				final NBTTagCompound nbtEffects = nbtPlayer.getCompoundTag(Reference.PLAYER_EFFECT_KEY);
				for (final PlayerEffect effect : InitArrays.PLAYER_EFFECTS) {
					effect.update(nbtEffects, 20, player);
				}
				if (nbtEffects.hasNoTags()) {
					nbtPlayer.removeTag(Reference.PLAYER_EFFECT_KEY);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onHarvestDrops(final EntityPlayer player, final BlockEvent.HarvestDropsEvent event) {
		final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
		if (nbtPlayer != null && nbtPlayer.hasKey(Reference.PLAYER_EFFECT_KEY)) {
			final NBTTagCompound nbtEffects = nbtPlayer.getCompoundTag(Reference.PLAYER_EFFECT_KEY);
			for (final PlayerEffect effect : InitArrays.PLAYER_EFFECTS) {
				effect.harvest(nbtEffects, event, player);
			}
			if (nbtEffects.hasNoTags()) {
				nbtPlayer.removeTag(Reference.PLAYER_EFFECT_KEY);
			}
		}
	}

	@SubscribeEvent
	public static void onInteract(final EntityPlayer player, final PlayerInteractEvent event) {
		final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
		if (nbtPlayer != null && nbtPlayer.hasKey(Reference.PLAYER_EFFECT_KEY)) {
			final NBTTagCompound nbtEffects = nbtPlayer.getCompoundTag(Reference.PLAYER_EFFECT_KEY);
			for (final PlayerEffect effect : InitArrays.PLAYER_EFFECTS) {
				effect.interact(nbtEffects, event, player);
			}
			if (nbtEffects.hasNoTags()) {
				nbtPlayer.removeTag(Reference.PLAYER_EFFECT_KEY);
			}
		}
	}
}
