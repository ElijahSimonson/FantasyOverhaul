package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public final class TargetPointUtil {

	public static TargetPoint from(final Entity entity, final double range) {
		if (entity != null) {
			return new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range);
		}

		return new TargetPoint(0, 0.0, 0.0, 0.0, range);
	}

	public static TargetPoint from(World world, double x, double y, double z, double range) {
		return new TargetPoint(world.provider.getDimension(), x, y, z, range);
	}

}
