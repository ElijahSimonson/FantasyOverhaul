package nz.castorgaming.fantasyoverhaul.util.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;

public class PushTarget implements IMessage {

	private double motionX, motionY, motionZ;

	public PushTarget() {
	}

	public PushTarget(double motionX, double motionY, double motionZ) {
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(motionX);
		buf.writeDouble(motionY);
		buf.writeDouble(motionZ);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		motionX = buf.readDouble();
		motionY = buf.readDouble();
		motionZ = buf.readDouble();
	}

	public static class Handler implements IMessageHandler<PushTarget, IMessage> {
		@Override
		public IMessage onMessage(PushTarget message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PushTarget message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			player.motionX = message.motionX;
			player.motionY = message.motionY;
			player.motionZ = message.motionZ;
		}
	}

}
