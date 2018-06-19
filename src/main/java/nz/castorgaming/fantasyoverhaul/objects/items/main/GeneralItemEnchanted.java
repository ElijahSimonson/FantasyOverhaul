package nz.castorgaming.fantasyoverhaul.objects.items.main;

import net.minecraft.item.ItemStack;

public class GeneralItemEnchanted extends GeneralItem {

	public GeneralItemEnchanted(String name) {
		super(name);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public boolean hasEffect(ItemStack stack, int pass) {
		return true;
	}
}
