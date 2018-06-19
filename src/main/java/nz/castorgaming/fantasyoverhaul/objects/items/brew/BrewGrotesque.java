package nz.castorgaming.fantasyoverhaul.objects.items.brew;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketPlayerStyle;

public class BrewGrotesque extends Brew{

	public BrewGrotesque(String name) {
		super(name);
	}
	
	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		super.onDrunk(stack,world, player );
		if (!world.isRemote) {
			Infusion.getNBT(player).setInteger(Reference.INFUSION_GROTESQUE, 1200);
			Reference.PACKET_HANDLER.sendToDimension(new PacketPlayerStyle(player), player.dimension);
		}
		return stack;
	}

}
