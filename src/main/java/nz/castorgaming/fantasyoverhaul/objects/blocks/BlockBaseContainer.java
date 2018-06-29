package nz.castorgaming.fantasyoverhaul.objects.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;

public class BlockBaseContainer extends BlockContainer {

	public BlockBaseContainer(String name, Material material) {
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(FantasyOverhaul.foBlocks);

		InitArrays.BLOCKS.add(this);
		InitArrays.ITEMS.add(new ItemBlock(this).setRegistryName(getRegistryName()));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return null;
	}

}
