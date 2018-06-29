package nz.castorgaming.fantasyoverhaul.util.handlers.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.init.Potions;
import nz.castorgaming.fantasyoverhaul.objects.blocks.tileBlocks.BlockBearTrap;
import nz.castorgaming.fantasyoverhaul.util.classes.EntitySizeInfo;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandlePreRenderLiving;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleRenderLiving;

@SideOnly(Side.CLIENT)
@EventBusSubscriber
public class PotionClientHooks {

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderLiving(final RenderLivingEvent.Pre event) {
		if (event.getEntity() != null && event.getEntity().worldObj != null && event.getEntity().worldObj.isRemote) {
			for (final IHandlePreRenderLiving handler : Potions.LIVING_PRERENDER_HANDLERS) {
				if (event.isCanceled()) {
					break;
				}
				if (!event.getEntity().isPotionActive(handler.getPotion())) {
					continue;
				}
				final PotionEffect effect = event.getEntity().getActivePotionEffect(handler.getPotion());
				handler.onLivingRender(event.getEntity().worldObj, event.getEntity(), event, effect.getAmplifier());
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderLiving(final RenderLivingEvent.Post event) {
		if (event.getEntity() != null && event.getEntity().worldObj != null && event.getEntity().worldObj.isRemote) {
			for (final IHandleRenderLiving handler : Potions.LIVING_RENDER_HANDLERS) {
				if (event.isCanceled()) {
					break;
				}
				if (!event.getEntity().isPotionActive(handler.getPotion())) {
					continue;
				}
				final PotionEffect effect = event.getEntity().getActivePotionEffect(handler.getPotion());
				handler.onLivingRender(event.getEntity().worldObj, event.getEntity(), event, effect.getAmplifier());
			}
		}
	}

	@SubscribeEvent
	public void onDrawBlockHighlight(final DrawBlockHighlightEvent event) {
		if (event != null && !event.isCanceled() && event.getPlayer() != null) {
			if (event.getPlayer().isPotionActive(Potions.RESIZING)
					|| !new EntitySizeInfo(event.getPlayer()).isDefault) {
				final double reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
				final RayTraceResult mop = event.getPlayer().rayTrace(reach, event.getPartialTicks());
				if (mop != null && !BlockBearTrap.checkForHiddenTrap(event.getPlayer(), mop)) {
					event.getContext().drawSelectionBox(event.getPlayer(), mop, 0, event.getPartialTicks());
				}
				event.setCanceled(true);
			} else if (BlockBearTrap.checkForHiddenTrap(event.getPlayer(), event.getTarget())) {
				event.setCanceled(true);
			}
		}
	}
}
