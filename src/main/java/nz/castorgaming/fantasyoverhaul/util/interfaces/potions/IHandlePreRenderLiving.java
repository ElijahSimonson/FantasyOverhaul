package nz.castorgaming.fantasyoverhaul.util.interfaces.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public interface IHandlePreRenderLiving {

	PotionBase getPotion();

	@SideOnly(Side.CLIENT)
	void onLivingRender(World world, EntityLivingBase entity, RenderLivingEvent.Pre event, int p1);

}
