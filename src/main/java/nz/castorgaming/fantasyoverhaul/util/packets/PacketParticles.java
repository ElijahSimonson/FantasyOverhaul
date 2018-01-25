package nz.castorgaming.fantasyoverhaul.util.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class PacketParticles implements IMessage {

	private ParticleEffect particleEffect;
	private SoundEffect soundEffect;
	private double x;
	private double y;
	private double z;
	private double width;
	private double height;
	private int color;

	public PacketParticles() {
	}

	public PacketParticles(ParticleEffect particleEffect, SoundEffect soundEffect, double x, double y, double z,
			double width, double height, int color) {
		this.particleEffect = particleEffect;
		this.soundEffect = soundEffect != null ? soundEffect : SoundEffect.NONE;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.color = color;
	}

	public PacketParticles(ParticleEffect particleEffect, SoundEffect soundEffect, Entity entity, double width,
			double height, int color) {
		this(particleEffect, soundEffect, entity.posX, entity.posY, entity.posZ, width, height, color);
	}

	public PacketParticles(ParticleEffect particleEffect, SoundEffect soundEffect, Entity entity, double width,
			double height) {
		this(particleEffect, soundEffect, entity.posX, entity.posY, entity.posZ, width, height, 16777215);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int ordinalParticle = buf.readInt();
		this.particleEffect = ParticleEffect.values()[ordinalParticle];
		int ordinalSound = buf.readInt();
		this.soundEffect = SoundEffect.values()[ordinalSound];
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.color = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.particleEffect.ordinal());
		buf.writeInt(this.soundEffect.ordinal());
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeDouble(this.width);
		buf.writeDouble(this.height);
		buf.writeInt(this.color);
	}

	public static class Handler implements IMessageHandler<PacketParticles, IMessage> {
		public IMessage onMessage(PacketParticles message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketParticles message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			World world = player.worldObj;
			double x = message.x;
			double y = message.y;
			double z = message.z;
			double width = message.width;
			double height = message.height;
			SoundEffect sound = message.soundEffect;
			int color = message.color;
			ParticleEffect particle = message.particleEffect;
			FantasyOverhaul.proxy.showParticleEffect(world, x, y, z, width, height, sound, color, particle);
		}
	}

}
