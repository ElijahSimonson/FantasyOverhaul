package nz.castorgaming.fantasyoverhaul.objects.items.main;

import net.minecraft.item.ItemPickaxe;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHasModel;

public class ToolPickaxe extends ItemPickaxe implements IHasModel {

	public ToolPickaxe(String name, ToolMaterial material) {
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(FantasyOverhaul.foItems);

		InitArrays.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		FantasyOverhaul.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
