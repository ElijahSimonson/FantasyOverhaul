package nz.castorgaming.fantasyoverhaul.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class witcheryTab extends CreativeTabs {

	private ItemStack icon;

	public witcheryTab(String label, ItemStack item) {
		super(label);
		// this.setBackgroundImageName("advFuncTab.png");
		icon = item;
	}

	@Override
	public Item getTabIconItem() {
		return icon.getItem();
	}

}
