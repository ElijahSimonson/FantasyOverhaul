package nz.castorgaming.fantasyoverhaul.objects.entities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.util.classes.Waypoint;

public class EntityFlyingTameable extends EntityTameable{

	protected EntityAISit aiSit;
	public ItemStack waypoint;
	public double homeX, homeY, homeZ;
	
	public EntityFlyingTameable(World worldIn) {
		super(worldIn);
		aiSit = new EntityAISit(this);
		waypoint = null;
	}
	
	public Waypoint getWaypoint() {
		return new Waypoint(worldObj, waypoint, homeX, homeY, homeZ);
	}
	
	@Override
	public void fall(float distance, float damageMultiplier) {}
	
	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {}	
	
	@Override
	public boolean isOnLadder() {
		return false;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (waypoint != null) {
			NBTTagCompound nbtWaypoint = new NBTTagCompound();
			waypoint.writeToNBT(nbtWaypoint);
			compound.setTag("FOWaypoint", nbtWaypoint);
		}
		compound.setDouble("HomeX", homeX);
		compound.setDouble("HomeY", homeY);
		compound.setDouble("HomeZ", homeZ);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("FOWaypoint")) {
			NBTTagCompound nbtWaypoint = compound.getCompoundTag("FOWaypoint");
			waypoint = ItemStack.loadItemStackFromNBT(nbtWaypoint);
		}else {
			waypoint = null;
		}
		homeX = compound.getDouble("HomeX");
		homeY = compound.getDouble("HomeY");
		homeZ = compound.getDouble("HomeZ");
	}
	
	public void moveEntityWithHeading(final float par1, final float par2) {
        if (this.isInWater()) {
            this.moveRelative(par1, par2, 0.02f);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929;
            this.motionY *= 0.800000011920929;
            this.motionZ *= 0.800000011920929;
        }
        else if (isInLava()) {
            this.moveRelative(par1, par2, 0.02f);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        }
        else {
            float f2 = 0.91f;
            if (this.onGround) {
                f2 = 0.54600006f;
                final Block i = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(getCollisionBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock();
                if (i != Blocks.AIR) {
                    f2 = i.slipperiness * 0.91f;
                }
            }
           float f3 = 0.16277136f / (f2 * f2 * f2);
            moveRelative(par1, par2, this.onGround ? (0.1f * f3) : 0.02f);
            f2 = 0.91f;
            if (this.onGround) {
                f2 = 0.54600006f;
                final Block j = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(getCollisionBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock();
                if (j != Blocks.AIR) {
                    f2 = j.slipperiness * 0.91f;
                }
            }
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= f2;
            this.motionY *= f2;
            this.motionZ *= f2;
        }
        this.prevLimbSwingAmount = this.limbSwingAmount;
        final double d0 = this.posX - this.prevPosX;
        final double d2 = this.posZ - this.prevPosZ;
        float f4 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 4.0f;
        if (f4 > 1.0f) {
            f4 = 1.0f;
        }
        this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4f;
        this.limbSwing += this.limbSwingAmount;
    }

}
