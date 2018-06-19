package nz.castorgaming.fantasyoverhaul.objects.items.brew;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;

public class BrewDepths extends Brew{

	public BrewDepths(String name) {
		super(name);
	}
	
	@Override
	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			Infusion.getNBT(player).setInteger(Reference.INFUSION_DEPTHS, 300);
		}
		
		return super.onDrunk(stack, world, player);
		
	}

}
