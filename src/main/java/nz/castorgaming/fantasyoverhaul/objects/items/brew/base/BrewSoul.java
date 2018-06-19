package nz.castorgaming.fantasyoverhaul.objects.items.brew.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.powers.symbols.SymbolEffect;

public class BrewSoul extends Brew{
	
	private final SymbolEffect effect;

	public BrewSoul(String name, SymbolEffect effectIn) {
		super(name);
		effect = effectIn;
	}
	
	@Override
	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		effect.acquireKnowledge(player);
		return super.onDrunk(stack, world, player);
	}

}
