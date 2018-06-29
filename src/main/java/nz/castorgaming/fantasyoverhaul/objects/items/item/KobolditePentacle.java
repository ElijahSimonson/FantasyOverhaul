package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;

public class KobolditePentacle extends GeneralItem {

	public KobolditePentacle(String name) {
		super(name);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.getBlockState(pos).getBlock() == BlockInit.ALTAR && facing == EnumFacing.UP
				&& worldIn.getBlockState(new BlockPos(pos).up()).getBlock() == Blocks.AIR) {
			BlockPlacedItem.placeItemInWorld(stack, playerIn, worldIn, new BlockPos(pos).up());
			playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

}
