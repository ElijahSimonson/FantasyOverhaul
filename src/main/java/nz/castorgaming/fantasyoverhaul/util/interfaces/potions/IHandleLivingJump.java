package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandleLivingJump {

	PotionBase getPotion();

	void onLivingJump(World world, EntityLivingBase entityLivingBase, LivingEvent.LivingJumpEvent event, int p1);

}
