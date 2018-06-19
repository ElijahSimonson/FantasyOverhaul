package nz.castorgaming.fantasyoverhaul.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.util.interfaces.IHasModel;

public class BlockBase extends Block implements IHasModel {

	public BlockBase(String name, Material material) {
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(FantasyOverhaul.foBlocks);

		InitArrays.BLOCKS.add(this);
		InitArrays.ITEMS.add(new ItemBlock(this).setRegistryName(getRegistryName()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		FantasyOverhaul.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}

}
