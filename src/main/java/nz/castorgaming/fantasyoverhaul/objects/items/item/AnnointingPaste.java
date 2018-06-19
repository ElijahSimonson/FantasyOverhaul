package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.block.Block;
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
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class AnnointingPaste extends GeneralItem{

	public AnnointingPaste(String name) {
		super(name);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			Block block = worldIn.getBlockState(pos).getBlock();
			if (block == Blocks.CAULDRON) {
				worldIn.setBlockState(pos, BlockInit.CAULDRON);
				--stack.stackSize;
				ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_FIZZ, worldIn, pos, 1.0, 1.0, 16);
				ParticleEffect.LARGE_EXPLODE.send(SoundEffect.RANDOM_LEVELUP, worldIn, pos, 1.0, 1.0, 16);
			return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}

}
