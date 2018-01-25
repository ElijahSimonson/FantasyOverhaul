package nz.castorgaming.fantasyoverhaul.capabilities.extendedVillager;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IExtendVillager extends INBTSerializable<NBTTagCompound> {

	public int getBlood();

	public void giveBlood(int quantity);

	public void incrementSleepingTicks();

	public boolean isClientSynced();

	public boolean isSleeping();

	public void setBlood(int blood);

	public void setSleeping(boolean sleeping);

	public void sync();

	public int takeBlood(int quantity, EntityLivingBase player);

	public void setVillager(EntityVillager entity);

}
