package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.BlockInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Drinkable;

public class DemonHeart extends Drinkable{

	public DemonHeart(String name, PotionEffect... effectsIn) {
		super(name, EnumAction.EAT, effectsIn);
	}
	
	public ItemStack onFoodEaten(ItemStack stack, World world, EntityPlayer player){
		super.onFoodEaten(stack, world, player);
		if (player.isSneaking()) {
			return stack;
		}
		player.setFire(2640);
		return stack;
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			placeBlock(BlockInit.DEMON_HEART, stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		}
		
	}

}
