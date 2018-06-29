package nz.castorgaming.fantasyoverhaul.util.classes;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketParticles;

public class TeleportUtil {

	public static void teleportToLocation(World world, int x, int y, int z, int dim, Entity entity, boolean preset) {
		teleportToLocation(world, x, y, z, dim, entity, preset, ParticleEffect.PORTAL, SoundEffect.MOB_ENDERMEN_PORTAL);
	}

	public static void teleportToLocation(World world, int x, int y, int z, int dim, Entity entity, boolean preset,
			ParticleEffect particle, SoundEffect sound) {
		boolean isVampire = CreatureUtilities.isVampire(entity);
		if (isVampire) {
			Reference.PACKET_HANDLER.sendToAllAround(
					new PacketParticles(ParticleEffect.SMOKE, SoundEffect.RANDOM_POOF, entity, 0.5, 2.0),
					TargetPointUtil.from(entity, 16.0));
		} else {
			Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(particle, sound, entity, 0.5, 2.0),
					TargetPointUtil.from(entity, 16.0));
		}
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (entity.dimension != dim) {
				if (preset) {
					player.setPosition(x, y, z);
				}
				travelToDimension(player, dim);
			}
			player.setPositionAndUpdate(x, y, z);
		} else if (entity instanceof EntityLiving) {
			if (entity.dimension != dim) {
				travelToDimension(entity, dim, x, y, z);
			} else {
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			}
		} else if (entity.dimension != dim) {
			travelToDimension(entity, dim, x, y, z);
		} else {
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
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

	public static boolean teleportToLocationSafely(World world, int x, int y, int z, int dim, Entity entity,
			boolean preset) {
		World targetWorld = world.getMinecraftServer().worldServerForDimension(dim);
		for (int i = 0; i < 16; i++) {
			int dy = y + i;
			if (dy < 250 && !BlockUtil.isReplaceableBlock(targetWorld, x, dy, z)
					&& BlockUtil.isReplaceableBlock(targetWorld, x, dy + 1, z)
					&& BlockUtil.isReplaceableBlock(targetWorld, x, dy + 2, z)) {
				teleportToLocation(world, x, dy + 1, z, dim, entity, preset);
				return true;
			}
			dy = y - i;
			if (i > 0 && dy > 1 && !BlockUtil.isReplaceableBlock(targetWorld, x, dy, z)
					&& BlockUtil.isReplaceableBlock(targetWorld, x, dy + 1, z)
					&& BlockUtil.isReplaceableBlock(targetWorld, x, dy + 2, z)) {
				teleportToLocation(world, x, dy + 1, z, dim, entity, preset);
				return true;
			}
		}
		return false;
	}

	public static void travelToDimension(EntityPlayer player, int dim) {
		if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
			MinecraftServer mcServer = player.getServer();
			int currDim = player.dimension;
			WorldServer currWorld = mcServer.worldServerForDimension(currDim);
			WorldServer newWorld = mcServer.worldServerForDimension(dim);
			player.dimension = dim;
			player.worldObj.removeEntity(player);
			currWorld.updateEntityWithOptionalForce(player, false);
			Entity player2 = EntityList.createEntityByName(EntityList.getEntityString(player), newWorld);
			if (player2 != null) {
				player2.copyLocationAndAnglesFrom(player);
				NBTTagCompound playerNBT = new NBTTagCompound();
				player.writeToNBT(playerNBT);
				playerNBT.removeTag("Dimension");
				player2.readFromNBT(playerNBT);
				boolean flag = player2.forceSpawn;
				player2.forceSpawn = true;
				newWorld.spawnEntityInWorld(player2);
				player2.forceSpawn = flag;
				newWorld.updateEntityWithOptionalForce(player2, false);
			}
			player.isDead = true;
		}
	}

	@Nullable
	private static Entity travelToDimension(Entity entity, int dim, int x, int y, int z) {
		if (!entity.worldObj.isRemote && !entity.isDead) {
			MinecraftServer mcServer = entity.getServer();
			int currDim = entity.dimension;
			WorldServer currWorld = mcServer.worldServerForDimension(currDim);
			WorldServer newWorld = mcServer.worldServerForDimension(dim);
			entity.dimension = dim;
			entity.worldObj.removeEntity(entity);
			currWorld.updateEntityWithOptionalForce(entity, false);
			Entity entity2 = EntityList.createEntityByName(EntityList.getEntityString(entity), newWorld);
			if (entity2 != null) {
				entity2.copyLocationAndAnglesFrom(entity2);
				NBTTagCompound entityNBT = new NBTTagCompound();
				entity2.writeToNBT(entityNBT);
				entityNBT.removeTag("Dimension");
				entity2.readFromNBT(entityNBT);
				boolean flag = entity2.forceSpawn;
				entity2.forceSpawn = true;
				newWorld.spawnEntityInWorld(entity2);
				entity2.forceSpawn = flag;
				entity2.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
				newWorld.updateEntityWithOptionalForce(entity2, false);
			}
			entity.isDead = true;
			return entity2;
		}
		return null;
	}
}
