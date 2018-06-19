package nz.castorgaming.fantasyoverhaul.objects.items.drinkables;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Drinkable;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;

public class BloodWarm extends Drinkable{

	public BloodWarm(String name) {
		super(name);
	}
	
	@Override
	public void onDrunk(World world, EntityPlayer player, ItemStack stack) {
		if (!world.isRemote) {
			PlayerVampire vamp = IPlayerVampire.get(player);
			if (vamp.isVampire()) {
				vamp.increaseBloodPower(500);
			}else {
				player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, TimeUtilities.secsToTicks(6)));
			}
		}
	}

}
