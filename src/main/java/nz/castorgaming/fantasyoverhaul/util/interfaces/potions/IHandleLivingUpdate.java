package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandleLivingUpdate {

	PotionBase getPotion();

	void onLivingUpdate(World world, EntityLivingBase entity, LivingEvent.LivingUpdateEvent event, int p1, int p2);

}
