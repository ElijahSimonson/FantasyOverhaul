package nz.castorgaming.fantasyoverhaul.util.classes.ai;

import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityDemon;

public class EntityAILookAtDemonicBarginPlayer extends EntityAIWatchClosest {

	private final EntityDemon theMerchant;

	public EntityAILookAtDemonicBarginPlayer(EntityDemon trader) {
		super(trader, EntityPlayer.class, 8.0f);
		theMerchant = trader;
	}

	@Override
	public boolean shouldExecute() {
		if (theMerchant.isTrading()) {
			closestEntity = theMerchant.getCustomer();
			return true;
		}
		return false;
	}

}
