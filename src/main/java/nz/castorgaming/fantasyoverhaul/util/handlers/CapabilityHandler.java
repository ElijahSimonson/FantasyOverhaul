package nz.castorgaming.fantasyoverhaul.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayerProvider;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedVillager.ExtendedVillagerProvider;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampireProvider;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.PlayerWerewolfProvider;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;

@EventBusSubscriber
public class CapabilityHandler {

	public boolean canHaveAttributes(Entity entity) {
		if (entity instanceof EntityLivingBase) {
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void onEntityAddCapability(AttachCapabilitiesEvent<Entity> e) {
		if (canHaveAttributes(e.getObject())) {
			EntityLivingBase entity = (EntityLivingBase) e.getObject();

			if (entity instanceof EntityVillager) {
				e.addCapability(ExtendedVillagerProvider.NAME, new ExtendedVillagerProvider());
				entity.getCapability(CapabilityInit.EXTENDED_VILLAGER, null).setVillager((EntityVillager) entity);
			}

			if (entity instanceof EntityPlayer) {
				e.addCapability(ExtendedPlayerProvider.NAME, new ExtendedPlayerProvider());
				e.addCapability(PlayerVampireProvider.NAME, new PlayerVampireProvider());
				e.addCapability(PlayerWerewolfProvider.NAME, new PlayerWerewolfProvider());

				entity.getCapability(CapabilityInit.EXTENDED_PLAYER, null).setPlayer((EntityPlayer) entity);
				entity.getCapability(CapabilityInit.PLAYER_VAMPIRE, null).setPlayer((EntityPlayer) entity);
				entity.getCapability(CapabilityInit.PLAYER_WEREWOLF, null).setPlayer((EntityPlayer) entity);
			}

		}
	}

}
