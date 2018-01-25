package nz.castorgaming.fantasyoverhaul.powers.playereffect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;

public abstract class PlayerEffect extends IForgeRegistryEntry.Impl<PlayerEffect> {
	protected final String unlocalizedName;

	protected PlayerEffect(final String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		setRegistryName("playereffect_" + unlocalizedName);
		InitArrays.PLAYER_EFFECTS.add(this);
	}

	public void interact(final NBTTagCompound nbtEffects, final PlayerInteractEvent event, final EntityPlayer player) {
		if (nbtEffects.hasKey(this.unlocalizedName)) {
			this.doInteract(player, event);
		}
	}

	protected abstract void doInteract(final EntityPlayer p0, final PlayerInteractEvent p1);

	public void harvest(final NBTTagCompound nbtEffects, final BlockEvent.HarvestDropsEvent event, final EntityPlayer player) {
		if (nbtEffects.hasKey(this.unlocalizedName)) {
			this.doHarvest(player, event);
		}
	}

	protected abstract void doHarvest(final EntityPlayer p0, final BlockEvent.HarvestDropsEvent p1);

	public void applyTo(final EntityPlayer player, final int durationTicks) {
		final NBTTagCompound nbtPlayer = Infusion.getNBT(player);
		if (nbtPlayer != null) {
			if (!nbtPlayer.hasKey(Reference.PLAYER_EFFECT_KEY)) {
				nbtPlayer.setTag(Reference.PLAYER_EFFECT_KEY, new NBTTagCompound());
			}
			final NBTTagCompound nbtEffects = nbtPlayer.getCompoundTag(Reference.PLAYER_EFFECT_KEY);
			nbtEffects.setInteger(this.unlocalizedName, durationTicks);
		}
	}

	public void removeFrom(final NBTTagCompound nbtEffects) {
		if (nbtEffects.hasKey(this.unlocalizedName)) {
			nbtEffects.removeTag(this.unlocalizedName);
		}
	}

	public void update(final NBTTagCompound nbtEffects, final int ticks, final EntityPlayer player) {
		if (nbtEffects.hasKey(this.unlocalizedName)) {
			final int remainingTicks = nbtEffects.getInteger(this.unlocalizedName);
			final int newTicks = Math.max(remainingTicks - ticks, 0);
			if (newTicks == 0) {
				this.removeFrom(nbtEffects);
			}
			else {
				nbtEffects.setInteger(this.unlocalizedName, newTicks);
				this.doUpdate(player, ticks);
			}
		}
	}

	protected abstract void doUpdate(final EntityPlayer p0, final int p1);

	protected abstract void onDeath(EntityPlayer player);
}