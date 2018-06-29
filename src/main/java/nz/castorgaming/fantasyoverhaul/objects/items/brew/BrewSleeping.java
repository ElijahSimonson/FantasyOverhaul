package nz.castorgaming.fantasyoverhaul.objects.items.brew;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.objects.worlds.WorldProviderDreamWorld;

public class BrewSleeping extends Brew {

	public BrewSleeping(String name) {
		super(name);
	}

	@Override
	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		super.onDrunk(stack, world, player);
		if (player.dimension == 0 && !world.isRemote && !WorldProviderDreamWorld.isPlayerGhost(player)) {
			WorldProviderDreamWorld.sendPlayerToSpiritWorld(player, 0.998);
			stack.stackSize = 0;
			world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5f,
					world.rand.nextFloat() * 0.1f + 0.9f);
		}
		return (player.inventory.getCurrentItem() != null) ? player.inventory.getCurrentItem() : stack;
	}

}
