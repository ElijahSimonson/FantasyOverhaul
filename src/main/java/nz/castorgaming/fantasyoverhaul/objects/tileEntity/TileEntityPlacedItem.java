package nz.castorgaming.fantasyoverhaul.objects.tileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPlacedItem extends TileEntity{

	private static final String ITEM_KEY = "FOPlacedItem";
	private ItemStack stack;
	
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (stack != null) {
			NBTTagCompound item = new NBTTagCompound();
			stack.writeToNBT(item);
			compound.setTag(ITEM_KEY, item);
		}
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey(ITEM_KEY)) {
			NBTTagCompound item = compound.getCompoundTag(ITEM_KEY);
			ItemStack stackNBT = ItemStack.loadItemStackFromNBT(item);
			stack = stackNBT;
		}
	}
	
	public void setStack(ItemStack stackIn) {
		stack = stackIn;
		if (!worldObj.isRemote) {
			worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}
	
	public ItemStack getStack() {
		return stack;
	}
	
	public Packet<INetHandlerPlayClient> getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 1, nbt);
	}
	
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());
		worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
	}
	
}
