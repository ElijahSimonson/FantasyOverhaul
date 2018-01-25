package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SuppressWarnings(value = { "deprecation" })
public class BlockUtil {

	public static Block getBlock(final World world, final int posX, final int posY, final int posZ) {
		return world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
	}

	public static Block getBlock(final World world, final double posX, final double posY, final double posZ) {
		final int x = MathHelper.floor_double(posX);
		final int y = MathHelper.floor_double(posY);
		final int z = MathHelper.floor_double(posZ);
		return getBlock(world, x, y, z);
	}

	public static Block getBlock(final World world, final RayTraceResult mop) {
		return getBlock(world, mop, false);
	}

	public static boolean isReplaceableBlock(final World world, final int posX, final int posY, final int posZ) {
		return isReplaceableBlock(world, posX, posY, posZ, null);
	}

	public static boolean isReplaceableBlock(final World world, final int posX, final int posY, final int posZ, final EntityLivingBase player) {
		final Block block = getBlock(world, posX, posY, posZ);
		IBlockState state = world.getBlockState(new BlockPos(posX, posY, posZ));
		if (player != null) {
			if (!BlockProtect.checkModsForBreakOK(world, posX, posY, posZ, state, player)) {
				return false;
			}
		}
		return block == null || block.getMaterial(state).isReplaceable();
	}

	public static Material getBlockMaterial(final EntityPlayer player) {
		return getBlockMaterial(player, 0);
	}

	public static Material getBlockMaterial(final EntityPlayer player, final int yOffset) {
		final int posX = MathHelper.floor_double(player.posX);
		final int posY = MathHelper.floor_double(player.getEntityBoundingBox().minY) + yOffset;
		final int posZ = MathHelper.floor_double(player.posZ);
		return getBlockMaterial(player.worldObj, posX, posY, posZ);
	}

	public static Material getBlockMaterial(final World world, final int posX, final int posY, final int posZ) {
		IBlockState state = world.getBlockState(new BlockPos(posX, posY, posZ));
		if (state != null) {
			return state.getMaterial();
		}
		return Material.AIR;
	}

	public static Block getBlock(final World world, final RayTraceResult mop, final boolean before) {
		if (mop == null) {
			return null;
		}
		if (mop.typeOfHit != RayTraceResult.Type.BLOCK) {
			final int posX = MathHelper.floor_double(mop.entityHit.posX);
			final int posY = MathHelper.floor_double(mop.entityHit.posY) - 1;
			final int posZ = MathHelper.floor_double(mop.entityHit.posZ);
			return getBlock(world, posX, posY, posZ);
		}
		if (before) {
			final int x = mop.getBlockPos().getX() + ((mop.sideHit == EnumFacing.NORTH) ? -1 : ((mop.sideHit == EnumFacing.SOUTH) ? 1 : 0));
			final int z = mop.getBlockPos().getZ() + ((mop.sideHit == EnumFacing.EAST) ? -1 : ((mop.sideHit == EnumFacing.WEST) ? 1 : 0));
			int y = mop.getBlockPos().getY() + ((mop.sideHit == EnumFacing.DOWN) ? -1 : ((mop.sideHit == EnumFacing.UP) ? 1 : 0));
			if (mop.sideHit == EnumFacing.UP && !getMaterial(world, new BlockPos(x, mop.getBlockPos().getY(), z)).isSolid()) {
				--y;
			}
			return getBlock(world, x, y, z);
		}
		return getBlock(world, mop.getBlockPos().getX(), mop.getBlockPos().getY(), mop.getBlockPos().getZ());
	}

	public static int[] getBlockCoords(final World world, final RayTraceResult mop, final boolean before) {
		if (mop == null) {
			return null;
		}
		if (mop.typeOfHit != RayTraceResult.Type.BLOCK) {
			final int posX = MathHelper.floor_double(mop.entityHit.posX);
			final int posY = MathHelper.floor_double(mop.entityHit.posY) - 1;
			final int posZ = MathHelper.floor_double(mop.entityHit.posZ);
			return new int[] { posX, posY, posZ };
		}
		if (before) {
			final int x = mop.getBlockPos().getX() + ((mop.sideHit == EnumFacing.NORTH) ? -1 : ((mop.sideHit == EnumFacing.SOUTH) ? 1 : 0));
			final int z = mop.getBlockPos().getZ() + ((mop.sideHit == EnumFacing.EAST) ? -1 : ((mop.sideHit == EnumFacing.WEST) ? 1 : 0));
			int y = mop.getBlockPos().getY() + ((mop.sideHit == EnumFacing.DOWN) ? -1 : ((mop.sideHit == EnumFacing.UP) ? 1 : 0));
			if (mop.sideHit == EnumFacing.UP && !getMaterial(world, mop.getBlockPos()).isSolid()) {
				--y;
			}
			return new int[] { x, y, z };
		}
		return new int[] { mop.getBlockPos().getX(), mop.getBlockPos().getY(), mop.getBlockPos().getZ() };
	}

	public static int getBlockMetadata(final World world, BlockPos pos) {
		final int blockMetadata = getBlock(world, pos).getMetaFromState(getState(world, pos));
		return blockMetadata;
	}

	public static <T> T getTileEntity(final IBlockAccess world, BlockPos pos, final Class<T> clazz) {
		return getTileEntity(world, pos.getX(), pos.getY(), pos.getZ(), clazz);
	}

	public static <T> T getTileEntity(final IBlockAccess world, final int posX, final int posY, final int posZ, final Class<T> clazz) {
		final TileEntity tile = world.getTileEntity(new BlockPos(posX, posY, posZ));
		if (tile != null && clazz.isAssignableFrom(tile.getClass())) {
			return clazz.cast(tile);
		}
		return null;
	}

