package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandlePlayerDrops {

	PotionBase getPotion();

	void onPlayerDrops(World world, EntityPlayer player, PlayerDropsEvent event, int p1);

}
