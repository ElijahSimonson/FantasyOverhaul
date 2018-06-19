package nz.castorgaming.fantasyoverhaul.objects.items.main;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;

public class Contract extends GeneralItem{

	public Contract(String name) {
		super(name);
	}
	
	public static boolean isBoundContract(ItemStack stack) {	
		if (stack.getItem() instanceof Contract) {
			return ItemInit.TAGLOCK_KIT.isTaglockPresent(stack);
		}
		return false;
	}
	
	public static EntityLivingBase getBoundEntity(World world, EntityPlayer player, ItemStack stack) {
		return (EntityLivingBase) ItemInit.TAGLOCK_KIT.getBoundEntity(world, player, stack);
	}
	
	public static Contract getContract(ItemStack stack) {
		if (stack.getItem() instanceof Contract) {
			return (Contract) stack.getItem();
		}
		return null;
	}

	public boolean activate(ItemStack stack, EntityLivingBase targetEntity) {
		return false;
	}
}
