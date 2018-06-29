package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;

public class ItemQuicklime extends Brew {

	public ItemQuicklime(String name) {
		super(name);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		throwBrew(itemStackIn, worldIn, playerIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

}
