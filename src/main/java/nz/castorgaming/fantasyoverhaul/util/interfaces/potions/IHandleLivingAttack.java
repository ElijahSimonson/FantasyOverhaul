package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandleLivingAttack {

	PotionBase getPotion();

	void onLivingAttack(World world, EntityLivingBase entity, LivingAttackEvent event, int p1);

}
