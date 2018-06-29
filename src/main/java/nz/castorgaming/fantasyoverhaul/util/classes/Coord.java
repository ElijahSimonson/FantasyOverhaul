package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.util.interfaces.INullSource;

public class Coord {

	public static Coord createFrom(final NBTTagCompound nbtTag, final String key) {
		if (nbtTag.hasKey(key + "X") && nbtTag.hasKey(key + "Y") && nbtTag.hasKey(key + "Z")) {
			return new Coord(nbtTag.getInteger(key + "X"), nbtTag.getInteger(key + "Y"), nbtTag.getInteger(key + "Z"));
		}
		return null;
	}

	public static double distance(final Coord first, final Coord second) {
		final double dX = first.x - second.x;
		final double dY = first.y - second.y;
		final double dZ = first.z - second.z;
		return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
	}

	public static double distance(final double firstX, final double firstY, final double firstZ, final double secondX,
			final double secondY, final double secondZ) {
		final double dX = firstX - secondX;
		final double dY = firstY - secondY;
		final double dZ = firstZ - secondZ;
		return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
	}

	public static double distanceSq(final double firstX, final double firstY, final double firstZ, final double secondX,
			final double secondY, final double secondZ) {
		final double dX = firstX - secondX;
		final double dY = firstY - secondY;
		final double dZ = firstZ - secondZ;
		return dX * dX + dY * dY + dZ * dZ;
	}

	public static Coord fromTagNBT(final NBTTagCompound nbt) {
		if (nbt.hasKey("posX") && nbt.hasKey("posY") && nbt.hasKey("posZ")) {
			return new Coord(nbt.getInteger("posX"), nbt.getInteger("posY"), nbt.getInteger("posZ"));
		}
		return null;
	}

	public final int x;

	public final int y;

	public final int z;

	public Coord(Entity entity) {
		this(MathHelper.floor_double(entity.getPosition().getX()), MathHelper.floor_double(entity.getPosition().getY()),
				MathHelper.floor_double(entity.getPosition().getZ()));
	}

	public Coord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Coord(int x, int y, int z, EnumFacing side) {
		this.x = x + side.getDirectionVec().getX();
		this.y = y + side.getDirectionVec().getY();
		this.z = z + side.getDirectionVec().getZ();
	}

	public Coord(INullSource entity) {
		this(entity.getPosX(), entity.getPosY(), entity.getPosZ());
	}

	public Coord(RayTraceResult mop, EntityPosition altPos, boolean before) {
		if (mop != null) {
			switch (mop.typeOfHit) {
			case BLOCK: {
				if (before) {
					x = mop.getBlockPos().getX()
							+ (mop.sideHit == EnumFacing.NORTH ? -1 : mop.sideHit == EnumFacing.SOUTH ? 1 : 0);
					y = mop.getBlockPos().getY()
							+ (mop.sideHit == EnumFacing.DOWN ? -1 : mop.sideHit == EnumFacing.UP ? 1 : 0);
					z = mop.getBlockPos().getZ()
							+ (mop.sideHit == EnumFacing.EAST ? -1 : mop.sideHit == EnumFacing.WEST ? 1 : 0);
					break;
				}
				x = mop.getBlockPos().getX();
				y = mop.getBlockPos().getY();
				z = mop.getBlockPos().getZ();
				break;
			}
			case ENTITY: {
				x = MathHelper.floor_double(altPos.x);
				y = MathHelper.floor_double(altPos.y);
				z = MathHelper.floor_double(altPos.z);
				break;
			}
			default: {
				if (altPos != null) {
					x = MathHelper.floor_double(altPos.x);
					y = MathHelper.floor_double(altPos.y);
					z = MathHelper.floor_double(altPos.z);
					break;
				}
				x = 0;
				y = 0;
				z = 0;
				break;
			}
			}
		} else if (altPos != null) {
			x = MathHelper.floor_double(altPos.x);
			y = MathHelper.floor_double(altPos.y);
			z = MathHelper.floor_double(altPos.z);
		} else {
			x = 0;
			y = 0;
			z = 0;
		}
	}

