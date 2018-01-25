package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandleHarvestDrops {

	PotionBase getPotion();

	void onHarvestDrops(World world, EntityPlayer entity, BlockEvent.HarvestDropsEvent event, int p1);

}
