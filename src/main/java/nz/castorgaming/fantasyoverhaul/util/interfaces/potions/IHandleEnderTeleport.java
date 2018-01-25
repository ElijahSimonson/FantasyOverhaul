package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandleEnderTeleport {

	PotionBase getPotion();

	void onEnderTeleport(World world, EntityLivingBase entity, EnderTeleportEvent event, int p1);

}
