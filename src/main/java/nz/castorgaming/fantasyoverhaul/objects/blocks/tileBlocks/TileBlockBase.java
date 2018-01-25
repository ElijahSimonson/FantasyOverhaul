package nz.castorgaming.fantasyoverhaul.objects.blocks.tileBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;

public class TileBlockBase extends Block implements ITileEntityProvider {

	protected boolean registerBlockName;
	protected boolean registerTileEntity;
	protected boolean registerWithCreateTab;
	protected final Class<? extends TileEntity> clazzTile;
	protected final Class<? extends ItemBlock> clazzItem;

	public TileBlockBase(Material mat, Class<? extends TileEntity> tile, String name) {
		this(mat, tile, null, name);
	}

	public TileBlockBase(Material mat, Class<? extends TileEntity> tile, Class<? extends ItemBlock> item, String name) {
		super(mat);
		registerBlockName = true;
		registerTileEntity = true;
		registerWithCreateTab = true;
		clazzTile = tile;
		clazzItem = item;
		setRegistryName(name);
		setUnlocalizedName(name);
		isBlockContainer = true;

		if (registerWithCreateTab) {
			setCreativeTab(FantasyOverhaul.foBlocks);
		}
		if (registerBlockName) {
			InitArrays.BLOCKS.add(this);
			if (clazzItem != null) {
				InitArrays.ITEMS.add(Item.getItemFromBlock(this));
			}
		}
		if (registerTileEntity) {
			GameRegistry.registerTileEntity(clazzTile, getUnlocalizedName());
		}

	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		try {
			return this.clazzTile.newInstance();
		} catch (Throwable e) {
			return null;
		}
	}

}
