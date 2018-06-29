package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;

public class EntityPosition {
	public final double x;
	public final double y;
	public final double z;

	public EntityPosition(final int x, final int y, final int z) {
		this(0.5 + x, 0.5 + y, 0.5 + z);
	}

	public EntityPosition(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public EntityPosition(final BlockPosition position) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
	}

	public EntityPosition(final Entity entity) {
		this(entity.posX, entity.posY, entity.posZ);
	}

	public EntityPosition(final RayTraceResult mop) {
		if (mop.typeOfHit == RayTraceResult.Type.ENTITY) {
			this.x = mop.entityHit.posX;
			this.y = mop.entityHit.posY;
			this.z = mop.entityHit.posZ;
		} else if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
			this.x = mop.getBlockPos().getX() + 0.5;
			this.y = mop.getBlockPos().getY() + 0.5;
			this.z = mop.getBlockPos().getZ() + 0.5;
		} else {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 0.0;
		}
	}

	public double getDistanceSqToEntity(final Entity entity) {
		final double d0 = this.x - entity.posX;
		final double d2 = this.y - entity.posY;
		final double d3 = this.z - entity.posZ;
		return d0 * d0 + d2 * d2 + d3 * d3;
	}

	public AxisAlignedBB getBounds(final double radius) {
		final AxisAlignedBB aabb = new AxisAlignedBB(this.x - radius, this.y - radius, this.z - radius, this.x + radius,
				this.y + radius, this.z + radius);
		return aabb;
	}

	public boolean occupiedBy(final Entity entity) {
		return entity != null && entity.posX == this.x && entity.posY == this.y && entity.posZ == this.z;
	}
}
