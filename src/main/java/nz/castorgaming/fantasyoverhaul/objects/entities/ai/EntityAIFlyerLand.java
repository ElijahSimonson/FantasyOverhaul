package nz.castorgaming.fantasyoverhaul.objects.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIFlyerLand extends EntityAIBase {
	private double speed;
	int[] target;
	World worldObj;
	public int courseChangeCooldown;
	public double waypointX;
	public double waypointY;
	public double waypointZ;
	public boolean findTrees;
	EntityLiving living;

	public EntityAIFlyerLand(final EntityLiving par1EntityCreature, final double par2, final boolean findTrees) {
		this.living = par1EntityCreature;
		this.worldObj = this.living.worldObj;
		this.speed = par2;
		this.findTrees = findTrees;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		return !this.isLanded() && !this.liquidBelow((int) this.living.posY - 1)
				&& !this.liquidBelow((int) this.living.posY) && this.worldObj.rand.nextInt(20) == 0;
	}

	private boolean liquidBelow(final int y) {
		return this.worldObj.getBlockState(
				new BlockPos(MathHelper.floor_double(this.living.posX), y, MathHelper.floor_double(this.living.posZ)))
				.getMaterial().isLiquid();
	}

	public boolean continueExecuting() {
		final boolean cont = !this.isLanded() && !this.liquidBelow((int) this.living.posY - 1)
				&& !this.liquidBelow((int) this.living.posY);
		return cont;
	}

	public void startExecuting() {
		this.courseChangeCooldown = 100;
		final int x0 = MathHelper.floor_double(this.living.posX);
		final int y0 = MathHelper.floor_double(this.living.posY);
		final int z0 = MathHelper.floor_double(this.living.posZ);
		this.target = (int[]) (this.findTrees ? this.findTreeTop(x0, y0, z0) : null);
		if (this.target == null) {
			this.target = this.findGround(x0, y0, z0);
		}
		if (this.target != null) {
		}
	}

	public void resetTask() {
		this.target = null;
		super.resetTask();
	}

	private int[] findTreeTop(final int x0, final int y0, final int z0) {
		final int RADIUS = 16;
		final int Y_RADIUS = 3;
		for (int y = Math.max(y0 - 3, 1); y <= y0 + 3; ++y) {
			for (int x = x0 - 16; x <= x0 + 16; ++x) {
				for (int z = z0 - 16; z <= z0 + 16; ++z) {
					final IBlockState blockID = this.worldObj.getBlockState(new BlockPos(x, y, z));
					if (blockID.getMaterial() == Material.LEAVES) {
						for (int y2 = y; y2 < y0 + 10; ++y2) {
							if (this.worldObj.isAirBlock(new BlockPos(x, y2, z))) {
								final double d0 = x - this.living.posX;
								final double d2 = y2 - this.living.posY;
								final double d3 = z - this.living.posZ;
								double d4 = d0 * d0 + d2 * d2 + d3 * d3;
								d4 = MathHelper.sqrt_double(d4);
								if (this.isCourseTraversable(x, y2, z, d4)) {
									return new int[] { x, y2 + 2, z };
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private int[] findGround(final int x0, final int y0, final int z0) {
		for (int y = y0; y > 1; --y) {
			final Material material = this.worldObj.getBlockState(new BlockPos(x0, y, z0)).getMaterial();
			if (material != Material.AIR) {
				if (!material.isLiquid()) {
					return new int[] { x0, y + 1, z0 };
				}
				for (int i = 0; i < 10; ++i) {
					final int j = MathHelper.floor_double(this.living.posX + this.worldObj.rand.nextInt(20) - 10.0);
					final int k = MathHelper.floor_double(
							this.living.getEntityBoundingBox().minY + this.worldObj.rand.nextInt(6) - 3.0);
					final int l = MathHelper.floor_double(this.living.posZ + this.worldObj.rand.nextInt(20) - 10.0);
					final IBlockState blockID = this.worldObj.getBlockState(new BlockPos(j, k, l));
					final double d0 = j - this.living.posX;
					final double d2 = k - this.living.posY;
					final double d3 = l - this.living.posZ;
					double d4 = d0 * d0 + d2 * d2 + d3 * d3;
					d4 = MathHelper.sqrt_double(d4);
					if ((blockID.getMaterial() == Material.LEAVES || blockID.getMaterial().isSolid())
							&& this.worldObj.isAirBlock(new BlockPos(j, k + 1, l))
							&& this.isCourseTraversable(j, k, l, d4)) {
						return new int[] { j, k + 1, l };
					}
				}
			}
		}
		return null;
	}

	public void updateTask() {
		if (!this.isLanded()) {
			if (this.target != null && this.living.getDistanceSq((double) this.target[0], this.living.posY,
					(double) this.target[2]) > 1.0 && this.courseChangeCooldown-- > 0) {
				final double d0 = this.target[0] - this.living.posX;
				final double d2 = this.target[1] - this.living.posY;
				final double d3 = this.target[2] - this.living.posZ;
				double d4 = d0 * d0 + d2 * d2 + d3 * d3;
				d4 = MathHelper.sqrt_double(d4);
				if (this.isCourseTraversable(this.target[0], this.target[1], this.target[2], d4)) {
					final EntityLiving living = this.living;
					living.motionX += d0 / d4 * 0.05;
					final EntityLiving living2 = this.living;
					living2.motionY += d2 / d4 * 0.05;
					final EntityLiving living3 = this.living;
					living3.motionZ += d3 / d4 * 0.05;
				}
			} else if (!this.liquidBelow((int) (this.living.posY - 1.0))) {
				this.living.motionY = -0.1;
			}
			final EntityLiving living4 = this.living;
			final EntityLiving living5 = this.living;
			final float n = -(float) Math.atan2(this.living.motionX, this.living.motionZ) * 180.0f / 3.1415927f;
			living5.rotationYaw = n;
			living4.renderYawOffset = n;
		}
		final EntityLiving living6 = this.living;
		final EntityLiving living7 = this.living;
		final float n2 = -(float) Math.atan2(this.living.motionX, this.living.motionZ) * 180.0f / 3.1415927f;
		living7.rotationYaw = n2;
		living6.renderYawOffset = n2;
	}

	private boolean isLanded() {
		final IBlockState blockID = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.living.posX),
				(int) (this.living.posY - 0.01), MathHelper.floor_double(this.living.posZ)));
		final Material material = blockID.getMaterial();
		return material == Material.LEAVES || material.isSolid();
	}

	private boolean isCourseTraversable(final double par1, final double par3, final double par5, final double par7) {
		final double d4 = (par1 - this.living.posX) / par7;
		final double d5 = (par3 - this.living.posY) / par7;
		final double d6 = (par5 - this.living.posZ) / par7;
		AxisAlignedBB entityBB = living.getEntityBoundingBox();
		final AxisAlignedBB axisalignedbb = new AxisAlignedBB(entityBB.minX, entityBB.minY, entityBB.minZ,
				entityBB.maxX, entityBB.maxY, entityBB.maxZ);
		for (int i = 1; i < par7; ++i) {
			axisalignedbb.offset(d4, d5, d6);
			if (!this.worldObj.getCollisionBoxes((Entity) this.living, axisalignedbb).isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
