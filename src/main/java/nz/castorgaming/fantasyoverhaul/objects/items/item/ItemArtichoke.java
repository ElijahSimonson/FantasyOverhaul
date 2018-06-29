package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Edible;

public class ItemArtichoke extends Edible {

	public ItemArtichoke(String name, int heal, float sat, boolean wolfFav) {
		super(name, heal, sat, wolfFav);
	}

	@Override
	public ItemStack onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		int foodLevel = player.getFoodStats().getFoodLevel();
		super.onFoodEaten(stack, world, player);
		int healed = player.getFoodStats().getFoodLevel() - foodLevel;
		player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 3 * healed * 20, 2));
		return stack;
	}

}
