package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;

public class InfernalBlood extends GeneralItem {

	public InfernalBlood(String name) {
		super(name);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() == BlockInit.WICKER_BUNDLE
				&& BlockWickerBundle.limitToValidState(state) == BlockWickerBundle.Type.DRY) {
			if (!worldIn.isRemote) {
				for (int uses = 5, y = pos.getY() - 1; y <= pos.getY() + 1 && uses > 0; y++) {
					for (int x = pos.getX() - 1; x <= pos.getX() + 1 && uses > 0; x++) {
						for (int z = pos.getZ() - 1; z <= pos.getZ() + 1 && uses > 0; z++) {
							BlockPos currPos = new BlockPos(x, y, z);
							IBlockState blockState = worldIn.getBlockState(currPos);
							if (blockState.getBlock() == BlockInit.WICKER_BUNDLE
									&& BlockWickerBundle.limitToValidState(state) == BlockWickerBundle.Type.DRY) {
								worldIn.setBlockState(currPos, BlockInit.WICKER_BUNDLE.getDefaultState()
										.withProperty(BlockWickerBundle.STAINED, true));
								--uses;
							}
						}
					}
				}
			}
			--stack.stackSize;
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

}
