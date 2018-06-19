package nz.castorgaming.fantasyoverhaul.objects.items.drinkables;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Drinkable;

public class BloodLiliths extends Drinkable{

	public BloodLiliths(String name) {
		super(name);
	}
	
	@Override
	public void onDrunk(World world, EntityPlayer player, ItemStack stack) {
		if (!world.isRemote) {
			PlayerVampire vamp = PlayerVampire.get(player);
			int level = vamp.getVampireLevel();
			if (level == 10) {
				vamp.increaseBloodPower(1000);
			}else {
				vamp.increaseVampireLevel();
			}
		}
	}

}
