package nz.castorgaming.fantasyoverhaul.util.packets;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.objects.potions.effects.Resizing;

public class PacketSyncEntitySize implements IMessage {

	private UUID entityUUID;
	private float width, height, stepSize, eyeHeight;

	public PacketSyncEntitySize() {
	}

	public PacketSyncEntitySize(Entity entity) {
		entityUUID = ((entity != null) ? entity.getUniqueID() : new UUID(0L, 0L));
		width = entity.width;
		height = entity.height;
		stepSize = entity.stepHeight;
		if (entity instanceof EntityPlayer) {
			eyeHeight = entity.getEyeHeight();
		}
		else {
			eyeHeight = -1.0f;
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityUUID = new UUID(buf.readLong(), buf.readLong());
		width = buf.readFloat();
		height = buf.readFloat();
		stepSize = buf.readFloat();
		eyeHeight = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(entityUUID.getMostSignificantBits());
		buf.writeLong(entityUUID.getLeastSignificantBits());
		buf.writeFloat(width);
		buf.writeFloat(height);
		buf.writeFloat(stepSize);
		buf.writeFloat(eyeHeight);
	}

	public static class Handler implements IMessageHandler<PacketSyncEntitySize, IMessage> {
		@Override
		public IMessage onMessage(PacketSyncEntitySize message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketSyncEntitySize message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			for (Entity entity : player.worldObj.getLoadedEntityList()) {
				if (entity.getUniqueID().toString().equals(message.entityUUID.toString())) {
					Resizing.setEntitySize(entity, message.width, message.height);
					entity.stepHeight = message.stepSize;
					break;
				}
			}
		}
	}

}
