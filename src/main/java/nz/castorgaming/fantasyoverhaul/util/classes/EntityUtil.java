package nz.castorgaming.fantasyoverhaul.util.classes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jline.internal.Log;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityHornedHuntsman;
import nz.castorgaming.fantasyoverhaul.objects.potions.tasks.EntityAIAttackOnCollide2;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHandleDT;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketParticles;
import nz.castorgaming.fantasyoverhaul.util.packets.PushTarget;

public class EntityUtil {

	public static class DamageSourceSunlight extends EntityDamageSource {
		public static final DamageSourceSunlight SUN;

		static {
			SUN = new DamageSourceSunlight(null);
		}

		public DamageSourceSunlight(Entity attacker) {
			super("sun", attacker);
			setDamageBypassesArmor();
			setMagicDamage();
		}

		@Override
		public ITextComponent getDeathMessage(EntityLivingBase entity) {
			EntityLivingBase entityLivingBase1 = entity.getLastAttacker();
			String s = "witchery:death.attack" + damageType;
			String s2 = s + ",player";
			TextComponentTranslation textOutput = entityLivingBase1 != null && I18n.hasKey(s2)
					? new TextComponentTranslation(s2,
							new Object[] { entity.getCommandSenderEntity().getName(),
									entityLivingBase1.getCommandSenderEntity().getName() })
					: new TextComponentTranslation(s, new Object[] { entity.getCommandSenderEntity().getName() });
			return textOutput;
		}
	}

	public static class DamageSourceVampireFire extends DamageSource {
		public static final DamageSourceVampireFire SOURCE;

		static {
			SOURCE = new DamageSourceVampireFire();
		}

		public DamageSourceVampireFire() {
			super("onFire");
			setDamageBypassesArmor();
			setMagicDamage();

		}
	}

	private static Field fieldTrackedEntities;

	private static Field fieldGhastTargetedEntity;

	private static Field fieldGhastAggroCooldown;

	static {
		fieldTrackedEntities = null;
	}

	@SuppressWarnings("unchecked")
	public static void correctProjectileTrackerSync(World world, Entity projectile) {
		if (!world.isRemote && world instanceof WorldServer) {
			try {
				if (fieldTrackedEntities == null) {
					fieldTrackedEntities = ReflectionHelper.findField(EntityTracker.class,
							new String[] { "trackedEntities", "trackedEntities", "b" });
				}
				if (fieldTrackedEntities != null) {
					EntityTracker tracker = ((WorldServer) world).getEntityTracker();
					Set<EntityTrackerEntry> trackedEntities = (Set<EntityTrackerEntry>) fieldTrackedEntities
							.get(tracker);

					for (EntityTrackerEntry next : trackedEntities) {
						if (next.getTrackedEntity() == projectile) {
							next.updateCounter = 1;
							break;
						}
					}
				}
			} catch (IllegalAccessException e) {
				Log.warn(e, "Exception occured setting entity tracking for bolt.");
			} catch (Exception e2) {
				Log.debug(String.format("Exception occured setting entity tracking for bolt. %s", e2.toString()));
			}
		}
	}

