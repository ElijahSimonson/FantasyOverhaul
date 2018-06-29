package nz.castorgaming.fantasyoverhaul.objects.items.item;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Drinkable;

public class PurifiedMilk extends Drinkable {

	public PurifiedMilk(String name, PotionEffect... effectsIn) {
		super(name, effectsIn);
	}

	@Override
	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		super.onDrunk(stack, world, player);
		if (!world.isRemote && world.rand.nextInt(2) == 0) {
			Collection<PotionEffect> effects = player.getActivePotionEffects();
			if (effects != null && !effects.isEmpty()) {
				PotionEffect[] effectArray = (PotionEffect[]) effects.toArray();
				int itemIndex = world.rand.nextInt(effects.size());
				PotionEffect effect = effectArray[itemIndex];
				player.removePotionEffect(effect.getPotion());
			}
		}
		return stack;
	}

}
