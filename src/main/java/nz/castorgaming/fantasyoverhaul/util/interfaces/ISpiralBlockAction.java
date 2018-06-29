package nz.castorgaming.fantasyoverhaul.util.interfaces;

import net.minecraft.world.World;

public interface ISpiralBlockAction {

	void onSpiralActionStart(final World p0, final int p1, final int p2, final int p3);

	boolean onSpiralBlockAction(final World p0, final int p1, final int p2, final int p3);

	void onSpiralActionStop(final World p0, final int p1, final int p2, final int p3);

}
