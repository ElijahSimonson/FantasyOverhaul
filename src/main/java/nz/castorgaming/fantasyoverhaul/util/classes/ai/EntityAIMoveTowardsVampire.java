package nz.castorgaming.fantasyoverhaul.util.classes.ai;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;

public class EntityAIMoveTowardsVampire extends EntityAIBase {

	private EntityCreature theCreature;
	private EntityLivingBase theTarget;
	private double speed;
	private float minTargetDistance, maxTargetDistance;

	public EntityAIMoveTowardsVampire(EntityCreature theCreature, double speed, float min, float max) {
		this.theCreature = theCreature;
		this.speed = speed;
		minTargetDistance = min;
		maxTargetDistance = max;
		setMutexBits(1);
	}

	@Override
	public boolean continueExecuting() {
		if (theCreature.ticksExisted % 20 == 0) {
			theCreature.getNavigator().tryMoveToXYZ(theTarget.posX, theTarget.posY, theTarget.posZ, speed);
		}
		return true;
	}

	private EntityLivingBase getDistanceSqToPartner() {
		double r = maxTargetDistance;
		AxisAlignedBB bb = new AxisAlignedBB(
				new BlockPos(theCreature.posX - r, theCreature.posY - r, theCreature.posZ - r));
		List<EntityPlayer> mogs = theCreature.worldObj.getEntitiesWithinAABB(EntityPlayer.class, bb);
		double minDistance = Double.MAX_VALUE;
		EntityLivingBase target = null;
		for (EntityPlayer player : mogs) {
			if (IPlayerVampire.get(player).getVampireLevel() >= 8) {
				double distance = theCreature.getDistanceToEntity(player);
				if (distance >= minDistance) {
					continue;
				}
				minDistance = distance;
				target = player;
			}
		}
		return target;
	}

	@Override
	public void resetTask() {
		theTarget = null;
	}

	@Override
	public boolean shouldExecute() {
		theTarget = getDistanceSqToPartner();
		if (theTarget == null) {
			return false;
		}
		double dist = theTarget.getDistanceSqToEntity(theCreature);
		return dist <= maxTargetDistance * maxTargetDistance && dist >= minTargetDistance * minTargetDistance;
	}

	@Override
	public void startExecuting() {
		theCreature.getNavigator().tryMoveToXYZ(theTarget.posX, theTarget.posY, theTarget.posZ, speed);
	}

}
