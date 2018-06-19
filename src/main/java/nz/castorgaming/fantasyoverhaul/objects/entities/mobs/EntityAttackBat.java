package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.powers.infusions.player.InfusionOtherwhere;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class EntityAttackBat extends EntityBat {

	private EntityPlayer ownerPlayer;
	private GameProfile owner;

	public EntityAttackBat(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void collideWithNearbyEntities() {
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224, 0.0, 0.20000000298023224));
		if (list != null && !list.isEmpty()) {
			for (Entity entity : list) {
				if (entity.canBePushed()) {
					collideWithEntity(entity);
				}
			}
		}
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		if (!worldObj.isRemote && entity instanceof EntityLivingBase) {
			EntityLivingBase target = (EntityLivingBase) entity;
			if (ownerPlayer == null) {
				ownerPlayer = getOwner();
			}
			if (target != ownerPlayer && !(target instanceof EntityBat) && !target.isDead) {
				target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, ownerPlayer), 4.0f);
				ParticleEffect.REDDUST.send(SoundEffect.RANDOM_DRINK, entity, entity.width, entity.height, 16);
				setDead();
			}
		}
	}

	@Override
	protected void updateAITasks() {
		if (!worldObj.isRemote) {
			boolean done = false;
			if (ticksExisted > 300) {
				ParticleEffect.SMOKE.send(SoundEffect.NONE, this, this.width, this.height, 16);
				setDead();
			}
			else {
				if (ownerPlayer == null) {
					ownerPlayer = getOwner();
				}
				if (ownerPlayer != null && ownerPlayer.dimension == dimension) {
					RayTraceResult rtr = InfusionOtherwhere.raytraceEntities(worldObj, ownerPlayer, true, 32.0);
					if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.ENTITY && rtr.entityHit instanceof EntityLivingBase && !(rtr.entityHit instanceof EntityBat)) {
						double divider = Math.sqrt(Math.pow(rtr.entityHit.posX + posX, 2) + Math.pow(rtr.entityHit.posY + posY, 2) + Math.pow(rtr.entityHit.posZ + posZ, 2));
						if (isCourseTraversable(rtr.entityHit.posX, rtr.entityHit.posY, rtr.entityHit.posZ, divider)) {
							motionX += (rtr.entityHit.posX - posX) / divider * 0.1;
							motionY += (rtr.entityHit.posY - posY) / divider * 0.1;
							motionZ += (rtr.entityHit.posZ - posZ) / divider * 0.1;
							float f = (float) (Math.atan2(motionZ, motionX) * 180.0 / Math.PI) - 90.0f;
							float f2 = MathHelper.wrapDegrees(f - rotationYaw);
							moveForward = 0.5f;
							rotationYaw += f2;
							done = true;
						}
						float n = -(float) (Math.atan2(motionX, motionZ) * 180.0f / Math.PI);
						rotationYaw = n;
						renderYawOffset = n;
					}
				}
			}
			if (!done) {
				super.updateAITasks();
			}
		}
	}

	private boolean isCourseTraversable(double par1, double par2, double par3, double divider) {
		double d0 = (par1 - posX) / divider;
		double d1 = (par2 - posY) / divider;
		double d2 = (par3 - posZ) / divider;
		AxisAlignedBB orig = getCollisionBoundingBox();
		AxisAlignedBB bb = new AxisAlignedBB(orig.maxX, orig.maxY, orig.maxZ, orig.minX, orig.minY, orig.minZ);

		for (int i = 1; i < divider; i++) {
			bb.offset(d0, d1, d2);
			if (!worldObj.getCollisionBoxes(this, bb).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void setOwner(EntityPlayer player) {
		owner = player.getGameProfile();
	}

	public EntityPlayer getOwner() {
		return (owner != null) ? worldObj.getPlayerEntityByUUID(owner.getId()) : null;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound root) {
		super.writeEntityToNBT(root);
		if (owner != null) {
			NBTTagCompound compounds = new NBTTagCompound();
			NBTUtil.writeGameProfile(compounds, owner);
			root.setTag("Owner", compounds);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("Owner")) {
			owner = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("Owner"));
		}
	}

}
