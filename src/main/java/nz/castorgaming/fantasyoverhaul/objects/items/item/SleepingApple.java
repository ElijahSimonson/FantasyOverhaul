package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Edible;
import nz.castorgaming.fantasyoverhaul.objects.worlds.WorldProviderDreamWorld;

public class SleepingApple extends Edible{

	public SleepingApple(String name, int heal, float sat, boolean wolfFav, boolean eatAny) {
		super(name, heal, sat, wolfFav, eatAny);
	}
	
	@Override
	public ItemStack onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		if (player.dimension == 0 && !world.isRemote && !WorldProviderDreamWorld.isPlayerGhost(player)) {
			WorldProviderDreamWorld.sendPlayerToSpiritWorld(player, 1.0);
			stack.stackSize = 0;
		}
		return stack;
	}

}