	public Coord(TileEntity tile) {
		this(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
	}

	public double distanceSqTo(final Coord other) {
		final double dX = other.x - x;
		final double dY = other.y - y;
		final double dZ = other.z - z;
		return dX * dX + dY * dY + dZ * dZ;
	}

	public double distanceSqTo(final int x, final int y, final int z) {
		final double dX = x - this.x;
		final double dY = y - this.y;
		final double dZ = z - this.z;
		return dX * dX + dY * dY + dZ * dZ;
	}

	public double distanceTo(final Coord other) {
		final double dX = other.x - x;
		final double dY = other.y - y;
		final double dZ = other.z - z;
		return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
	}

	public Coord east() {
		return this.east(1);
	}

	public Coord east(final int n) {
		return new Coord(x + n, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		final Coord other = (Coord) obj;
		return x == other.x && y == other.y && z == other.z;
	}

	public Block getBlock(final World world) {
		return this.getBlock(world, 0, 0, 0);
	}

	public Block getBlock(final World world, final int offsetX, final int offsetY, final int offsetZ) {
		return world.getBlockState(new BlockPos(x + offsetX, y + offsetY, z + offsetZ)).getBlock();
	}

	public int getBlockMetadata(final World world) {
		return this.getBlockMetadata(world, 0, 0, 0);
	}

	public int getBlockMetadata(final World world, final int offsetX, final int offsetY, final int offsetZ) {
		// return world.getBlockMetadata(this.x + offsetX, this.y + offsetY, this.z +
		// offsetZ);
		IBlockState state = world.getBlockState(new BlockPos(x + offsetX, y + offsetY, z + offsetZ));
		Block block = state.getBlock();
		return block.getMetaFromState(state);
	}

	public TileEntity getBlockTileEntity(final World world) {
		return this.getBlockTileEntity(world, 0, 0, 0);
	}

	public TileEntity getBlockTileEntity(final World world, final int offsetX, final int offsetY, final int offsetZ) {
		return world.getTileEntity(new BlockPos(x + offsetX, y + offsetY, z + offsetZ));
	}

	public int getHeading(final Coord destination) {
		final double dX = x - destination.x;
		final double dZ = z - destination.z;
		final double yaw = Math.atan2(dZ, dX);
		if (yaw > -0.39269908169872414 && yaw <= 0.39269908169872414) {
			return 6;
		}
		if (yaw > 0.39269908169872414 && yaw <= 1.1780972450961724) {
			return 7;
		}
		if (yaw > 1.1780972450961724 && yaw <= 1.9634954084936207) {
			return 0;
		}
		if (yaw > 1.9634954084936207 && yaw <= 2.748893571891069) {
			return 1;
		}
		if (yaw > 2.748893571891069 || yaw <= -2.748893571891069) {
			return 2;
		}
		if (yaw > -2.748893571891069 && yaw <= -1.9634954084936207) {
			return 3;
		}
		if (yaw > -1.9634954084936207 && yaw <= -1.1780972450961724) {
			return 4;
		}
		return 5;
	}

	public <T> T getTileEntity(final IBlockAccess world, final Class<T> clazz) {
		return BlockUtil.getTileEntity(world, x, y, z, clazz);
	}

	public boolean isAtPosition(TileEntity tileEntity) {
		return tileEntity != null && x == tileEntity.getPos().getX() && y == tileEntity.getPos().getY()
				&& z == tileEntity.getPos().getZ();
	}

	public boolean isMatch(final int x, final int y, final int z) {
		return this.x == x && this.y == y && this.z == z;
	}

	public boolean isNorthOf(final Coord coord) {
		return z < coord.z;
	}

	public boolean isWestOf(final Coord coord) {
		return x < coord.x;
	}

	public void markBlockForUpdate(final World world) {
		world.markBlockRangeForRenderUpdate(thisBlockPos(), thisBlockPos());
	}

	public Coord north() {
		return this.north(1);
	}

	public Coord north(final int n) {
		return new Coord(x, y, z - n);
	}

	public Coord northEast() {
		return new Coord(x + 1, y, z - 1);
	}

	public Coord northWest() {
		return new Coord(x - 1, y, z - 1);
	}

	public void setAir(final World world) {
		world.setBlockToAir(thisBlockPos());
	}

	public boolean setBlock(final World world, final Block block) {
		IBlockState state = block.getDefaultState();
		return world.setBlockState(thisBlockPos(), state);
	}

	public boolean setBlock(final World world, final Block block, final int metadata, final int flags) {
		IBlockState state = block.getStateFromMeta(metadata);
		return world.setBlockState(thisBlockPos(), state, flags);
	}

	public void setNBT(final NBTTagCompound nbtTag, final String key) {
		nbtTag.setInteger(key + "X", x);
		nbtTag.setInteger(key + "Y", y);
		nbtTag.setInteger(key + "Z", z);
	}

	public Coord south() {
		return this.south(1);
	}

	public Coord south(final int n) {
		return new Coord(x, y, z + n);
	}

	public Coord southEast() {
		return new Coord(x + 1, y, z + 1);
	}

	public Coord southWest() {
		return new Coord(x - 1, y, z + 1);
	}

	private BlockPos thisBlockPos() {
		return new BlockPos(x, y, z);
	}

	@Override
	public String toString() {
		return String.format("%d, %d, %d", x, y, z);
	}

	public NBTTagCompound toTagNBT() {
		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("posX", x);
		nbt.setInteger("posY", y);
		nbt.setInteger("posZ", z);
		return nbt;
	}

	public Coord west() {
		return this.west(1);
	}

	public Coord west(final int n) {
		return new Coord(x - n, y, z);
	}
}
