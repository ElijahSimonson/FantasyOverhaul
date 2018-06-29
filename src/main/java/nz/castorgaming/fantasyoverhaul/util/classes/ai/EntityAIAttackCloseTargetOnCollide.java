package nz.castorgaming.fantasyoverhaul.util.classes.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;

public class EntityAIAttackCloseTargetOnCollide extends EntityAIAttackMelee {

	EntityCreature attacker;
	Class classTarget;
	double maxDistance;

	public EntityAIAttackCloseTargetOnCollide(EntityCreature living, Class cls, double par1, boolean par2,
			double maxDistance) {
		this(living, par1, par2, maxDistance);
		classTarget = cls;
	}

	public EntityAIAttackCloseTargetOnCollide(final EntityCreature par1EntityLiving, final double par2,
			final boolean par3, final double maxDistance) {
		super(par1EntityLiving, par2, par3);
		this.attacker = par1EntityLiving;
		this.maxDistance = maxDistance;
	}

	@Override
	public boolean shouldExecute() {
		boolean execute = super.shouldExecute();
		if (execute && !this.isTargetNearby()) {
			execute = false;
		}
		return execute;
	}

	protected boolean isTargetNearby() {
		final EntityLivingBase entityTarget = (this.attacker != null) ? this.attacker.getAttackTarget() : null;
		return entityTarget != null
				&& this.attacker.getDistanceSqToEntity(entityTarget) <= this.maxDistance * this.maxDistance
				&& this.attacker.getNavigator().getPathToEntityLiving(entityTarget) != null
				&& (entityTarget.getHeldItemMainhand() == null
						|| entityTarget.getHeldItemMainhand().getItem() != ItemInit.DEVILS_TONGUE_CHARM);
	}

	@Override
	public boolean continueExecuting() {
		boolean execute = super.continueExecuting();
		if (execute && !this.isTargetNearby()) {
			execute = false;
		}
		return execute;
	}

}