	public static void dropAttackTarget(EntityLiving entity) {
		entity.setAttackTarget(null);
		if (entity instanceof EntityCreature) {
			EntityCreature creatureEntity = (EntityCreature) entity;
			creatureEntity.setRevengeTarget(null);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> T findNearestEntityWithinAABB(World world, Class<T> clazz, AxisAlignedBB bounds,
			Entity entity) {
		Entity foundEntity = world.findNearestEntityWithinAABB((Class<? extends Entity>) clazz, bounds, entity);
		if (foundEntity != null) {
			return (T) foundEntity;
		}
		return null;
	}

	public static <T extends Entity> List<T> getEntityInRadius(Class<T> clazz, TileEntity tile, double radius) {
		return getEntityInRadius(clazz, tile.getWorld(), 0.5 + tile.getPos().getX(), 0.5 + tile.getPos().getY(),
				0.5 + tile.getPos().getZ(), radius);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> List<T> getEntityInRadius(Class<T> clazz, World world, double x, double y,
			double z, double radius) {
		AxisAlignedBB bounds = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius,
				z + radius);
		List<? extends Entity> entities = world.getEntitiesWithinAABB((Class<? extends Entity>) clazz, bounds);
		ArrayList<T> nearbyEntities = new ArrayList<T>();
		double radiusSq = radius * radius;
		for (Entity entity : entities) {
			if (entity.getDistanceSq(x, entity.posY, z) <= radiusSq) {
				nearbyEntities.add((T) entity);
			}
		}
		return nearbyEntities;
	}

	public static float getHealthAfterDamage(LivingHurtEvent event, float currentHealth, EntityLivingBase entity) {
		if (event.getSource().isUnblockable()) {
			return currentHealth - event.getAmount();
		}
		float damage = event.getAmount();
		int i = 25 - entity.getTotalArmorValue();
		float f1 = damage * i;
		damage = f1 / 25.0f;
		if (entity.isPotionActive(MobEffects.RESISTANCE) && event.getSource() != DamageSource.outOfWorld) {
			i = (entity.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
			float j = 25 - i;
			f1 = damage * j;
			damage = f1 / 25.0f;
		}
		if (damage <= 00.0f) {
			damage = 0.0f;
		} else {
			i = EnchantmentHelper.getEnchantmentModifierDamage(entity.getHeldEquipment(), event.getSource());
			if (i > 20) {
				float j = 25 - i;
				f1 = damage * j;
				damage = f1 / 25;
			}
		}
		return currentHealth - damage;
	}

	public static void instantDeath(EntityLivingBase entity, EntityLivingBase attacker) {
		if (entity != null && entity.worldObj != null && !entity.worldObj.isRemote) {
			if (entity instanceof EntityLiving) {
				if (attacker == null) {
					entity.onDeath(DamageSource.magic);
				} else {
					entity.onDeath(new EntityDamageSource(DamageSource.magic.getDamageType(), attacker));
				}
				entity.setDead();
			} else if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (!player.capabilities.isCreativeMode) {
					if (player.isPlayerSleeping()) {
						player.wakeUpPlayer(true, true, false);
					}
					entity.setHealth(0.0f);
					if (IPlayerVampire.get(player).isVampire()) {
						entity.onDeath(
								attacker == null ? DamageSourceSunlight.SUN : new DamageSourceSunlight(attacker));
					} else {
						entity.onDeath(new EntityDamageSource(DamageSource.magic.getDamageType(), attacker));
					}
				}
			}
		}
	}

	public static boolean isNoDrops(EntityLivingBase entity) {
		if (entity == null || entity instanceof EntityPlayer) {
			return false;
		}
		return entity.getEntityData().getBoolean("WITCNoDrops");
	}

	public static boolean moveBlockToPositionAndUpdate(EntityLiving entity, int x, int y, int z, int maxDY) {
		World world = entity.worldObj;
		boolean done = false;
		int mod = 0;
		int sign = -1;
		while (!done && mod <= 2 * maxDY && y < 250 && y > 2) {
			if (BlockUtil.isNormalCube(world.getBlockState(new BlockPos(x, y, z)).getBlock())
					&& world.isAirBlock(new BlockPos(x, y + 1, z)) && world.isAirBlock(new BlockPos(x, y + 2, z))) {
				done = true;
			} else {
				++mod;
				sign *= -1;
				y += mod * sign;
			}
		}
		if (done) {
			entity.setPositionAndUpdate(0.5 + x, 1.05 + y, 0.5 + z);
		}
		return done;
	}

	public static void persistanceRequired(EntityLiving entity) {
		entity.enablePersistence();
	}

	public static EntityPlayer playerOrFake(World world, EntityLivingBase entity) {
		if (entity != null && entity instanceof EntityPlayer) {
			return (EntityPlayer) entity;
		}
		if (world == null || !(world instanceof WorldServer)) {
			return null;
		}
		return FakePlayerFactory.getMinecraft((WorldServer) world);
	}

	public static EntityPlayer playerOrFake(World world, String thrower) {
		return playerOrFake(world, world != null ? world.getPlayerEntityByName(thrower) : null);
	}

	public static void pullTowards(World world, Entity entity, EntityPosition target, double dy, double yy) {
		if (entity instanceof EntityDragon || entity instanceof EntityHornedHuntsman || target.occupiedBy(entity)) {
			return;
		}

		double d = target.x - entity.posX, d2 = target.y - entity.posY, d3 = target.z - entity.posZ;
		double distance = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
		if (distance < 0.01) {
			return;
		}
		float f2 = 0.1f + (float) dy;
		double mx = d / distance * f2 * distance;
		double my = yy == 0.0 ? 0.4 : d2 / distance * distance * 0.2 + 0.2 + yy;
		double mz = d3 / distance * f2 * distance;
		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 20, 1));
		}
		if (entity instanceof EntityPlayer) {
			Reference.PACKET_HANDLER.sendTo((IMessage) new PushTarget(mx, my, mz), (EntityPlayer) entity);
		} else {
			entity.motionX = mx;
			entity.motionY = my;
			entity.motionZ = mz;
		}
	}

	public static void push(World world, Entity entity, EntityPosition position, double power) {
		double d = position.x - entity.posX;
		double d2 = position.y - entity.posY;
		double d3 = position.z - entity.posZ;
		double d4 = d * d + d2 * d2 + d3 * d3;
		d4 *= d4;
		if (d4 <= Math.pow(6.0, 4.0)) {
			double d5 = -(d * 0.01999999955296516 / d4) * Math.pow(6.0, 3.0);
			double d6 = -(d2 * 0.01999999955296516 / d4) * Math.pow(6.0, 3.0);
			double d7 = -(d3 * 0.01999999955296516 / d4) * Math.pow(6.0, 3.0);
			if (d5 > 0.0) {
				d5 = 0.22;
			} else if (d5 < 0.0) {
				d5 = -0.22;
			}
			if (d6 > 0.2) {
				d6 = 0.12;
			} else if (d6 < -0.1) {
				d6 = 0.12;
			}
			if (d7 > 0.0) {
				d7 = 0.22;
			} else if (d7 < 0.0) {
				d7 = -0.22;
			}
			entity.motionX += d5 * power;
			entity.motionY += d6 * (power / 3.0);
			entity.motionZ += d7 * power;

		}
	}

	public static void pushBack(World world, Entity entity, EntityPosition hit, double xyScale, double ySpeed) {
		double d = hit.x - entity.posX;
		double d2 = hit.y - entity.posY;
		double d3 = hit.z - entity.posZ;
		Vec3d vec = new Vec3d(d, d2, d3).normalize();
		double dx = -vec.xCoord * xyScale;
		double dy = Math.max(-vec.yCoord, ySpeed);
		double dz = -vec.zCoord * xyScale;

		if (entity instanceof EntityPlayer) {
			Reference.PACKET_HANDLER.sendTo((IMessage) new PushTarget(dx, dy, dz), (EntityPlayer) entity);
		} else {
			entity.motionX = dx;
			entity.motionY = dy;
			entity.motionZ = dz;
		}
	}

	public static void setNoDrops(EntityLivingBase entity) {
		if (entity != null) {
			NBTTagCompound nbtEntity = entity.getEntityData();
			nbtEntity.setBoolean("WITCNoDrops", true);
		}
	}

	public static void setTarget(EntityLiving attacker, EntityLivingBase victim) {
		attacker.setAttackTarget(victim);
		if (attacker instanceof EntityGhast) {
			try {
				EntityGhast ghastEntity = (EntityGhast) attacker;
				if (fieldGhastTargetedEntity == null) {
					fieldGhastTargetedEntity = ReflectionHelper.findField(EntityGhast.class,
							new String[] { "targetedEntity", "targetedEntity", "g" });
				}
				fieldGhastTargetedEntity.set(ghastEntity, victim);
				if (fieldGhastAggroCooldown == null) {
					fieldGhastAggroCooldown = ReflectionHelper.findField(EntityGhast.class,
							new String[] { "aggroCooldown", "aggroCooldown", "h" });
				}
				fieldGhastAggroCooldown.set(ghastEntity, 20000);
			} catch (IllegalAccessException e) {
				Log.warn(e, "Exception occured setting ghast target");
			} catch (Exception e2) {
				Log.debug(String.format("Exception occurred setting ghast target. %s", e2.toString()));
			}

		}
		if (attacker instanceof EntityCreature) {
			EntityCreature attackerCreature = (EntityCreature) attacker;
			attackerCreature.setAttackTarget(victim);
			attackerCreature.setRevengeTarget(victim);
			if (attackerCreature instanceof EntityZombie || attackerCreature instanceof EntityCreeper) {
				boolean found = false;
				Class<? extends EntityLivingBase> victimClass = victim.getClass();
				for (Object obj : attackerCreature.targetTasks.taskEntries) {
					EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry) obj;
					if (task.action instanceof EntityAIAttackOnCollide2) {
						EntityAIAttackOnCollide2 ai = (EntityAIAttackOnCollide2) task.action;
						if (ai != null && ai.appliesToClass(victimClass)) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					attacker.tasks.addTask(2, new EntityAIAttackOnCollide2(attackerCreature, victimClass, 1.0, false));
				}
			}
		}
	}

	public static void spawnEntityInWorld(World world, Entity entity) {
		if (entity != null && world != null && !world.isRemote) {
			world.spawnEntityInWorld(entity);
		}
	}

	public static void syncInventory(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
		}
	}

	public static boolean touchOfDeath(Entity victim, EntityLivingBase attacker, float damage) {
		if (victim != null && victim.isEntityInvulnerable(DamageSource.magic)) {
			return false;
		}
		if (victim != null && victim.worldObj != null && !victim.worldObj.isRemote) {
			if (victim instanceof EntityLiving) {
				DamageSource source = new EntityDamageSource(DamageSource.magic.getDamageType(), attacker);
				EntityLiving creature = (EntityLiving) victim;
				float cap = 10000.0f;
				if (victim instanceof IHandleDT) {
					cap = ((IHandleDT) victim).getCapDT(source, damage);
					if (cap <= 0.0f) {
						return false;
					}
					if (attacker instanceof EntityLiving) {
						cap = Math.min(6.0f, cap);
					}
				}
				creature.attackEntityFrom(source, 0.0f);
				creature.setHealth(Math.max(creature.getHealth() - Math.min(damage, cap), 0.0f));
				creature.attackEntityFrom(source, 0.0f);
			} else if (victim instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) victim;
				if (player.capabilities.isCreativeMode) {
					return false;
				}
				player.setHealth(Math.max(player.getHealth() - damage, 0.0f));
				if (player.getHealth() <= 0.0f) {
					if (attacker == null) {
						player.onDeath(DamageSource.magic);
					} else {
						player.onDeath(new EntityDamageSource(DamageSource.magic.getDamageType(), attacker));
					}
				} else {
					player.attackEntityFrom(new EntityDamageSource(DamageSource.magic.getDamageType(), attacker), 0.0f);
				}
			}
		}
		return true;
	}

	private static MinecraftServer getServer(World world) {
		return !world.isRemote ? world.getMinecraftServer() : FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	public static boolean teleportToLocationSafely(final World world, final double posX, final double posY,
			final double posZ, final int dimension, final Entity entity, final boolean presetPosition) {
		final World targetWorld = getServer(world).worldServerForDimension(dimension);
		final int x = MathHelper.floor_double(posX);
		final int y = MathHelper.floor_double(posY);
		final int z = MathHelper.floor_double(posZ);
		for (int i = 0; i < 16; ++i) {
			int dy = y + i;
			if (dy < 250 && !BlockUtil.isReplaceableBlock(targetWorld, x, dy, z)
					&& BlockUtil.isReplaceableBlock(targetWorld, x, dy + 1, z)
					&& BlockUtil.isReplaceableBlock(targetWorld, x, dy + 2, z)) {
				teleportToLocation(world, x, dy + 1, z, dimension, entity, presetPosition);
				return true;
			}
			dy = y - i;
			if (i > 0 && dy > 1 && !BlockUtil.isReplaceableBlock(targetWorld, x, dy, z)
					&& BlockUtil.isReplaceableBlock(targetWorld, x, dy + 1, z)
					&& BlockUtil.isReplaceableBlock(targetWorld, x, dy + 2, z)) {
				teleportToLocation(world, x, dy + 1, z, dimension, entity, presetPosition);
				return true;
			}
		}
		return false;
	}

	public static void teleportToLocation(final World world, final double posX, final double posY, final double posZ,
			final int dimension, final Entity entity, final boolean presetPosition) {
		teleportToLocation(world, posX, posY, posZ, dimension, entity, presetPosition, ParticleEffect.PORTAL,
				SoundEffect.MOB_ENDERMEN_PORTAL);
	}

	public static void teleportToLocation(final World world, final double posX, final double posY, final double posZ,
			final int dimension, final Entity entity, final boolean presetPosition, final ParticleEffect particle,
			final SoundEffect sound) {
		final boolean isVampire = CreatureUtilities.isVampire(entity);
		if (isVampire) {
			Reference.PACKET_HANDLER.sendToAllAround(
					new PacketParticles(ParticleEffect.SMOKE, SoundEffect.RANDOM_POOF, entity, 0.5, 2.0),
					TargetPointUtil.from(entity, 16.0));
		} else {
			Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(particle, sound, entity, 0.5, 2.0),
					TargetPointUtil.from(entity, 16.0));
		}
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			if (entity.dimension != dimension) {
				if (presetPosition) {
					player.setPosition(posX, posY, posZ);
				}
				travelToDimension(player, dimension);
			}
			player.setPositionAndUpdate(posX, posY, posZ);
		} else if (entity instanceof EntityLiving) {
			if (entity.dimension != dimension) {
				travelToDimension(entity, dimension, posX, posY, posZ);
			} else {
				entity.setLocationAndAngles(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
			}
		} else if (entity.dimension != dimension) {
			travelToDimension(entity, dimension, posX, posY, posZ);
		} else {
			entity.setLocationAndAngles(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
		}
		if (isVampire) {
			Reference.PACKET_HANDLER.sendToAllAround(
					new PacketParticles(ParticleEffect.SMOKE, SoundEffect.RANDOM_POOF, entity, 0.5, 2.0),
					TargetPointUtil.from(entity, 16.0));
		} else {
			Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(particle, sound, entity, 0.5, 2.0),
					TargetPointUtil.from(entity, 16.0));
		}
	}

	public static void travelToDimension(final EntityPlayer player, final int dimension) {
		if (!player.worldObj.isRemote & player instanceof EntityPlayerMP) {
			final MinecraftServer server = getServer(player.worldObj);
			final WorldServer newWorldServer = server.worldServerForDimension(dimension);
			server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) player, dimension,
					new FOTeleporter(newWorldServer));
		}
	}

	private static Entity travelToDimension(final Entity thisE, final int newDimension, final double posX,
			final double posY, final double posZ) {
		if (!thisE.worldObj.isRemote && !thisE.isDead) {
			final MinecraftServer minecraftserver = getServer(thisE.worldObj);
			int currentDimension = thisE.dimension;
			WorldServer currentWorldServer = minecraftserver.worldServerForDimension(currentDimension);
			WorldServer newWorldServer = minecraftserver.worldServerForDimension(newDimension);
			thisE.dimension = newDimension;
			if (currentDimension == 1 && newDimension == 1) {
				newWorldServer = minecraftserver.worldServerForDimension(0);
				thisE.dimension = 0;
			}
			/*
			 * thisE.worldObj.removeEntity(thisE); thisE.isDead = false;
			 * thisE.worldObj.theProfiler.startSection("reposition");
			 * minecraftserver.getPlayerList().transferEntityToWorld(thisE,
			 * currentDimension, currentWorldServer, newWorldServer, (Teleporter) new
			 * Teleporter2(newWorldServer));
			 * thisE.worldObj.theProfiler.endStartSection("reloading"); final Entity entity
			 * = EntityList.createEntityByName(EntityList.getEntityString(thisE),
			 * newWorldServer); if (entity != null) { entity.copyDataFromOld(thisE);
			 * entity.setLocationAndAngles(posX, posY, posZ, entity.rotationYaw,
			 * entity.rotationPitch); worldserver2.spawnEntityInWorld(entity); }
			 * thisE.isDead = true; thisE.worldObj.theProfiler.endSection();
			 * worldserver.resetUpdateEntityTick(); worldserver2.resetUpdateEntityTick();
			 * thisE.worldObj.theProfiler.endSection();
			 */
			minecraftserver.getPlayerList().transferEntityToWorld(thisE, currentDimension, currentWorldServer,
					newWorldServer, new FOTeleporter(newWorldServer));

			return thisE;
		}
		return null;
	}

	private static class FOTeleporter extends Teleporter {

		public FOTeleporter(WorldServer worldIn) {
			super(worldIn);
		}

		@Override
		public boolean makePortal(final Entity par1Entity) {
			return false;
		}

		@Override
		public boolean placeInExistingPortal(final Entity entityIn, final float rotationYaw) {
			return false;
		}
	}
}