package nz.castorgaming.fantasyoverhaul.objects.items.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;

public class ItemChalice extends GeneralItem{

	public ItemChalice(String name) {
		super(name);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getMetadata();
		return super.getUnlocalizedName(stack) + "." + CHALICE_STATE.byMetadata(i).getUnlocalizedName();
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
	}
	
	@Override
	public void registerModels() {
		for (CHALICE_STATE state : CHALICE_STATE.values()) {	
			FantasyOverhaul.proxy.registerItemRenderer(this, state.getMetadata(), state.getUnlocalizedName());
		}
	}
	
	enum CHALICE_STATE{
		EMPTY(0, "empty"), FILLED(1, "filled");
		
		private int meta;
		private String name;
		private static CHALICE_STATE[] META_LOOKUP = new CHALICE_STATE[values().length];
		
		private CHALICE_STATE(int meta, String name) {
			this.meta = meta;
			this.name = name;
		}
		
		public int getMetadata() {
			return meta;
		}
		
		public String getUnlocalizedName() {
			return name;
		}
		
		
		
		public static CHALICE_STATE byMetadata(int meta) {
			if (meta < 0 || meta > 1) {
				meta = 1;
			}
			return META_LOOKUP[meta];
		}
		
		static {
			for (CHALICE_STATE state : values()) {
				META_LOOKUP[state.getMetadata()] = state;
			}
		}
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing != EnumFacing.UP) {
			return EnumActionResult.FAIL;
		}
		if (worldIn.getBlockState(pos).isSideSolid(worldIn, pos, facing)) {
			pos = pos.up();
			worldIn.setBlockState(pos, BlockInit.CHALICE.getDefaultState().withProperty(BlockChalice.FILLED, true));
			playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
	
}
