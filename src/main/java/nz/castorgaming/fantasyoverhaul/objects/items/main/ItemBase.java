package nz.castorgaming.fantasyoverhaul.objects.items.main;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHasModel;

public class ItemBase extends Item implements IHasModel {

	public ItemBase(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(FantasyOverhaul.foItems);
		InitArrays.ITEMS.add(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String tipText = stack.getUnlocalizedName() + ".tip";
		String toolTip = I18n.format(tipText);
		if (toolTip != null) {
			toolTip = toolTip.replace("|", "\n");
			for (String s : toolTip.split("\n")) {
				if (s.isEmpty()) {
					continue;
				}
				tooltip.add(s);
			}
		}
	}

	public boolean isMatch(ItemStack stack) {
		return (stack != null && this == stack.getItem());
	}

	public ItemStack createStack() {
		return createStack(1);
	}

	public ItemStack createStack(int size) {
		return new ItemStack(this, size);
	}

	@Override
	public void registerModels() {
		FantasyOverhaul.proxy.registerItemRenderer(this, 0, "inventory");
	}

}