package nz.castorgaming.fantasyoverhaul.capabilities.playerVampire;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;
import nz.castorgaming.fantasyoverhaul.util.Reference;

public class PlayerVampireProvider implements ICapabilitySerializable<NBTTagCompound>, ICapabilityProvider {

	public static final ResourceLocation NAME = new ResourceLocation(Reference.MODID, "PlayerVampire");

	private final PlayerVampire cap = new PlayerVampire();

	@Override
	public NBTTagCompound serializeNBT() {
		return cap.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		cap.deserializeNBT(nbt);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityInit.PLAYER_VAMPIRE;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (hasCapability(capability, facing)) {
			return CapabilityInit.PLAYER_VAMPIRE.cast(cap);
		}
		return null;
	}
}
