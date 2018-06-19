package nz.castorgaming.fantasyoverhaul.objects.items.main;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.util.classes.GeneralUtil;

public class ItemMarkupBook extends GeneralItem{
	
	private final int dialogID;
	private final int[] creativeMetaValues;

	public ItemMarkupBook(String name, int dialogID) {
		this(name, dialogID, new int[] {0});		
	}

	public ItemMarkupBook(String name, int dialogID2, int[] is) {
		super(name);
		dialogID = dialogID2;
		hasSubtypes = true;
		creativeMetaValues = is;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		playerIn.openGui(FantasyOverhaul.instance, dialogID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String itemName = getUnlocalizedName();
		for (String s : GeneralUtil.resource("item." + itemName + ".tip").split("\n")) {
			if (!s.isEmpty()) {
				tooltip.add(s);
			}
		}
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int meta : creativeMetaValues) {
			subItems.add(new ItemStack(this, 1, meta));
		}
	}
	
	public void onBookRead(ItemStack stack, World world, EntityPlayer player) {};
	
	

}
