package nz.castorgaming.fantasyoverhaul.powers.infusions.player;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.EnderInhibition;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.classes.ChatUtilities;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;
import nz.castorgaming.fantasyoverhaul.util.classes.DimensionalLocation;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class InfusionOtherwhere extends Infusion {
	private static final String RECALL_LOCATON_KEY = "WITCRecall";
	private static final int SAVE_RECALL_POINT_THRESHOLD = 60;

	public InfusionOtherwhere(String name) {
		super(name);
	}

	public InfusionOtherwhere() {
		super("otherwhere");
	}

	@Override
	public ResourceLocation getPowerBarIcon(final EntityPlayer player, final int index) {
		return new ResourceLocation("portal");
	}

	@Override
	public void onLeftClickEntity(final ItemStack itemstack, final World world, final EntityPlayer player,
			final Entity otherEntity) {
		if (world.isRemote) {
			return;
		}
		if (otherEntity instanceof EntityLivingBase) {
			final EntityLivingBase otherLivingEntity = (EntityLivingBase) otherEntity;
			if (player.isSneaking()) {
				final DimensionalLocation recallLocation = this.recallLocation(Infusion.getNBT(player), "WITCRecall");
				if (recallLocation != null && recallLocation.dimension != Config.instance().dimensionDreamID
						&& recallLocation.dimension != Config.instance().dimensionTormentID
						&& recallLocation.dimension != Config.instance().dimensionMirrorID
						&& world.provider.getDimension() != Config.instance().dimensionDreamID
						&& world.provider.getDimension() != Config.instance().dimensionTormentID
						&& world.provider.getDimension() != Config.instance().dimensionMirrorID
						&& !EnderInhibition.isActive(player, 2) && this.consumeCharges(world, player, 4, false)) {
					if (player instanceof EntityPlayerMP && !isConnectionClosed((EntityPlayerMP) player)) {
						player.fallDistance = 0.0f;
						EntityUtil.teleportToLocation(world, recallLocation.posX, recallLocation.posY,
								recallLocation.posZ, recallLocation.dimension, player, true);
						otherLivingEntity.fallDistance = 0.0f;
						if (!EnderInhibition.isActive(otherLivingEntity, 2)) {
							EntityUtil.teleportToLocation(world, recallLocation.posX, recallLocation.posY,
									recallLocation.posZ, recallLocation.dimension, otherLivingEntity, true);
						}
					}
				} else {
					world.playSound(player, player.getPosition(), SoundEffect.NOTE_PLING.event(),
							SoundEffect.NOTE_PLING.category(), 0.5f, 0.4f / ((float) Math.random() * 0.4f + 0.8f));
				}
			} else if (!EnderInhibition.isActive(player, 2) && this.consumeCharges(world, player, 2, true)) {
				final double HIKE_HEIGHT = 8.0;
				final RayTraceResult hitMOP = raytraceUpBlocks(world, player, true, HIKE_HEIGHT);
				final double hikeModified = (hitMOP == null) ? 8.0
						: Math.min(hitMOP.getBlockPos().getY() - otherLivingEntity.posY - 2.0, HIKE_HEIGHT);
				final RayTraceResult hitMOP2 = raytraceUpBlocks(world, otherLivingEntity, true, 8.0);
				final double hikeModified2 = (hitMOP2 == null) ? 8.0
						: Math.min(hitMOP2.getBlockPos().getY() - otherLivingEntity.posY - 2.0, HIKE_HEIGHT);
				if (player instanceof EntityPlayerMP && !isConnectionClosed((EntityPlayerMP) player)
						&& hikeModified > 0.0 && hikeModified2 > 0.0) {
					EntityUtil.teleportToLocation(world, player.posX, player.posY + hikeModified, player.posZ,
							player.dimension, player, true);
					if (!EnderInhibition.isActive(otherLivingEntity, 2)) {
						EntityUtil.teleportToLocation(world, otherLivingEntity.posX,
								otherLivingEntity.posY + hikeModified2, otherLivingEntity.posZ,
								otherLivingEntity.dimension, otherLivingEntity, true);
					}
				}
			}
		}
	}

	@Override
	public void onUsingItemTick(final ItemStack itemstack, final World world, final EntityPlayer player,
			final int countdown) {
		final int elapsedTicks = this.getMaxItemUseDuration(itemstack) - countdown;
		if (player.isSneaking() && elapsedTicks == 60) {
			if (!world.isRemote) {
				ChatUtilities.sendTranslated(TextFormatting.GRAY, player, "witchery.infuse.cansetrecall",
						new Object[0]);
			}
			player.worldObj.playSound(player, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS,
					0.5f, 0.4f / ((float) Math.random() * 0.4f + 0.8f));
		} else if (!player.isSneaking() && elapsedTicks > 0 && elapsedTicks % 20 == 0) {
			final int MAX_TELEPORT_DISTANCE = 40 + 20 * (elapsedTicks / 20);
			final RayTraceResult hitMOP = doCustomRayTrace(world, player, true, MAX_TELEPORT_DISTANCE);
			if (hitMOP != null) {
				player.worldObj.playSound(player, player.getPosition(), SoundEffect.RANDOM_ORB.event(),
						SoundEffect.RANDOM_ORB.category(), 0.5f, 0.4f / ((float) Math.random() * 0.4f + 0.8f));
				if (!world.isRemote) {
					ChatUtilities.sendTranslated(TextFormatting.GRAY, (ICommandSender) player,
							"witchery.infuse.canteleport", new Object[0]);
				}
			} else {
				player.worldObj.playSound(player, player.getPosition(), SoundEffect.RANDOM_POP.event(),
						SoundEffect.RANDOM_POP.category(), 0.5f, 0.4f / ((float) Math.random() * 0.4f + 0.8f));
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(final ItemStack itemstack, final World world, final EntityPlayer player,
			final int countdown) {
		if (world.isRemote) {
			return;
		}
		final int elapsedTicks = this.getMaxItemUseDuration(itemstack) - countdown;
		if (player.isSneaking() && elapsedTicks >= 60) {
			this.storeLocation(Infusion.getNBT(player), "WITCRecall", player);
			SoundEffect.RANDOM_FIZZ.playAtPlayer(world, player);
		} else if (player.isSneaking()) {
			final DimensionalLocation recallLocation = this.recallLocation(Infusion.getNBT(player), "WITCRecall");
			if (recallLocation != null && recallLocation.dimension != Config.instance().dimensionDreamID
					&& recallLocation.dimension != Config.instance().dimensionTormentID
					&& recallLocation.dimension != Config.instance().dimensionMirrorID
					&& world.provider.getDimension() != Config.instance().dimensionDreamID
					&& world.provider.getDimension() != Config.instance().dimensionTormentID
					&& world.provider.getDimension() != Config.instance().dimensionMirrorID
					&& !EnderInhibition.isActive(player, 2) && this.consumeCharges(world, player, 2, false)) {
				if (player instanceof EntityPlayerMP && !isConnectionClosed((EntityPlayerMP) player)) {
					player.fallDistance = 0.0f;
					EntityUtil.teleportToLocation(world, recallLocation.posX, recallLocation.posY, recallLocation.posZ,
							recallLocation.dimension, player, true);
					Infusion.setCooldown(world, itemstack, 1500);
				}
			} else {
				world.playSound(player, player.getPosition(), SoundEffect.NOTE_SNARE.event(),
						SoundEffect.NOTE_SNARE.category(), 0.5f, 0.4f / ((float) Math.random() * 0.4f + 0.8f));
			}
		} else {
			final int MAX_TELEPORT_DISTANCE = 40 + 20 * (elapsedTicks / 20);
			final RayTraceResult hitMOP = doCustomRayTrace(world, player, true, MAX_TELEPORT_DISTANCE);
			if (hitMOP != null && !EnderInhibition.isActive(player, 2)
					&& this.consumeCharges(world, player, 1, false)) {
				ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, player, 0.5, 2.0, 16);
				teleportEntity(player, hitMOP);
				ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, player, 0.5, 2.0, 16);
				Infusion.setCooldown(world, itemstack, 1500);
			} else {
				world.playSound(player, player.getPosition(), SoundEffect.NOTE_SNARE.event(),
						SoundEffect.NOTE_SNARE.category(), 0.5f, 0.4f / ((float) Math.random() * 0.4f + 0.8f));
				if (hitMOP == null && !world.isRemote) {
					ChatUtilities.sendTranslated(TextFormatting.RED, (ICommandSender) player,
							"witchery.infuse.cannotteleport", new Object[0]);
				}
			}
		}
	}

	private void storeLocation(final NBTTagCompound nbt, final String key, final EntityPlayer player) {
		final DimensionalLocation location = new DimensionalLocation(player);
		location.saveToNBT(nbt, key);
		if (!player.worldObj.isRemote) {
			ChatUtilities.sendTranslated(TextFormatting.GRAY, player, "witchery.infuse.setrecall",
					player.worldObj.provider.getDimension(),
					Integer.valueOf(MathHelper.floor_double(location.posX)).toString(),
					Integer.valueOf(MathHelper.floor_double(location.posY)).toString(),
					Integer.valueOf(MathHelper.floor_double(location.posZ)).toString());
		}
	}

	private DimensionalLocation recallLocation(final NBTTagCompound nbtTag, final String key) {
		final DimensionalLocation location = new DimensionalLocation(nbtTag, key);
		if (!location.isValid) {
			return null;
		}
		return location;
	}

	public static void teleportEntity(final EntityPlayer entityPlayer, final RayTraceResult hitMOP) {
		if (hitMOP != null && entityPlayer instanceof EntityPlayerMP) {
			final EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
			if (!isConnectionClosed(player)) {
				switch (hitMOP.typeOfHit) {
				case ENTITY: {
					player.setPositionAndUpdate(hitMOP.hitVec.xCoord, hitMOP.hitVec.yCoord, hitMOP.hitVec.zCoord);
					break;
				}
				case BLOCK: {
					double hitx = hitMOP.hitVec.xCoord;
					double hity = hitMOP.hitVec.yCoord;
					double hitz = hitMOP.hitVec.zCoord;
					switch (hitMOP.sideHit) {
					case DOWN: {
						hity -= 2.0;
					}
					case NORTH: {
						hitz -= 0.5;
						break;
					}
					case SOUTH: {
						hitz += 0.5;
						break;
					}
					case WEST: {
						hitx -= 0.5;
						break;
					}
					case EAST: {
						hitx += 0.5;
						break;
					}
					default:
						break;
					}
					player.fallDistance = 0.0f;
					player.setPositionAndUpdate(hitx, hity, hitz);
					break;
				}
				default:
					break;
				}
			}
		}
	}

	public static RayTraceResult doCustomRayTrace(final World world, final EntityPlayer player,
			final boolean collisionFlag, final double reachDistance) {
		final RayTraceResult pickedBlock = raytraceBlocks(world, player, collisionFlag, reachDistance);
		final RayTraceResult pickedEntity = raytraceEntities(world, player, collisionFlag, reachDistance);
		if (pickedBlock == null) {
			return pickedEntity;
		}
		if (pickedEntity == null) {
			return pickedBlock;
		}
		final Vec3d playerPosition = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		final double dBlock = pickedBlock.hitVec.distanceTo(playerPosition);
		final double dEntity = pickedEntity.hitVec.distanceTo(playerPosition);
		if (dEntity < dBlock) {
			return pickedEntity;
		}
		return pickedBlock;
	}

	public static RayTraceResult raytraceEntities(final World world, final EntityPlayer player,
			final boolean collisionFlag, final double reachDistance) {
		RayTraceResult pickedEntity = null;
		final Vec3d playerPosition = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		final Vec3d playerLook = player.getLookVec();
		final Vec3d playerViewOffset = new Vec3d(playerPosition.xCoord + playerLook.xCoord * reachDistance,
				playerPosition.yCoord + playerLook.yCoord * reachDistance,
				playerPosition.zCoord + playerLook.zCoord * reachDistance);
		final double playerBorder = 1.1 * reachDistance;
		final AxisAlignedBB boxToScan = player.getEntityBoundingBox().expand(playerBorder, playerBorder, playerBorder);
		final List<Entity> entitiesHit = world.getEntitiesWithinAABBExcludingEntity(player, boxToScan);
		double closestEntity = reachDistance;
		if (entitiesHit == null || entitiesHit.isEmpty()) {
			return null;
		}
		for (final Entity entityHit : entitiesHit) {
			if (entityHit != null && entityHit.canBeCollidedWith() && entityHit.getEntityBoundingBox() != null) {
				final float border = entityHit.getCollisionBorderSize();
				final AxisAlignedBB aabb = entityHit.getEntityBoundingBox().expand(border, border, border);
				final RayTraceResult hitMOP = aabb.calculateIntercept(playerPosition, playerViewOffset);
				if (hitMOP == null) {
					continue;
				}
				if (aabb.isVecInside(playerPosition)) {
					if (0.0 >= closestEntity && closestEntity != 0.0) {
						continue;
					}
					pickedEntity = new RayTraceResult(entityHit);
					pickedEntity.hitVec = hitMOP.hitVec;
					closestEntity = 0.0;
				} else {
					final double distance = playerPosition.distanceTo(hitMOP.hitVec);
					if (distance >= closestEntity && closestEntity != 0.0) {
						continue;
					}
					pickedEntity = new RayTraceResult(entityHit);
					pickedEntity.hitVec = hitMOP.hitVec;
					closestEntity = distance;
				}
			}
		}
		return pickedEntity;
	}

	private static boolean isConnectionClosed(final EntityPlayerMP player) {
		return !player.connection.netManager.isChannelOpen();
	}

	public static RayTraceResult raytraceBlocks(final World world, final EntityPlayer player,
			final boolean collisionFlag, final double reachDistance) {
		final Vec3d playerPosition = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		final Vec3d playerLook = player.getLookVec();
		final Vec3d playerViewOffset = new Vec3d(playerPosition.xCoord + playerLook.xCoord * reachDistance,
				playerPosition.yCoord + playerLook.yCoord * reachDistance,
				playerPosition.zCoord + playerLook.zCoord * reachDistance);
		return world.rayTraceBlocks(playerPosition, playerViewOffset, collisionFlag, !collisionFlag, false);
	}

	private static RayTraceResult raytraceUpBlocks(final World world, final EntityLivingBase player,
			final boolean collisionFlag, final double reachDistance) {
		final Vec3d playerPosition = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		final Vec3d playerUp = new Vec3d(0.0, 1.0, 0.0);
		final Vec3d playerViewOffset = new Vec3d(playerPosition.xCoord + playerUp.xCoord * reachDistance,
				playerPosition.yCoord + playerUp.yCoord * reachDistance,
				playerPosition.zCoord + playerUp.zCoord * reachDistance);
		return world.rayTraceBlocks(playerPosition, playerViewOffset, collisionFlag, !collisionFlag, false);
	}
}
