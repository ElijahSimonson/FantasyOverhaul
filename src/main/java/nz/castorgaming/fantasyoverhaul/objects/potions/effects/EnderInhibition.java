package nz.castorgaming.fantasyoverhaul.objects.potions.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleEnderTeleport;

public class EnderInhibition extends PotionBase implements IHandleEnderTeleport {

	public EnderInhibition(int color) {
		super(true, color);
	}

	public static boolean isActive(Entity entity, int amplifier) {
		if (entity != null && entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			return living.isPotionActive(Potions.ENDER_INHIBITION)
					&& living.getActivePotionEffect(Potions.ENDER_INHIBITION).getAmplifier() >= amplifier;
		}
		return false;
	}

	@Override
	public void onEnderTeleport(World world, EntityLivingBase entity, EnderTeleportEvent event, int p1) {
		event.setCanceled(true);
	}

}
