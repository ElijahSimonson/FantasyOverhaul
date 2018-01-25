package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandleLivingSetAttackTarget {

	PotionBase getPotion();

	void onLivingSetAttackTarget(World world, EntityLiving entity, LivingSetAttackTargetEvent event, int p1);

}
