package nz.castorgaming.fantasyoverhaul.objects.blocks.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.blocks.BlockDoorBase;

public class BlockDoorRowan extends BlockDoorBase{

	
	public BlockDoorRowan(String name, Material material) {
		super(name, material);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		if (!worldIn.isRemote) {
			ItemStack key = ItemInit.DOOR_KEY.createStack();
			if (!key.hasTagCompound()) {
				key.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound nbt = key.getTagCompound();
			nbt.setInteger("doorX", pos.getX());
			nbt.setInteger("doorY", pos.getY());
			nbt.setInteger("doorZ", pos.getZ());
			nbt.setInteger("doorD", worldIn.provider.getDimension());
			nbt.setString("doorDN", worldIn.provider.getDimensionType().name());
			EntityItem keyItem = new EntityItem(worldIn);
			keyItem.setEntityItemStack(key);
			worldIn.spawnEntityInWorld(keyItem);
		}
	}
}
