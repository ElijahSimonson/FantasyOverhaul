package nz.castorgaming.fantasyoverhaul.objects.potions.tasks;

import java.util.UUID;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Enslaved;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;

public class EntityAIEnslaverHurtByTarget extends EntityAITarget {

	EntityCreature enslavedEntity;
	EntityLivingBase enslaversAttacker;
	private int enslaversRevengeTimer;

	public EntityAIEnslaverHurtByTarget(EntityCreature enslavedCreature) {
		super(enslavedCreature, false);
		enslavedEntity = enslavedCreature;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (!enslavedEntity.isPotionActive(Potions.ENSLAVED)) {
			return false;
		}
		String ownerName = Enslaved.getMobEnslaverName(enslavedEntity);
		if (ownerName == null || ownerName.isEmpty()) {
			return false;
		}
		EntityLivingBase enslaver = enslavedEntity.worldObj.getPlayerEntityByUUID(UUID.fromString(ownerName));
		if (enslaver == null) {
			return false;
		}

		enslaversAttacker = enslaver.getAITarget();
		int revengeTimer = enslaver.getRevengeTimer();
		return revengeTimer != enslaversRevengeTimer && enslaversAttacker != null
				&& isSuitableTarget(enslaversAttacker, true);
	}

	@Override
	public void startExecuting() {
		EntityUtil.setTarget(taskOwner, enslaversAttacker);
		String enslaverName = Enslaved.getMobEnslaverName(enslavedEntity);
		EntityLivingBase entity = enslavedEntity.worldObj.getPlayerEntityByUUID(UUID.fromString(enslaverName));
		if (entity != null) {
			enslaversRevengeTimer = entity.getRevengeTimer();
		}
		super.startExecuting();
	}

}
