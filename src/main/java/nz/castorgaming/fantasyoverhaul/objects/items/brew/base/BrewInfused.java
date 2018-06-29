package nz.castorgaming.fantasyoverhaul.objects.items.brew.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.powers.brews.effects.InfusionBrewEffect;

public class BrewInfused extends Brew {

	private final InfusionBrewEffect effect;

	public BrewInfused(String name, InfusionBrewEffect effectIn) {
		super(name);
		effect = effectIn;
	}

	@Override
	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		effect.drunk(world, player, stack);
		return super.onDrunk(stack, world, player);
	}

}
