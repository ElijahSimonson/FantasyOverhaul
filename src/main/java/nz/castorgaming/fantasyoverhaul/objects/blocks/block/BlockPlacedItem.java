package nz.castorgaming.fantasyoverhaul.objects.blocks.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.blocks.BlockBaseContainer;
import nz.castorgaming.fantasyoverhaul.objects.tileEntity.TileEntityPlacedItem;

public class BlockPlacedItem extends BlockBaseContainer {

	AxisAlignedBB BOUNDS = new AxisAlignedBB(0.2f, 0.2f, 0.2f, 0.8f, 0.05f, 0.8f);

	public BlockPlacedItem(String name, Material material) {
		super(name, material);
	}

	public static void placeItemInWorld(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
		int meta = 0;
		if (player != null) {
			int look = MathHelper.floor_double(player.rotationYaw * 4.0f / 360.0f + 0.5);
			switch (look) {
			case 0:
				meta = 2;
				break;
			case 1:
				meta = 5;
				break;
			case 2:
				meta = 3;
				break;
			case 3:
				meta = 4;
				break;
			}
		}
		world.setBlockState(pos, BlockInit.PLACED_ITEMSTACK.getStateFromMeta(meta));
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityPlacedItem) {
			((TileEntityPlacedItem) tile).setStack(stack);
		}
	}

	public BlockPlacedItem(String name) {
		super(name, Material.GROUND);
		super.setCreativeTab(null);
		setHardness(0.0f);
		setSoundType(SoundType.METAL);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return BOUNDS;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			worldIn.setBlockState(pos, state, 4);
		}
		dropBlockAsItem(worldIn, pos, state, 0);
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityPlacedItem && ((TileEntityPlacedItem) tile).getStack() != null) {
			drops.add(((TileEntityPlacedItem) tile).getStack());
		}
		return drops;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityPlacedItem && ((TileEntityPlacedItem) tile).getStack() != null) {
			return ((TileEntityPlacedItem) tile).getStack().copy();
		}
		return new ItemStack(ItemInit.ARTHANA);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		if (world instanceof World) {
			World worldIn = (World) world;
			if (!canBlockStay(worldIn, pos)) {
				if (!worldIn.isRemote) {
					dropBlockAsItem(worldIn, pos, getDefaultState(), 0);
					worldIn.setBlockToAir(pos);
				}
			}
		}

	}

	public boolean canBlockStay(World world, BlockPos pos) {
		Material mat = world.getBlockState(pos.down()).getMaterial();
		return !world.isAirBlock(pos.down()) && mat != null && mat.isOpaque() && mat.isSolid();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		return false;
	}
}
