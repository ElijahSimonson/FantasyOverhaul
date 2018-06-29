package nz.castorgaming.fantasyoverhaul.objects.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAISitAndStay extends EntityAIBase {

	private EntityTameable theEntity;

	public EntityAISitAndStay(EntityTameable entityIn) {
		theEntity = entityIn;
		setMutexBits(5);
	}

	@Override
	public boolean shouldExecute() {
		return theEntity.isSitting();
	}

	@Override
	public void startExecuting() {
		theEntity.getNavigator().clearPathEntity();
	}

}
