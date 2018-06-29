package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class DimensionalLocation {

	public final int dimension;
	public final double posX;
	public final double posY;
	public final double posZ;
	public final boolean isValid;

	public DimensionalLocation(final Entity entity) {
		this(entity.dimension, entity.posX, entity.posY, entity.posZ, true);
	}

	public DimensionalLocation(final NBTTagCompound nbtTag, final String prefix) {
		this(nbtTag.getInteger(prefix + "D"), nbtTag.getDouble(prefix + "X"), nbtTag.getDouble(prefix + "Y"),
				nbtTag.getDouble(prefix + "Z"), nbtTag.hasKey(prefix + "D") && nbtTag.hasKey(prefix + "X")
						&& nbtTag.hasKey(prefix + "Y") && nbtTag.hasKey(prefix + "Z"));
	}

	public DimensionalLocation(final int dimension, final double posX, final double posY, final double posZ) {
		this(dimension, posX, posY, posZ, true);
	}

	protected DimensionalLocation(final int dimension, final double posX, final double posY, final double posZ,
			final boolean isValid) {
		this.dimension = dimension;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.isValid = isValid;
	}

	public void saveToNBT(final NBTTagCompound nbtTag, final String prefix) {
		nbtTag.setInteger(prefix + "D", this.dimension);
		nbtTag.setDouble(prefix + "X", this.posX);
		nbtTag.setDouble(prefix + "Y", this.posY);
		nbtTag.setDouble(prefix + "Z", this.posZ);
	}
}
