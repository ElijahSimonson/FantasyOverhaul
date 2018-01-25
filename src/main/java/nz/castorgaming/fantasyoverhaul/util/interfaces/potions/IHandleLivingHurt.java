package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandleLivingHurt {

	PotionBase getPotion();

	void onLivingHurt(World world, EntityLivingBase entityLivingBase, LivingHurtEvent event, int p1);

	boolean handleAllHurtEvents();

}
