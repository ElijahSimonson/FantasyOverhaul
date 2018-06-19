package nz.castorgaming.fantasyoverhaul.objects.entities.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIFlyerWander extends EntityAIBase{

	private double xPosition;
    private double yPosition;
    private double zPosition;
    private double speed;
    World worldObj;
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    public double fleeDistance;
    EntityLiving living;
    
    public EntityAIFlyerWander(final EntityLiving par1EntityCreature, final double par2, final double fleeDistance) {
        this.living = par1EntityCreature;
        this.worldObj = this.living.worldObj;
        this.speed = par2;
        this.fleeDistance = fleeDistance;
        this.setMutexBits(1);
    }
    
    public boolean shouldExecute() {
        final boolean isTame = this.living instanceof EntityTameable && ((EntityTameable)this.living).isTamed();
        return (!isTame && this.living.worldObj.getClosestPlayerToEntity((Entity)this.living, this.fleeDistance) != null) || (this.living.getAge() < 100 && this.living.getRNG().nextInt(this.living.worldObj.provider.isDaytime() ? 300 : 100) == 0 && (!(this.living instanceof EntityTameable) || !((EntityTameable)this.living).isSitting()));
    }
    
    public boolean continueExecuting() {
        return (this.living instanceof EntityTameable && !((EntityTameable)this.living).isSitting()) || this.living.getRNG().nextInt(40) != 0;
    }
    
    public void startExecuting() {
    }
    
    public void updateTask() {
        final double d0 = this.waypointX - this.living.posX;
        final double d2 = this.waypointY - this.living.posY;
        final double d3 = this.waypointZ - this.living.posZ;
        double d4 = d0 * d0 + d2 * d2 + d3 * d3;
        if (d4 < 1.0 || d4 > 3600.0) {
            final float distance = (this.living instanceof EntityTameable && ((EntityTameable)this.living).isTamed()) ? 2.0f : 6.0f;
            this.waypointX = this.living.posX + (this.worldObj.rand.nextFloat() * 8.0f - 4.0f) * distance;
            this.waypointY = this.living.posY + (this.worldObj.rand.nextFloat() * 2.0f - 1.0f) * distance;
            this.waypointZ = this.living.posZ + (this.worldObj.rand.nextFloat() * 8.0f - 4.0f) * distance;
        }
        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown += this.worldObj.rand.nextInt(2) + 2;
            d4 = MathHelper.sqrt_double(d4);
            if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, d4)) {
                final EntityLiving living = this.living;
                living.motionX += d0 / d4 * 0.1;
                final EntityLiving living2 = this.living;
                living2.motionY += d2 / d4 * 0.1;
                final EntityLiving living3 = this.living;
                living3.motionZ += d3 / d4 * 0.1;
            }
            else {
                this.waypointX = this.living.posX;
                this.waypointY = this.living.posY;
                this.waypointZ = this.living.posZ;
            }
        }
        final EntityLiving living4 = this.living;
        final EntityLiving living5 = this.living;
        final float n = -(float)Math.atan2(this.living.motionX, this.living.motionZ) * 180.0f / 3.1415927f;
        living5.rotationYaw = n;
        living4.renderYawOffset = n;
    }
    
    private boolean isCourseTraversable(final double par1, final double par3, final double par5, final double par7) {
        final double d4 = (par1 - this.living.posX) / par7;
        final double d5 = (par3 - this.living.posY) / par7;
        final double d6 = (par5 - this.living.posZ) / par7;
        AxisAlignedBB entityBB = this.living.getEntityBoundingBox();
        final AxisAlignedBB axisalignedbb = new AxisAlignedBB(entityBB.minX, entityBB.minY, entityBB.minZ, entityBB.maxX, entityBB.maxY, entityBB.maxZ);
        for (int i = 1; i < par7; ++i) {
            axisalignedbb.offset(d4, d5, d6);
            if (!this.living.worldObj.getCollisionBoxes((Entity)this.living, axisalignedbb).isEmpty()) {
                return false;
            }
        }
        return true;
    }
	
}
