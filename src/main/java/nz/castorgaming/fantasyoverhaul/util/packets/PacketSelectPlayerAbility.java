package nz.castorgaming.fantasyoverhaul.util.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire.VampirePower;

public class PacketSelectPlayerAbility implements IMessage {

	private int vampirePower;
	private boolean shouldTrigger;

	public PacketSelectPlayerAbility() {
	}

	public PacketSelectPlayerAbility(EntityPlayer player, boolean trigger) {
		vampirePower = IPlayerVampire.get(player).getSelectedVampirePower().toInt();
		shouldTrigger = trigger;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(vampirePower);
		buf.writeBoolean(shouldTrigger);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		vampirePower = buf.readInt();
		shouldTrigger = buf.readBoolean();
	}

	public static class Handler implements IMessageHandler<PacketSelectPlayerAbility, IMessage> {
		@Override
		public IMessage onMessage(PacketSelectPlayerAbility message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketSelectPlayerAbility message, MessageContext ctx) {
			EntityPlayer player = FantasyOverhaul.proxy.getPlayer(ctx);
			PlayerVampire vamp = IPlayerVampire.get(player);
			if (vamp != null) {
				vamp.setSelectedVampirePower(VampirePower.fromInt(message.vampirePower), false);
				if (message.shouldTrigger) {
					vamp.triggerSelectedVampirePower();
				}
			}
		}
	}

}
