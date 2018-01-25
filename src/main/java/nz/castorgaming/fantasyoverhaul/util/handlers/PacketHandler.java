package nz.castorgaming.fantasyoverhaul.util.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler {

	private static int packetID = 0;

	public static SimpleNetworkWrapper INSTANCE = null;

	public static int nextID() {
		return packetID++;
	}

	public static void registerMessages() {

	}

	public static void registerMessages(String channelName) {
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
		registerMessages();
	}

	public PacketHandler() {
	}

	public void sendTo(IMessage message, EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			INSTANCE.sendTo(message, (EntityPlayerMP) player);
		}
	}

	public void sendTo(IMessage message, EntityPlayerMP player) {
		PacketHandler.INSTANCE.sendTo(message, player);
	}

	public void sendTo(Packet<?> packet, EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP mp = (EntityPlayerMP) player;
			mp.connection.sendPacket(packet);
		}
	}

	public void sendToAll(IMessage message) {
		INSTANCE.sendToAll(message);
	}

	public void sendToAll(Packet<?> packet) {
		for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().getServer().worldServers) {
			this.sendToDimension(packet, world);
		}
	}

	public void sendToAllAround(IMessage message, TargetPoint targetPoint) {
		INSTANCE.sendToAllAround(message, targetPoint);
	}

	public void sendToDimension(IMessage message, int dimensionId) {
		INSTANCE.sendToDimension(message, dimensionId);
	}

	public void sendToDimension(Packet<?> packet, World world) {
		for (Object obj : world.playerEntities) {
			if (!(obj instanceof EntityPlayerMP)) {
				EntityPlayerMP mp = (EntityPlayerMP) obj;
				mp.connection.sendPacket(packet);
			}
		}
	}

	public void sendToDimension(Packet<?> packet, World world, TargetPoint targetpoint) {
		double RANGE_SQ = targetpoint.range * targetpoint.range;
		for (Object obj : world.playerEntities) {
			EntityPlayerMP mp;
			if (!(obj instanceof EntityPlayerMP) || (mp = (EntityPlayerMP) obj).getDistanceSq(targetpoint.x,
					targetpoint.y, targetpoint.z) > RANGE_SQ) {
				continue;
			}
			mp.connection.sendPacket(packet);
		}
	}

	public void sendToServer(IMessage message) {
		INSTANCE.sendToServer(message);
	}

}
