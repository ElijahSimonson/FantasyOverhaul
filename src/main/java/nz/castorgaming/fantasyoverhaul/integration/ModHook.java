package nz.castorgaming.fantasyoverhaul.integration;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import nz.castorgaming.fantasyoverhaul.util.Log;
import nz.castorgaming.fantasyoverhaul.util.configuration.ModConfig;

public abstract class ModHook {

	private boolean initialized;

	public ModHook() {
		initialized = false;
	}

	public abstract String getModID();

	public void init() {
		initialized = (ModConfig.instance().allowModIntegration && Loader.isModLoaded(getModID()));
		if (initialized) {
			doInit();
			Log.instance().debug(String.format("Mod: %s support initialized", getModID()));
		} else {
			Log.instance().debug(String.format("Mod: %s not found", getModID()));
		}
	}

	protected abstract void doInit();

	public void postInit() {
		if (initialized) {
			doPostInit();
			Log.instance().debug(String.format("Mod: %s support post initilized", getModID()));
		}
	}

	protected abstract void doPostInit();

	public void reduceMagicPower(final EntityLivingBase entity, final float factor) {
		if (initialized) {
			doReduceMagicPower(entity, factor);
		}
	}

	protected abstract void doReduceMagicPower(final EntityLivingBase p0, final float p1);

	public void boostBloodPowers(final EntityPlayer player, final float health) {
	}

	public boolean canVampireBeKilled(final EntityPlayer player) {
		return false;
	}

	public void tryMakeItemModProof(final ItemStack stack) {
		if (initialized) {
			makeItemModProof(stack);
		}
	}

	protected void makeItemModProof(final ItemStack stack) {
	}

}
