package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.GeneralItem;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class BoneNeedle extends GeneralItem{

	public BoneNeedle(String name) {
		super(name);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (IPlayerVampire.get(playerIn).isVampire()) {
			IBlockState block = worldIn.getBlockState(pos);
			if (block == Blocks.WOOL && block.getProperties().get(BlockColored.COLOR) == EnumDyeColor.WHITE) {
				PlayerVampire vamp = IPlayerVampire.get(playerIn);
				if (vamp.getVampireLevel() >= 4 && vamp.decreaseBloodPower(125, true)) {
					worldIn.setBlockState(pos, BlockInit.BLOODED_WOOL.getDefaultState());
					ParticleEffect.REDDUST.send(SoundEffect.RANDOM_DRINK, worldIn, pos, 1.0, 1.0, 16);
					return EnumActionResult.SUCCESS;
				}
			}
			SoundEffect.NOTE_SNARE.playOnlyTo(playerIn);
			return EnumActionResult.SUCCESS;
		}
	}

}
