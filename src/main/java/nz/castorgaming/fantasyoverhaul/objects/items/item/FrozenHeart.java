package nz.castorgaming.fantasyoverhaul.objects.items.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Drinkable;
import nz.castorgaming.fantasyoverhaul.powers.playereffect.PlayerEffect;

public class FrozenHeart extends Drinkable{

	public FrozenHeart(String name, EnumAction useAction, PotionEffect[] effectsIn) {
		super(name, useAction, effectsIn);
	}
	
	@Override
	public ItemStack onDrunk(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			PlayerEffect.onDeath(player);
		}
		return super.onDrunk(stack, world, player);
	}

}
