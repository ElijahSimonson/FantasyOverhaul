package nz.castorgaming.fantasyoverhaul.objects.entities.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.entities.EntityFlyingTameable;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.Waypoint;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class EntityAIFlyerFlyToWaypoint extends EntityAIBase {

	private EntityFlyingTameable flyer;
	private CarryRequirement carryRequirement;
	int courseTimer;

	public EntityAIFlyerFlyToWaypoint(EntityFlyingTameable flyerIn, CarryRequirement carryRestriction) {
		courseTimer = 0;
		flyer = flyerIn;
		carryRequirement = carryRestriction;
		setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		return flyer.waypoint != null
				&& (flyer.getHeldItemMainhand() != null || carryRequirement != CarryRequirement.HELD_ITEM);
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public void resetTask() {
		flyer.waypoint = null;
		flyer.setSitting(true);
		if (flyer.isBeingRidden()) {
			flyer.dismountRidingEntity();
		}
		courseTimer = 0;
	}

	@Override
	public void updateTask() {
		if (!flyer.isSitting()) {
			Waypoint waypoint = flyer.getWaypoint();
			if (carryRequirement == CarryRequirement.ENTITY_LIVING) {
				if (flyer.getDistanceSq(waypoint.X, waypoint.Y, waypoint.Z) <= 1.0) {
					List<EntityLivingBase> entities = flyer.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
							flyer.getEntityBoundingBox().expand(1.0, 1.0, 1.0));
					if (entities != null && entities.size() > 1) {
						for (EntityLivingBase entity : entities) {
							if (entity != flyer) {
								entity.startRiding(flyer);
							}
						}
					}
					flyer.waypoint = null;
					waypoint = flyer.getWaypoint();
				}
			} else if (flyer.getHeldItemMainhand() != null
					&& flyer.getDistanceSq(waypoint.X, waypoint.Y, waypoint.Z) <= 1.0) {
				if (!flyer.worldObj.isRemote) {
					ItemStack stack = this.flyer.getHeldItemMainhand();
					flyer.setHeldItem(EnumHand.MAIN_HAND, null);
					if (GeneralItem.isBrew(stack)) {
						flyer.worldObj.playSound(flyer.posX, flyer.posY, flyer.posZ, SoundEffect.RANDOM_BOW.event(),
								SoundEffect.RANDOM_BOW.category(), 0.5f,
								0.4f / (this.flyer.worldObj.rand.nextFloat() * 0.4f + 0.8f), false);
						EntityWitchProjectile projectile = new EntityWitchProjectile(this.flyer.worldObj,
								(EntityLivingBase) this.flyer, stack.getItem());
						projectile.motionX = 0.0;
						projectile.motionZ = 0.0;
						this.flyer.worldObj.spawnEntityInWorld((Entity) projectile);
					} else if (GeneralItem.isBrew(stack) && BrewRegistry.INSTANCE.isSplash(stack.getTagCompound())) {
						flyer.worldObj.playSound(flyer.posX, flyer.posY, flyer.posZ, SoundEffect.RANDOM_BOW.event(),
								SoundEffect.RANDOM_BOW.category(), 0.5f,
								0.4f / (this.flyer.worldObj.rand.nextFloat() * 0.4f + 0.8f), false);
						final EntityBrew projectile2 = new EntityBrew(this.flyer.worldObj,
								(EntityLivingBase) this.flyer, stack, false);
						projectile2.motionX = 0.0;
						projectile2.motionZ = 0.0;
						this.flyer.worldObj.spawnEntityInWorld((Entity) projectile2);
					} else if (stack.getItem() == Items.SPLASH_POTION) {
						flyer.worldObj.playSound(flyer.posX, flyer.posY, flyer.posZ, SoundEffect.RANDOM_BOW.event(),
								SoundEffect.RANDOM_BOW.category(), 0.5f,
								0.4f / (this.flyer.worldObj.rand.nextFloat() * 0.4f + 0.8f), false);
						final EntityPotion projectile3 = new EntityPotion(this.flyer.worldObj,
								(EntityLivingBase) this.flyer, stack);
						projectile3.motionX = 0.0;
						projectile3.motionZ = 0.0;
						this.flyer.worldObj.spawnEntityInWorld((Entity) projectile3);
					} else {
						final EntityItem item = new EntityItem(this.flyer.worldObj, this.flyer.posX, this.flyer.posY,
								this.flyer.posZ, stack);
						if (stack.getItem() == ItemInit.MANDRAKE_SEED) {
							item.lifespan = TimeUtilities.secsToTicks(3);
						}
						this.flyer.worldObj.spawnEntityInWorld((Entity) item);
					}
				}
				this.flyer.waypoint = null;
				waypoint = this.flyer.getWaypoint();
			}
			double dX = waypoint.X - this.flyer.posX;
			double dY = waypoint.Y - this.flyer.posY;
			double dZ = waypoint.Z - this.flyer.posZ;
			double trajectory = dX * dX + dY * dY + dZ * dZ;
			trajectory = MathHelper.sqrt_double(trajectory);
			if (trajectory >= 128.0 && this.carryRequirement == CarryRequirement.HELD_ITEM) {
				BlockVoidBramble.teleportRandomly(this.flyer.worldObj, (int) waypoint.X, (int) waypoint.Y,
						(int) waypoint.Z, (Entity) this.flyer, 16);
			}
			if (--this.courseTimer < 0) {
				this.courseTimer = 0;
			}
			if (this.courseTimer == 0) {
				if (!this.isCourseTraversable(waypoint.X, waypoint.Y, waypoint.Z, trajectory)) {
					final double newX = this.flyer.posX + (this.flyer.worldObj.rand.nextDouble() * 4.0 - 2.0) * 6.0;
					final double newY = this.flyer.posY + (this.flyer.worldObj.rand.nextDouble() * 2.0 - 1.0) * 4.0;
					final double newZ = this.flyer.posZ + (this.flyer.worldObj.rand.nextDouble() * 4.0 - 2.0) * 6.0;
					if (this.flyer.worldObj.rand.nextInt(2) != 0) {
						dX = newX - this.flyer.posX;
						dZ = newZ - this.flyer.posZ;
					}
					if (this.flyer.getDistanceSq(waypoint.X, waypoint.Y, waypoint.Z) <= 1.0) {
						dY = ((this.flyer.posY > waypoint.Y && newY > 0.0) ? (-newY) : newY) - this.flyer.posY;
					} else {
						dY = newY - this.flyer.posY;
					}
					trajectory = dX * dX + dY * dY + dZ * dZ;
					trajectory = MathHelper.sqrt_double(trajectory);
				}
				final double ACCELERATION = 0.2;
				final EntityFlyingTameable flyer = this.flyer;
				flyer.motionX += dX / trajectory * 0.2;
				final EntityFlyingTameable flyer2 = this.flyer;
				flyer2.motionZ += dZ / trajectory * 0.2;
				final EntityFlyingTameable flyer3 = this.flyer;
				flyer3.motionY += dY / trajectory * 0.2 + ((this.flyer.posY < Math
						.min(waypoint.Y + ((this.carryRequirement == CarryRequirement.HELD_ITEM) ? 32 : 32), 255.0))
								? 0.1
								: 0.0);
				this.courseTimer = 10;
			}
			final EntityFlyingTameable flyer4 = this.flyer;
			final EntityFlyingTameable flyer5 = this.flyer;
			final float n = -(float) Math.atan2(this.flyer.motionX, this.flyer.motionZ) * 180.0f / 3.1415927f;
			flyer5.rotationYaw = n;
			flyer4.renderYawOffset = n;
		}
	}
	

	private boolean isCourseTraversable(double x, double y, double z, double trajectory) {
		final double dx = (x - this.flyer.posX) / trajectory;
        final double dy = (y - this.flyer.posY) / trajectory;
        final double dz = (z - this.flyer.posZ) / trajectory;
        AxisAlignedBB bbFlyer = flyer.getEntityBoundingBox();
        final AxisAlignedBB axisalignedbb = new AxisAlignedBB(bbFlyer.minX, bbFlyer.minY, bbFlyer.minZ, bbFlyer.maxX, bbFlyer.maxY, bbFlyer.maxZ);
        for (int i = 1; i < trajectory; ++i) {
            axisalignedbb.offset(dx, dy, dz);
            if (!this.flyer.worldObj.getCollisionBoxes((Entity)this.flyer, axisalignedbb).isEmpty()) {
                return false;
            }
        }
        return true;
	}

	@Override
	public boolean continueExecuting() {
		boolean itemHeld = flyer.getHeldItemMainhand() != null;
		boolean awayFromHome = flyer.getDistance(flyer.homeX, flyer.posY, flyer.homeZ) > 1.0 || Math.abs(flyer.posY - flyer.homeY) > 1.0;
		return (itemHeld && carryRequirement == CarryRequirement.HELD_ITEM) || flyer.waypoint != null || awayFromHome;
	}

	public enum CarryRequirement {
		NONE, HELD_ITEM, ENTITY_LIVING;

	}
}
