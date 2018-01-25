package nz.castorgaming.fantasyoverhaul.capabilities.extendedVillager;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ExtVillagerStorage implements IStorage<IExtendVillager> {

	@Override
	public NBTTagCompound writeNBT(Capability<IExtendVillager> capability, IExtendVillager instance, EnumFacing side) {
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<IExtendVillager> capability, IExtendVillager instance, EnumFacing side,
			NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			instance.deserializeNBT((NBTTagCompound) nbt);
		}
	}

}
