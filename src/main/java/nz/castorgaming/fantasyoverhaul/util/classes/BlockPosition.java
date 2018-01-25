package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockPosition {
	public final int dimension;
	public final int x;
	public final int y;
	public final int z;

	public BlockPosition(final int dimension, final int x, final int y, final int z) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockPosition(final World world, final int x, final int y, final int z) {
		this(world.provider.getDimension(), x, y, z);
	}

	public BlockPosition(final World world, final Coord coord) {
		this(world, coord.x, coord.y, coord.z);
	}

	public BlockPosition(final World world, final double x, final double y, final double z) {
		this(world.provider.getDimension(), MathHelper.floor_double(x), MathHelper.floor_double(y),
				MathHelper.floor_double(z));
	}

	public BlockPosition(final World world, final EntityPosition position) {
		this(world, position.x, position.y, position.z);
	}

	public static BlockPosition from(final ItemStack stack) {
		final NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("PosX") && tag.hasKey("PosY") && tag.hasKey("PosZ") && tag.hasKey("PosD")) {
			final int newX = tag.getInteger("PosX");
			final int newY = tag.getInteger("PosY");
			final int newZ = tag.getInteger("PosZ");
			final int newD = tag.getInteger("PosD");
			return new BlockPosition(newD, newX, newY, newZ);
		}
		return null;
	}

	public World getWorld(final MinecraftServer server) {
		for (final WorldServer world : server.worldServers) {
			if (world.provider.getDimension() == this.dimension) {
				return world;
			}
		}
		return null;
	}
}
