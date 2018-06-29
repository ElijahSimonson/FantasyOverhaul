package nz.castorgaming.fantasyoverhaul.objects.items.main;

import net.minecraft.item.ItemStack;

public class Bolt extends GeneralItem {

	public Bolt(String name) {
		super(name);
	}

	public static Bolt getBolt(ItemStack stack) {
		if (stack != null && stack.getItem() instanceof Bolt) {
			return (Bolt) stack.getItem();
		}
		return null;
	}

}
