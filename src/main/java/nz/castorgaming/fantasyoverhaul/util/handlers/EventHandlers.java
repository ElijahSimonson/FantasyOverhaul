package nz.castorgaming.fantasyoverhaul.util.handlers;

import com.google.common.eventbus.Subscribe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;

@EventBusSubscriber
public class EventHandlers {

	@Subscribe
	public static void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		for (Potion potion : InitArrays.POTIONS) {
			if (entity.isPotionActive(potion) && entity.getActivePotionEffect(potion).getDuration() == 0) {
				entity.removePotionEffect(potion);
			}
		}
		return;
	}
}
