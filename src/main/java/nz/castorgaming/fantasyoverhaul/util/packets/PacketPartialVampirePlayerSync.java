package nz.castorgaming.fantasyoverhaul.util.packets;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;

public class PacketPartialVampirePlayerSync implements IMessage {

	private UUID entityID;
	private int blood;

	public PacketPartialVampirePlayerSync() {
	}

	public PacketPartialVampirePlayerSync(EntityPlayer player) {
		entityID = player.getUniqueID();
		blood = IPlayerVampire.get(player).getHumanBlood();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, entityID.toString());
		buf.writeInt(blood);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		blood = buf.readInt();
	}

	public static class Handler implements IMessageHandler<PacketPartialVampirePlayerSync, IMessage> {
		@Override
		public IMessage onMessage(PacketPartialVampirePlayerSync message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketPartialVampirePlayerSync message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			if (player != null) {
				Entity entity = player.worldObj.getPlayerEntityByUUID(message.entityID);
				if (entity instanceof EntityPlayer) {
					PlayerVampire vamp = IPlayerVampire.get((EntityPlayer) entity);
					if (vamp != null) {
						vamp.setHumanBlood(message.blood);
					}
				}
			}
		}
	}

}
