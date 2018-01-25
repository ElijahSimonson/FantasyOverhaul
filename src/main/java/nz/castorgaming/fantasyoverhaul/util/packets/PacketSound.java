package nz.castorgaming.fantasyoverhaul.util.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class PacketSound implements IMessage {

	private SoundEffect effect;
	private double x, y, z;
	private float volume, pitch;

	public PacketSound() {
	}

	public PacketSound(SoundEffect effect, Entity location) {
		this(effect, location, -1.0f, -1.0f);
	}

	public PacketSound(SoundEffect effect, Entity location, float volume, float pitch) {
		this.effect = effect;
		this.x = location.posX;
		this.y = location.posY;
		this.z = location.posZ;
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.effect = SoundEffect.values()[buf.readInt()];
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.volume = buf.readFloat();
		this.pitch = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.effect.ordinal());
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	public class Handler implements IMessageHandler<PacketSound, IMessage> {

		public IMessage onMessage(PacketSound message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketSound message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			if (message.volume == -1.0f) {
				message.volume = 0.5f;
			}
			if (message.pitch == -1.0f) {
				message.pitch = 0.4f / ((float) player.worldObj.rand.nextDouble() * 0.4f + 0.8f);
			}
			player.worldObj.playSound(null, new BlockPos(message.x, message.y, message.z), message.effect.event(),
					message.effect.category(), message.volume, message.pitch);
		}
	}

}
