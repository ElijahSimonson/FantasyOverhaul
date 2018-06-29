package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityEye extends EntityLiving {

	public EntityEye(World worldIn) {
		super(worldIn);
		setSize(0.0f, 0.0f);
		setInvisible(isImmuneToFire = true);
		noClip = true;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
	}

	@Override
	public boolean isOnLadder() {
		return false;
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return true;
	}

	@Override
	public void onLivingUpdate() {
		motionY = 0.5;
		super.onLivingUpdate();
		if (ticksExisted > 200) {
			setDead();
		}
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if (isInWater()) {
			moveRelative(strafe, forward, 0.02f);
			moveEntity(motionX, motionX, motionZ);
			motionX *= 0.8;
			motionY *= 0.8;
			motionZ *= 0.8;
		} else if (isInLava()) {
			moveRelative(strafe, forward, 0.02f);
			moveEntity(motionX, motionY, motionZ);
			motionX *= 0.5;
			motionY *= 0.5;
			motionZ *= 0.5;
		} else {
			float friction = 0.91f;
			if (onGround) {
				friction = 0.546f;
				Block block = worldObj.getBlockState(getPosition().down()).getBlock();
				if (block != Blocks.AIR) {
					friction = block.slipperiness * 0.91f;
				}
			}
			float friction2 = (float) (0.16277136f / Math.pow(friction, 3));
			moveRelative(strafe, forward, onGround ? (0.1f * friction2) : 0.02f);
			friction = 0.91f;
			if (onGround) {
				friction = 0.546f;
				Block block2 = worldObj.getBlockState(getPosition().down()).getBlock();
				if (block2 != Blocks.AIR) {
					friction = block2.slipperiness * 0.91f;
				}
			}
			moveEntity(motionX, motionY, motionZ);
			motionX *= friction;
			motionY *= friction;
			motionZ *= friction;
		}
		prevLimbSwingAmount = limbSwingAmount;
		double dx = posX - prevPosX;
		double dz = posZ - prevPosZ;
		float multi = Math.min(MathHelper.sqrt_double(dx * dx + dz * dz) * 4.0f, 1.0f);
		limbSwingAmount += (multi - limbSwingAmount) * 0.4f;
		limbSwing += limbSwingAmount;
	}

}
