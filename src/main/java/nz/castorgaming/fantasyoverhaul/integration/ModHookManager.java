package nz.castorgaming.fantasyoverhaul.integration;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Log;

public class ModHookManager {

	private ArrayList<ModHook> hooks;
	public boolean isTinkersPresent, isAM2Present, isMorphPresent;

	public ModHookManager() {
		hooks = new ArrayList<ModHook>();
	}

	public void register(Class<? extends ModHook> clazz) {
		try {
			ModHook hook = clazz.newInstance();
			hooks.add(hook);
		} catch (Throwable e) {
			Log.instance().warning(e, "unhandled exception loading ModHook");
		}
	}

	public void init() {
		for (ModHook hook : hooks) {
			try {
				hook.init();
			} catch (Throwable e) {
				Log.instance().warning(e, String.format("unhandled exeption init for hook %s", hook.getModID()));
			}
		}
	}

	public void postInit() {
		for (ModHook hook : hooks) {
			try {
				hook.postInit();
			} catch (Throwable e) {
				Log.instance().warning(e, String.format("unhandled execption post init for hook %s", hook.getModID()));
			}
		}
	}

	public void reducePowerLevels(final EntityLivingBase entity, final float reduction) {
		if (entity == null || entity.worldObj == null || entity.worldObj.isRemote) {
			return;
		}
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			final int maxEnergy = Infusion.getMaxEnergy(player);
			final int currentEnergy = Infusion.getCurrentPower(player);
			if (maxEnergy > 0 && currentEnergy > 0) {
				final int reduceBy = Math.max((int) (maxEnergy * reduction), 1);
				final int newMana = Math.max(currentEnergy - reduceBy, 0);
				Infusion.setCurrentEnergy(player, newMana);
			}
		}
		for (final ModHook hook : this.hooks) {
			try {
				hook.reduceMagicPower(entity, reduction);
			} catch (Throwable e) {
				Log.instance().warning(e, String.format("unhandled exception post init for hook %s", hook.getModID()));
			}
		}
	}

	public void boostBloodPowers(final EntityPlayer player, final float health) {
		for (final ModHook hook : this.hooks) {
			try {
				hook.boostBloodPowers(player, health);
			} catch (Throwable e) {
				Log.instance().warning(e, String.format("unhandled exception post init for hook %s", hook.getModID()));
			}
		}
	}

	public boolean canVampireBeKilled(final EntityPlayer player) {
		for (final ModHook hook : this.hooks) {
			try {
				if (hook.canVampireBeKilled(player)) {
					return true;
				}
				continue;
			} catch (Throwable e) {
				Log.instance().warning(e, String.format("unhandled exception post init for hook %s", hook.getModID()));
			}
		}
		return false;
	}

	public void makeItemModProof(final ItemStack stack) {
		for (final ModHook hook : this.hooks) {
			try {
				hook.tryMakeItemModProof(stack);
			} catch (Throwable e) {
				Log.instance().warning(e, String.format("unhandled exception post init for hook %s", hook.getModID()));
			}
		}
	}

}
