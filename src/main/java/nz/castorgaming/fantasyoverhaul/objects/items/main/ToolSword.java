package nz.castorgaming.fantasyoverhaul.objects.items.main;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHasModel;

public class ToolSword extends ItemSword implements IHasModel {

	public ToolSword(String name, ToolMaterial material) {
		super(material);
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

	@Override
	public void registerModels() {
		FantasyOverhaul.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
