package nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;
import nz.castorgaming.fantasyoverhaul.util.Reference;

public class ExtendedPlayerProvider implements ICapabilitySerializable<NBTTagCompound>, ICapabilityProvider {

	public static final ResourceLocation NAME = new ResourceLocation(Reference.MODID, "ExtendedVillager");

	private final ExtendedPlayer cap = new ExtendedPlayer();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityInit.EXTENDED_PLAYER;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityInit.EXTENDED_PLAYER) {
			return CapabilityInit.EXTENDED_PLAYER.cast(cap);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return cap.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		cap.deserializeNBT(nbt);
	}

}
