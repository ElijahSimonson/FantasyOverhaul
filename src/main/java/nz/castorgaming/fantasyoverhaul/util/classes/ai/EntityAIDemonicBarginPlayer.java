package nz.castorgaming.fantasyoverhaul.util.classes.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityDemon;

public class EntityAIDemonicBarginPlayer extends EntityAIBase {

	private EntityDemon trader;

	public EntityAIDemonicBarginPlayer(EntityDemon trader) {
		this.trader = trader;
		setMutexBits(5);
	}

	public boolean shouldExecute() {
		if (!trader.isEntityAlive()) {
			return false;
		}
		if (trader.isInWater()) {
			return false;
		}
		if (!trader.onGround) {
			return false;
		}
		if (trader.velocityChanged) {
			return false;
		}
		EntityPlayer player = trader.getCustomer();
		return player != null && trader.getDistanceSqToEntity(player) <= 16.0
				&& player.openContainer instanceof Container;
	}

	public void startExecuting() {
		trader.getNavigator().clearPathEntity();
	}

	public void resetTask() {
		trader.setCustomer(null);
		trader.targetTasks.onUpdateTasks();
	}
}