	public static void setBlock(final World world, BlockPos pos, IBlockState state, final int updateFlags) {
		if (state != null) {
			world.setBlockState(pos, state, updateFlags);
		}
		else {
			world.setBlockToAir(pos);
		}
	}

	public static void setBlock(final World world, BlockPos pos, IBlockState state) {
		if (state != null) {
			world.setBlockState(pos, state);
		}
		else {
			world.setBlockToAir(pos);
		}

	}

	public static void setBlock(final World world, final double posX, final double posY, final double posZ, final Block block) {
		final int x = MathHelper.floor_double(posX);
		final int y = MathHelper.floor_double(posY);
		final int z = MathHelper.floor_double(posZ);
		setBlock(world, x, y, z, block);
	}

	public static void setMetadata(final World world, final int posX, final int posY, final int posZ, IBlockState newMetadata) {
		setMetadata(world, posX, posY, posZ, newMetadata, 3);
	}

	public static void setMetadata(final World world, final int posX, final int posY, final int posZ, IBlockState newMetadata, final int updateFlags) {
		world.setBlockState(new BlockPos(posX, posY, posZ), newMetadata, updateFlags);
	}

	public static void setAirBlock(final World world, final int x, final int y, final int z) {
		world.setBlockToAir(new BlockPos(x, y, z));
	}

	public static void notifyNeighborsOfBlockChange(final World world, BlockPos pos, Block blockType) {
		world.notifyNeighborsOfStateChange(pos, blockType);
	}

	/*
	 * public static void setBlockDefaultDirection(final World world, final int
	 * posX, final int posY, final int posZ) { if (!world.isRemote) { IBlockState
	 * state = getState(world, new BlockPos(posX, posY, posZ)); BlockPos pos = new
	 * BlockPos(posX, posY,posZ); boolean l = isOpaque(world, new BlockPos(posX,
	 * posY, posZ - 1)); boolean i1 = isOpaque(world, new BlockPos(posX, posY, posZ
	 * + 1)); boolean j1 = isOpaque(world, new BlockPos(posX - 1, posY, posZ));
	 * boolean k1 = isOpaque(world, new BlockPos(posX + 1, posY, posZ)); byte b0 =
	 * 3; if (l && !i1) { b0 = 3; } if (i1 && !l) { b0 = 2; } if (j1 && !k1) { b0 =
	 * 5; } if (k1 && !j1) { b0 = 4; } IBlockState newState =
	 * state.getBlock().getStateForPlacement(world, pos, facing, hitX, hitY, hitZ,
	 * meta, placer, stack) world.setBlockState(new BlockPos(posX, posY, posZ),
	 * newState, 2); } }
	 */

	public static boolean isSolid(final World world, final int posX, final int posY, final int posZ) {
		BlockPos pos = new BlockPos(posX, posY, posZ);
		Block block = getBlock(world, pos);
		return block != null && !getMaterial(world, pos).isReplaceable();
	}

	public static boolean isNormalCube(final Block block) {
		return block.getMaterial(block.getDefaultState()).blocksMovement() && EnumBlockRenderType.MODEL == block.getRenderType(block.getDefaultState());
	}

	public static Coord getClosestPlantableBlock(final World world, final int x, final int y, final int z, final EnumFacing side, final EntityLivingBase entity) {
		return getClosestPlantableBlock(world, x, y, z, side, entity, false);
	}

	public static Coord getClosestPlantableBlock(final World world, int x, int y, int z, final EnumFacing side, final EntityLivingBase entity, final boolean allowAir) {
		boolean foundBase = false;
		if (isReplaceableBlock(world, x, y, z) && (!allowAir || !world.isAirBlock(new BlockPos(x, y, z)))) {
			do {
				--y;
			} while (isReplaceableBlock(world, x, y, z));
			foundBase = true;
		}
		else if (side == EnumFacing.UP || side == null) {
			foundBase = true;
		}
		else if (side != EnumFacing.DOWN) {
			x += side.getFrontOffsetX();
			z += side.getFrontOffsetZ();
			if (isReplaceableBlock(world, x, y, z)) {
				--y;
				foundBase = !isReplaceableBlock(world, x, y, z);
			}
		}
		if (foundBase) {
			final IBlockState replaceMeta = getState(world, new BlockPos(x, y + 1, z));
			if (BlockProtect.checkModsForBreakOK(world, x, y + 1, z, replaceMeta, entity)) {
				return new Coord(x, y + 1, z);
			}
		}
		return null;
	}

	public static boolean setBlockIfReplaceable(final World world, final int x, final int y, final int z, final Block block) {
		return setBlockIfReplaceable(world, x, y, z, block, 0);
	}

	public static boolean setBlockIfReplaceable(final World world, final int x, final int y, final int z, final Block block, final int meta) {
		BlockPos pos = new BlockPos(x, y, z);
		final Block currentBlock = getBlock(world, pos);
		if (currentBlock != null && currentBlock.isReplaceable(world, pos)) {
			setBlock(pos, getState(world, pos), world, 3);
			return true;
		}
		return false;
	}

	public static IBlockState getState(World world, BlockPos pos) {
		return world.getBlockState(pos);
	}

	public static Block getBlock(World world, BlockPos pos) {
		return getState(world, pos).getBlock();
	}

	public static void setBlock(BlockPos pos, IBlockState state, World world, int flags) {
		world.setBlockState(pos, state, flags);
	}

	public static Material getMaterial(World world, BlockPos pos) {
		return getState(world, pos).getMaterial();
	}

	public static boolean isOpaque(World world, BlockPos pos) {
		return getState(world, pos).isOpaqueCube();
	}
}
