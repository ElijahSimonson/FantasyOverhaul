package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.InfusionInit;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;
import nz.castorgaming.fantasyoverhaul.objects.entities.familiars.Familiar;
import nz.castorgaming.fantasyoverhaul.objects.items.item.BroomEnchanted;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;

public class EntityBroom extends Entity {

	private boolean isBoatEmpty;
	private double speedMulti;
	private int broomPosRotationInc;
	private double broomX, broomY, broomZ, broomYaw, broomPitch;
	@SideOnly(Side.CLIENT)
	private double velocityX, velocityY, velocityZ;
	boolean riderHasOwlFamiliar;
	boolean riderHasOwlFamilar, riderHasSoaringBrew;

	private DataParameter<Integer> BRUSH_COLOR = EntityDataManager.createKey(EntityBroom.class, DataSerializers.VARINT);
	private DataParameter<String> NAME = EntityDataManager.createKey(EntityBroom.class, DataSerializers.STRING);
	private DataParameter<Integer> DIRECTION = EntityDataManager.createKey(EntityBroom.class, DataSerializers.VARINT);
	private DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityBroom.class, DataSerializers.VARINT);
	private DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(EntityBroom.class, DataSerializers.FLOAT);
	
	public EntityBroom(World worldIn) {
		super(worldIn);
		riderHasOwlFamiliar = false;
		riderHasSoaringBrew = false;
		isBoatEmpty = true;
		speedMulti = 0.07;
		preventEntitySpawning = true;
		setSize(1.2f, 0.5f);
	}

	public EntityBroom(World world, double x, double y, double z) {
		this(world);
		setPosition(x, y + getYOffset(), z);
		motionX = 0.0;
		motionY = 0.0;
		motionZ = 0.0;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}
	
	public EntityBroom(World world, BlockPos pos) {
		this(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {
		dataManager.register(BRUSH_COLOR, EnumDyeColor.BROWN.getDyeDamage());
		dataManager.register(NAME, "");
		dataManager.register(DIRECTION, 1);
	}

	@Override
	protected void dealFireDamage(int amount) {
	}

	public void setBrushColor(EnumDyeColor color) {
		dataManager.set(BRUSH_COLOR, color.getDyeDamage());
	}

	public void setBrushColor(int color) {
		if (color < EnumDyeColor.values().length) {
			setBrushColor(EnumDyeColor.byDyeDamage(color));
		}
	}

	public EnumDyeColor getBrushColor() {
		return EnumDyeColor.byDyeDamage(dataManager.get(BRUSH_COLOR));
	}

	@Override
	public double getYOffset() {
		return height / 2.0f;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public double getMountedYOffset() {
		return height * 0.55;
	}

	@Override
	public String getCustomNameTag() {
		return dataManager.get(NAME);
	}

	@Override
	public boolean hasCustomName() {
		return dataManager.get(NAME).length() > 0;
	}

	public void setCustomNameTag(String name) {
		dataManager.set(NAME, name);
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead && isBeingRidden();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void performHurtAnimation() {
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() * 11.0f);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setVelocity(double x, double y, double z) {
		velocityX = x;
		velocityY = y;
		velocityZ = z;
		super.setVelocity(x, y, z);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setString("CustomName", getCustomNameTag());
		int brushColor = getBrushColor().getDyeDamage();
		if (brushColor >= 0) {
			compound.setByte("BrushColor", Byte.valueOf((byte) brushColor));
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("CustomName") && compound.getString("CustomName").length() > 0) {
			setCustomNameTag(compound.getString("CustomName"));
		}
		if (compound.hasKey("BrushColor")) {
			setBrushColor(compound.getByte("BrushColor"));
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isEntityInvulnerable(source)) {
			return false;
		}

		if (!worldObj.isRemote && isDead) {
			setForwardDirection(-getForwardDirection());
			setTimeSinceHit(10);
			setDamageTaken(getDamageTaken() + amount * 10.0f);
			setBeenAttacked();
			boolean flag = source.getEntity() instanceof EntityPlayer
					&& (((EntityPlayer) source.getEntity()).capabilities.isCreativeMode);
			if (flag || getDamageTaken() > 40.0f) {
				if (isBeingRidden()) {
					getRidingEntity().startRiding(this);
				}
				if (!flag) {
					ItemStack broomStack = ItemInit.BROOM_ENCHANTED.createStack();
					if (hasCustomName()) {
						broomStack.setStackDisplayName(getCustomNameTag());
					}
					BroomEnchanted.setBroomItemColor(broomStack, getBrushColor());
					entityDropItem(broomStack, 0.0f);
				}
				setDead();
			}
			return true;
		}
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void setIsBoatEmpty(boolean empty) {
		isBoatEmpty = empty;
	}
	
	public int getForwardDirection() {
		return dataManager.get(DIRECTION);
	}
	
	public void setForwardDirection(int dir) {
		dataManager.set(DIRECTION, dir);
	}
	
	public void setTimeSinceHit(int time) {
		dataManager.set(TIME_SINCE_HIT, time);
	}
	
	public int getTimeSinceHit() {
		return dataManager.get(TIME_SINCE_HIT);
	}
	
	public void setDamageTaken(float damage) {
		dataManager.set(DAMAGE_TAKEN, damage);
	}
	
	public float getDamageTaken() {
		return dataManager.get(DAMAGE_TAKEN);
	}
	
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increment) {
		if (isBoatEmpty) {
			broomPosRotationInc = increment + 5;
		}
		else {
			double dx = x - posX;
			double dy = y - posY;
			double dz = z - posZ;
			double d2 = dx * dx + dy * dy + dz * dz;
			if (d2 <= 1.0) {
				return;
			}
			broomPosRotationInc = 3;
		}
		broomX = x;
		broomY = y;
		broomZ = z;
		broomYaw = yaw;
		broomPitch = pitch;
		motionX = velocityX;
		motionY = velocityY;
		motionZ = velocityZ;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted % 100 == 0 && isBeingRidden() && getRidingEntity() instanceof EntityPlayer) {
			riderHasSoaringBrew = InfusionBrewEffect.SOARING.isActive((EntityPlayer) getRidingEntity());
		}
		if (getTimeSinceHit() > 0) {
			setTimeSinceHit(getTimeSinceHit() - 1);
		}
		if (getDamageTaken() > 0.0f) {
			setDamageTaken(getDamageTaken() - 1.0f);
		}
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		byte b0 = 5;
		double d0 = 0.0;
		double initialHorzVelocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		if (initialHorzVelocity > 0.26249999999999996) {
			double newHorzVelocity = Math.cos(this.rotationYaw * Math.PI / 180.0);
            Math.sin(this.rotationYaw * Math.PI / 180.0);
		}
		if (worldObj.isRemote && isBoatEmpty) {
			if (broomPosRotationInc > 0) {
				 final double newHorzVelocity = this.posX + (this.broomX - this.posX) / this.broomPosRotationInc;
	                final double d2 = this.posY + (this.broomY - this.posY) / this.broomPosRotationInc;
	                final double d3 = this.posZ + (this.broomZ - this.posZ) / this.broomPosRotationInc;
	                final double d4 = MathHelper.wrapDegrees(this.broomYaw - this.rotationYaw);
	                this.rotationYaw += (float)(d4 / this.broomPosRotationInc);
	                this.rotationPitch += (float)((this.broomPitch - this.rotationPitch) / this.broomPosRotationInc);
	                --this.broomPosRotationInc;
	                this.setPosition(newHorzVelocity, d2, d3);
	                this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
	                final double newHorzVelocity = this.posX + this.motionX;
	                final double d2 = this.posY + this.motionY;
	                final double d3 = this.posZ + this.motionZ;
	                this.setRotation((float)(this.rotationYaw + (this.broomYaw - this.rotationYaw)), (float)(this.rotationPitch + (this.broomPitch - this.rotationPitch)));
	                this.setPosition(newHorzVelocity, d2, d3);
	                this.motionX *= 0.9900000095367432;
	                this.motionZ *= 0.9900000095367432;
	            }
		 }
        else {
            if (isBeingRidden() && getRidingEntity() instanceof EntityLivingBase) {
                final double newHorzVelocity = ((EntityLivingBase)getRidingEntity()).moveForward;
                if (newHorzVelocity > 0.0) {
                    final double d2 = -Math.sin(getRidingEntity().rotationYaw * 3.1415927f / 180.0f);
                    final double d3 = Math.cos(getRidingEntity().rotationYaw * 3.1415927f / 180.0f);
                    this.motionX += d2 * this.speedMulti * (0.1 + (this.riderHasSoaringBrew ? 0.1 : 0.0) + (this.riderHasOwlFamiliar ? 0.2 : 0.0));
                    this.motionZ += d3 * this.speedMulti * (0.1 + (this.riderHasSoaringBrew ? 0.1 : 0.0) + (this.riderHasOwlFamiliar ? 0.2 : 0.0));
                    double pitch = -Math.sin(getRidingEntity().rotationPitch * 3.1415927f / 180.0f);
                    if (pitch > -0.5 && pitch < 0.2) {
                        pitch = 0.0;
                    }
                    else if (pitch < 0.0) {
                        pitch *= 0.5;
                    }
                    this.motionY = pitch * this.speedMulti * 2.0;
                }
                else if (newHorzVelocity == 0.0 && (this.riderHasOwlFamiliar || this.riderHasSoaringBrew)) {
                    this.motionX *= 0.9;
                    this.motionZ *= 0.9;
                }
            }
            else if (!isBeingRidden()) {
                this.riderHasOwlFamiliar = false;
                final double moX = this.motionX * 0.9;
                final double moZ = this.motionZ * 0.9;
                this.motionX = ((Math.abs(moX) < 0.01) ? 0.0 : moX);
                this.motionZ = ((Math.abs(moZ) < 0.01) ? 0.0 : moZ);
                if (!this.onGround) {
                    this.motionY = -0.2;
                }
            }
            double newHorzVelocity = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            final double SPEED_LIMIT = 0.9 + (this.riderHasOwlFamiliar ? 0.3 : 0.0) + (this.riderHasSoaringBrew ? 0.3 : 0.0);
            if (newHorzVelocity > SPEED_LIMIT) {
                final double d2 = SPEED_LIMIT / newHorzVelocity;
                this.motionX *= d2;
                this.motionZ *= d2;
                this.motionY *= d2;
                newHorzVelocity = SPEED_LIMIT;
            }
            final double MAX_ACCELERATION = (this.riderHasSoaringBrew || this.riderHasOwlFamiliar) ? 0.35 : 0.35;
            final double MAX_ACCELERATION_FACTOR = MAX_ACCELERATION * 100.0;
            if (newHorzVelocity > initialHorzVelocity && this.speedMulti < MAX_ACCELERATION) {
                this.speedMulti += (MAX_ACCELERATION - this.speedMulti) / MAX_ACCELERATION_FACTOR;
                if (this.speedMulti > MAX_ACCELERATION) {
                    this.speedMulti = MAX_ACCELERATION;
                }
            }
            else {
                this.speedMulti -= (this.speedMulti - 0.07) / MAX_ACCELERATION_FACTOR;
                if (this.speedMulti < 0.07) {
                    this.speedMulti = 0.07;
                }
            }
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9900000095367432;
            this.motionY *= 0.9900000095367432;
            this.motionZ *= 0.9900000095367432;
            this.rotationPitch = 0.0f;
            double d2 = this.rotationYaw;
            final double d3 = this.prevPosX - this.posX;
            final double d4 = this.prevPosZ - this.posZ;
            if (d3 * d3 + d4 * d4 > 0.001) {
                d2 = (float)(Math.atan2(d4, d3) * 180.0 / 3.141592653589793);
            }
            final double d5 = MathHelper.wrapDegrees(d2 - this.rotationYaw);
            this.setRotation(this.rotationYaw += (float)d5, this.rotationPitch);
            if (!this.worldObj.isRemote) {
                final List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, getCollisionBoundingBox().expand(0.20000000298023224, 0.0, 0.20000000298023224));
                if (list != null && !list.isEmpty()) {
                    for (int l = 0; l < list.size(); ++l) {
                        final Entity entity = (Entity) list.get(l);
                        if (entity != getRidingEntity() && entity.canBePushed() && entity instanceof EntityBroom) {
                            entity.applyEntityCollision((Entity)this);
                        }
                    }
                }
                if (isBeingRidden() && getRidingEntity().isDead) {
                    getRidingEntity().dismountRidingEntity();
                }
            }
        }
	}
	
	public boolean interactFirst(EntityPlayer player) {
		if (getRidingEntity() != null && getRidingEntity() instanceof EntityPlayer && getRidingEntity() != player) {
			return true;
		}
		EnumHand active = player.getActiveHand();
		if (!worldObj.isRemote && player.getHeldItem(active) != null && player.getHeldItem(active).getItem() == Items.DYE) {
			ItemStack stack = player.getHeldItem(active);
			setBrushColor(ItemDye.DYE_COLORS[stack.getItemDamage()]);
			if (!player.capabilities.isCreativeMode) {
				stack.stackSize--;
			}
			if (stack.stackSize <= 0) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}
			return true;
		}
		if (!worldObj.isRemote) {
			riderHasOwlFamiliar = Familiar.hasActiveBroomMasteryFamilar(player);
			riderHasSoaringBrew = InfusionInit.SOARING.isActive(player);
			player.startRiding(this);
		}
		return true;
	}
	
	public static class EventHooks{
		@SubscribeEvent
		public void onLivingFall(LivingFallEvent event) {
			if (event.getEntityLiving() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();
				if (player.isRiding() && player.getRidingEntity() instanceof EntityBroom) {
					event.setDistance(0.0f);
				}
			}
		}
	}
}
