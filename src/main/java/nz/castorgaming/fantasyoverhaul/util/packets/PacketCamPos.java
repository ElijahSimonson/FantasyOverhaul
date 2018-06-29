package nz.castorgaming.fantasyoverhaul.util.packets;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;

public class PacketCamPos implements IMessage {

	private boolean active;
	private boolean updatePosition;
	private UUID entityID;

	public PacketCamPos() {
	}

	public PacketCamPos(boolean isActive, boolean shouldUpdate, Entity entity) {
		active = isActive;
		updatePosition = shouldUpdate;
		entityID = entity.getUniqueID();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		buf.writeBoolean(active);
		buf.writeBoolean(updatePosition);
		ByteBufUtils.writeUTF8String(buf, entityID.toString());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		active = buf.readBoolean();
		updatePosition = buf.readBoolean();
		String id = ByteBufUtils.readUTF8String(buf);
		entityID = UUID.fromString(id);
	}

	public static class Handler implements IMessageHandler<PacketCamPos, IMessage> {
		public IMessage onMessage(PacketCamPos message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketCamPos message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			if (message.active) {
				PlayerRender.ticksSinceActive = Minecraft.getSystemTime();
				if (message.updatePosition) {
					PlayerRender.moveCameraToEntityID = message.entityID;
				}
			}
		}
	}
}
