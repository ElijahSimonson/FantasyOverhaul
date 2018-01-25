package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandleLivingDeath {

	PotionBase getPotion();

	void onLivingDeath(World world, EntityLivingBase entityLivingBase, LivingDeathEvent event, int p1);

}
