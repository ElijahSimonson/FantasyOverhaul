package nz.castorgaming.fantasyoverhaul.powers.brews.effects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;

public abstract class InfusionBrewEffect extends IForgeRegistryEntry.Impl<InfusionBrewEffect> {

	private long durationTicks;
	private String effectName;
	private static IForgeRegistry<InfusionBrewEffect> REGISTRY = GameRegistry.findRegistry(InfusionBrewEffect.class);

	protected InfusionBrewEffect(int id, long durationMS, String name) {
		this.durationTicks = durationMS;
		effectName = name;
		setRegistryName(effectName);
		InitArrays.BREW_EFFECT.add(this);
	}

	public void drunk(World world, EntityPlayer player, ItemStack stack) {
		setActiveBrew(this, player, true);
		immediateEffect(world, player, stack);
	}

	public abstract void immediateEffect(World world, EntityPlayer player, ItemStack stack);

	public abstract void regularEffect(World world, EntityPlayer player);

	public boolean tryUseEffect(World world, EntityPlayer player) {
		return isActive(player);
	}

	public boolean isActive(EntityPlayer player) {
		return getActiveBrew(player) == this;
	}

	public static InfusionBrewEffect getActiveBrew(EntityPlayer player) {
		if (player != null) {
			NBTTagCompound nbtPlayer = Infusion.getNBT(player);
			return getActiveBrew(nbtPlayer);
		}
		return null;
	}

	public static InfusionBrewEffect getActiveBrew(NBTTagCompound nbtPlayer) {
		if (nbtPlayer != null) {
			String brewName = nbtPlayer.getString(Reference.BREW_TYPE_KEY);
			if (brewName != null && !brewName.isEmpty()) {
				return REGISTRY.getValue(new ResourceLocation(Reference.MODID, brewName));
			}
		}
		return null;
	}

	public static long getActiveBrewStartTime(NBTTagCompound nbt) {
		if (nbt != null) {
			return nbt.getLong(Reference.BREW_START_KEY);

		}
		return 0L;
	}

	public static String getMinutesRemaining(World world, NBTTagCompound nbt, InfusionBrewEffect effect) {
		if (nbt != null) {
			long minsLeft = nbt.getLong(Reference.BREW_REMAINING_KEY);
			return String.format("%d", minsLeft);
		}
		return "";
	}

	public static void setActiveBrew(InfusionBrewEffect brew, EntityPlayer player, boolean sync) {
		if (player != null) {
			NBTTagCompound nbt = Infusion.getNBT(player);
			setActiveBrew(player.worldObj, player, nbt, brew, sync);
		}
	}

	public static void setActiveBrew(World world, EntityPlayer player, NBTTagCompound nbt, InfusionBrewEffect brew, boolean sync) {
		if (nbt != null && !world.isRemote) {
			nbt.setString(Reference.BREW_TYPE_KEY, brew.effectName);
			nbt.setLong(Reference.BREW_START_KEY, TimeUtilities.getServerTimeInTicks());
			if (sync) {
				Infusion.syncPlayer(world, player);
			}
		}
	}

	public static void setActiveBrewInfo(NBTTagCompound nbt, InfusionBrewEffect brew, long startTime) {
		nbt.setString(Reference.BREW_START_KEY, brew.effectName);
		nbt.setLong(Reference.BREW_START_KEY, startTime);
	}

	public static void setActiveBrewInfo(NBTTagCompound nbt, String brewName, long startTime) {
		nbt.setString(Reference.BREW_START_KEY, brewName);
		nbt.setLong(Reference.BREW_START_KEY, startTime);
	}

	public static void checkActiveEffects(World world, EntityPlayer player, NBTTagCompound nbt, boolean sync, long currentTime) {
		if (nbt != null && !world.isRemote) {
			InfusionBrewEffect activeEffect = getActiveBrew(nbt);
			if (activeEffect != null) {
				long startTime = nbt.getLong(Reference.BREW_START_KEY);
				if (currentTime > startTime + activeEffect.durationTicks) {
					nbt.removeTag(Reference.BREW_START_KEY);
					nbt.removeTag(Reference.BREW_TYPE_KEY);
					Infusion.syncPlayer(world, player);
					return;
				}
				activeEffect.regularEffect(world, player);
				if (sync) {
					Infusion.syncPlayer(world, player);
				}
			}
		}
	}

	public String getEffectName() {
		return effectName;
	}

	public long getDurationTicks() {
		return durationTicks;
	}

}
