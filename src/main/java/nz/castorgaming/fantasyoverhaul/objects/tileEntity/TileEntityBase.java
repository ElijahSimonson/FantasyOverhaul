package nz.castorgaming.fantasyoverhaul.objects.tileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityBase extends TileEntity implements ITickable {

	protected long ticks;

	public TileEntityBase() {
		ticks = 0;
	}

	@Override
	public void update() {
		if (ticks == 0L) {
			initiate();
		} else if (ticks >= Long.MAX_VALUE) {
			ticks = 1L;
		}
		ticks++;
	};

	protected void initiate() {

	}

	public void notifyBlockUpdate(boolean notifyNeighbour) {
		worldObj.notifyBlockUpdate(pos, null, null, getBlockMetadata());
	}
}
