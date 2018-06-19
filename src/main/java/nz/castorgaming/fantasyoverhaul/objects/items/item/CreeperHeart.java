package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Drinkable;
import nz.castorgaming.fantasyoverhaul.util.classes.Config;

public class CreeperHeart extends Drinkable{

	public CreeperHeart(String name, EnumAction useAction, PotionEffect[] effectsIn) {
		super(name, useAction, effectsIn);
	}
	
	@Override
	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (Config.instance().allowExplodingCreeperHearts) {
				world.createExplosion(player, player.posX, player.posY, player.posZ, 3.0f, true);
			}else {
				world.createExplosion(player, player.posX, player.posY, player.posZ, 1.0f, true);
			}
		}
		return super.onDrunk(stack, world, player);
	}

}
