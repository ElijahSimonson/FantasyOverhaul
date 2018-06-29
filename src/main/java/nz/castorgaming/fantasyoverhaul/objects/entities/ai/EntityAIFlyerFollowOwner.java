package nz.castorgaming.fantasyoverhaul.objects.entities.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.entities.familiars.Familiar;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;

public class EntityAIFlyerFollowOwner extends EntityAIBase {
	private EntityTameable thePet;
	private EntityLivingBase theOwner;
	World theWorld;
	private double field_75336_f;
	private int field_75343_h;
	float maxDist;
	float minDist;
	private boolean field_75344_i;

	public EntityAIFlyerFollowOwner(final EntityTameable par1EntityTameable, final double par2, final float par4,
			final float par5) {
		this.thePet = par1EntityTameable;
		this.theWorld = par1EntityTameable.worldObj;
		this.field_75336_f = par2;
		this.minDist = par4;
		this.maxDist = par5;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = this.thePet.getOwner();
		if (entitylivingbase == null && Familiar.couldBeFamiliar(this.thePet)) {
			entitylivingbase = (EntityLivingBase) Familiar.getOwnerForFamiliar(this.thePet).getCurrentOwner();
		}
		if (entitylivingbase == null) {
			return false;
		}
		if (this.thePet.isSitting()) {
			return false;
		}
		if (this.thePet.dimension != entitylivingbase.dimension
				|| this.thePet.getDistanceSqToEntity((Entity) entitylivingbase) < this.minDist * this.minDist) {
			return false;
		}
		this.theOwner = entitylivingbase;
		return true;
	}

	public boolean continueExecuting() {
		return this.thePet.getDistanceSqToEntity((Entity) this.theOwner) > this.maxDist * this.maxDist
				&& !this.thePet.isSitting();
	}

	public void startExecuting() {
		this.field_75343_h = 0;
	}

	public void resetTask() {
		this.theOwner = null;
	}

	public void updateTask() {
		if (!this.thePet.isSitting() && --this.field_75343_h <= 0) {
			this.field_75343_h = 10;
			if (this.thePet.dimension != this.theOwner.dimension
					|| this.thePet.getDistanceSqToEntity((Entity) this.theOwner) >= 256.0) {
				final int x = MathHelper.floor_double(this.theOwner.posX) - 2;
				final int z = MathHelper.floor_double(this.theOwner.posZ) - 2;
				final int y = MathHelper.floor_double(this.theOwner.getEntityBoundingBox().minY) - 2;
				for (int dx = 0; dx <= 4; ++dx) {
					for (int dz = 0; dz <= 4; ++dz) {
						for (int dy = 0; dy <= 4; ++dy) {
							final int newX = x + dz;
							final int newY = y + dy;
							final int newZ = z + dz;
							if (this.theOwner.worldObj.getBlockState(new BlockPos(newX, newY - 1, newZ)).isSideSolid(
									(IBlockAccess) this.theOwner.worldObj, new BlockPos(newX, newY - 1, newZ),
									EnumFacing.UP)
									&& !this.theOwner.worldObj.getBlockState(new BlockPos(newX, newY, newZ))
											.isNormalCube()
									&& !this.theOwner.worldObj.getBlockState(new BlockPos(newX, newY + 1, newZ))
											.isNormalCube()) {
								EntityUtil.teleportToLocation(this.theWorld, 0.5 + newX, 0.01 + newY, 0.5 + newZ,
										this.theOwner.dimension, (Entity) this.thePet, true);
								return;
							}
						}
					}
				}
			} else {
				double d0 = this.theOwner.posX - this.thePet.posX;
				double d2 = this.theOwner.posY - this.thePet.posY;
				double d3 = this.theOwner.posZ - this.thePet.posZ;
				double d4 = d0 * d0 + d2 * d2 + d3 * d3;
				d4 = MathHelper.sqrt_double(d4);
				if (this.isCourseTraversable(this.theOwner.posX, this.theOwner.posY, this.theOwner.posZ, d4)) {
					final EntityTameable thePet = this.thePet;
					thePet.motionX += d0 / d4 * 0.1;
					if (this.thePet.posY < this.theOwner.posY + 2.0) {
						final EntityTameable thePet2 = this.thePet;
						thePet2.motionY += d2 / d4 * 0.1 + 0.1;
					} else {
						final EntityTameable thePet3 = this.thePet;
						thePet3.motionY += d2 / d4 * 0.1;
					}
					final EntityTameable thePet4 = this.thePet;
					thePet4.motionZ += d3 / d4 * 0.1;
				} else {
					final double newX2 = this.thePet.posX
							+ (this.thePet.worldObj.rand.nextFloat() * 8.0f - 4.0f) * 6.0f;
					final double newY2 = this.thePet.posY
							+ (this.thePet.worldObj.rand.nextFloat() * 2.0f - 1.0f) * 6.0f;
					final double newZ2 = this.thePet.posZ
							+ (this.thePet.worldObj.rand.nextFloat() * 8.0f - 4.0f) * 6.0f;
					d0 = newX2 - this.thePet.posX;
					d2 = newY2 - this.thePet.posY;
					d3 = newZ2 - this.thePet.posZ;
					d4 = d0 * d0 + d2 * d2 + d3 * d3;
					d4 = MathHelper.sqrt_double(d4);
					final EntityTameable thePet5 = this.thePet;
					thePet5.motionX += d0 / d4 * 0.1;
					final EntityTameable thePet6 = this.thePet;
					thePet6.motionY += d2 / d4 * 0.1 + 0.1;
					final EntityTameable thePet7 = this.thePet;
					thePet7.motionZ += d3 / d4 * 0.1;
				}
			}
			final EntityTameable thePet8 = this.thePet;
			final EntityTameable thePet9 = this.thePet;
			final float n = -(float) Math.atan2(this.thePet.motionX, this.thePet.motionZ) * 180.0f / 3.1415927f;
			thePet9.rotationYaw = n;
			thePet8.renderYawOffset = n;
		}
	}

	private boolean isCourseTraversable(final double par1, final double par3, final double par5, final double par7) {
		final double d4 = (par1 - this.thePet.posX) / par7;
		final double d5 = (par3 - this.thePet.posY) / par7;
		final double d6 = (par5 - this.thePet.posZ) / par7;
		AxisAlignedBB entityBB = thePet.getEntityBoundingBox();
		final AxisAlignedBB axisalignedbb = new AxisAlignedBB(entityBB.minX, entityBB.minY, entityBB.minZ,
				entityBB.maxX, entityBB.maxY, entityBB.maxZ);
		for (int i = 1; i < par7; ++i) {
			axisalignedbb.offset(d4, d5, d6);
			if (!this.thePet.worldObj.getCollisionBoxes((Entity) this.thePet, axisalignedbb).isEmpty()) {
				return false;
			}
		}
		return true;
	}

}
