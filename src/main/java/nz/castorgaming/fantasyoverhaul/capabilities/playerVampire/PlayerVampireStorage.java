package nz.castorgaming.fantasyoverhaul.capabilities.playerVampire;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerVampireStorage implements IStorage<IPlayerVampire> {
	@Override
	public void readNBT(Capability<IPlayerVampire> capability, IPlayerVampire instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			instance.deserializeNBT((NBTTagCompound) nbt);
		}

	}

	@Override
	public NBTTagCompound writeNBT(Capability<IPlayerVampire> capability, IPlayerVampire instance, EnumFacing side) {
		return instance.serializeNBT();
	}

}
