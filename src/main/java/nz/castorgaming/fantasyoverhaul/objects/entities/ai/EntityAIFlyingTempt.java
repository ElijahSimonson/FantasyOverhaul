package nz.castorgaming.fantasyoverhaul.objects.entities.ai;

import java.util.Set;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIFlyingTempt extends EntityAIBase{

	private EntityCreature temptedEntity;
	private Set<Item> temptItem;
	private boolean scaredByPlayerMovement;
	private int delayTemptCounter;
	private EntityPlayer temptingPlayer;
	private BlockPos targetPos;
	private boolean isRunning;

	public EntityAIFlyingTempt(EntityCreature temptedEntityIn, boolean scaredByPlayerMovementIn,
			Set<Item> temptItemIn) {
		this.temptedEntity = temptedEntityIn;
        this.temptItem = temptItemIn;
        this.scaredByPlayerMovement = scaredByPlayerMovementIn;
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		boolean isTame = temptedEntity instanceof EntityTameable && ((EntityTameable)temptedEntity).isTamed();
		if (isTame) {
			return false;
		}
		if (delayTemptCounter > 0) {
			delayTemptCounter --;
			return false;
		}
		temptingPlayer = temptedEntity.worldObj.getClosestPlayerToEntity(temptedEntity, 10.0);
		if (temptingPlayer == null) {
			return false;
		}
		ItemStack stack = temptingPlayer.inventory.getCurrentItem();
		return stack != null && isBreedingFood(stack);
	}
	
	private boolean isBreedingFood(ItemStack stack) {
		for (Item item : temptItem) {
			if (stack.getItem() == item) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean continueExecuting() {
		if (scaredByPlayerMovement) {
			if (temptedEntity.getDistanceSqToEntity(temptingPlayer) < 36.0) {
				if (temptingPlayer.getDistanceSq(targetPos) > 0.01000000000002) {
					return false;
				}
			}
			else {
				targetPos = temptingPlayer.getPosition();
			}
		}
		return shouldExecute();
	}
	
	@Override
	public void startExecuting() {
		isRunning = true;
	}

	@Override
	public void resetTask() {
		temptingPlayer = null;
		delayTemptCounter = 100;
		isRunning = false;
	}
	
	@Override
	public void updateTask() {
		if (temptedEntity.getDistanceSqToEntity(temptingPlayer) >= 3.0) {
			double dx,dy,dz;
			double moveSq = (Math.pow((dx = targetPos.getX() - temptedEntity.posX), 2) + Math.pow((dy = targetPos.getY() - temptedEntity.posY), 2) + Math.pow((dz = targetPos.getZ() - temptedEntity.posZ), 2));
			double move = MathHelper.sqrt_double(moveSq);
			if (isCourseTraversable(targetPos, move)) {
				EntityCreature entityTempted = temptedEntity;
				entityTempted.motionX += dx / move * 0.05;
				if (entityTempted.posY < targetPos.getY() + 1.0) {
					entityTempted.motionY += dy / move * 0.05 + 0.025;
				}else {
					entityTempted.motionY += dy / move * 0.05;
				}
				entityTempted.motionZ += dz / move * 0.05;
			}
			float n = (float) (-Math.atan2(temptedEntity.motionX, temptedEntity.motionZ) * 180.0f / Math.PI);
			temptedEntity.rotationYaw = n;
			temptedEntity.renderYawOffset = n;
		}
	}
	
	private boolean isCourseTraversable(BlockPos target, double distance) {
		double x = (target.getX() - temptedEntity.posX) / distance;
		double y = (target.getY() - temptedEntity.posY) / distance;
		double z = (target.getZ() - temptedEntity.posZ) / distance;
		AxisAlignedBB temptedBound = temptedEntity.getCollisionBoundingBox();
		AxisAlignedBB bounds = new AxisAlignedBB(temptedBound.minX, temptedBound.minY, temptedBound.minZ, temptedBound.maxX, temptedBound.maxY, temptedBound.maxZ);
		for (int i = 1; i < distance; i++) {		
			bounds.offset(x, y, z);
			if (!temptedEntity.worldObj.getCollisionBoxes(temptedEntity, bounds).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
}
