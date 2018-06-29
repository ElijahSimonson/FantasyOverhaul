package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.util.interfaces.ISpiralBlockAction;

public class EffectSpiral {

	private final ISpiralBlockAction action;

	public EffectSpiral(ISpiralBlockAction iSpiralBlockAction) {
		this.action = iSpiralBlockAction;
	}

	public void apply(final World world, final int midX, final int midY, final int midZ, final int dimX,
			final int dimZ) {
		this.action.onSpiralActionStart(world, midX, midY, midZ);
		int x = 0;
		int z = 0;
		int dx = 0;
		int dz = -1;
		int t = Math.max(dimX, dimZ);
		for (int maxI = t * t, i = 0; i < maxI && (-dimX / 2 > x || x > dimX / 2 || -dimZ / 2 > z || z > dimZ / 2
				|| this.action.onSpiralBlockAction(world, midX + x, midY, midZ + z)); x += dx, z += dz, ++i) {
			if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
				t = dx;
				dx = -dz;
				dz = t;
			}
		}
		this.action.onSpiralActionStop(world, midX, midY, midZ);
	}

	public void apply(World world, BlockPos blockPos, int RSQ, int RSQ2) {
		apply(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), RSQ, RSQ2);
	}

}
