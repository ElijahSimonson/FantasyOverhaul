package nz.castorgaming.fantasyoverhaul.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayerStorage;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedVillager.ExtVillagerStorage;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedVillager.ExtendVillager;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedVillager.IExtendVillager;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.IPlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampire;
import nz.castorgaming.fantasyoverhaul.capabilities.playerVampire.PlayerVampireStorage;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.IPlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.PlayerWerewolf;
import nz.castorgaming.fantasyoverhaul.capabilities.playerWerewolf.PlayerWerewolfStorage;

public class CapabilityInit {

	@CapabilityInject(IExtendVillager.class)
	public static Capability<IExtendVillager> EXTENDED_VILLAGER = null;

	@CapabilityInject(IExtendPlayer.class)
	public static Capability<IExtendPlayer> EXTENDED_PLAYER = null;

	@CapabilityInject(IPlayerVampire.class)
	public static Capability<IPlayerVampire> PLAYER_VAMPIRE = null;

	@CapabilityInject(IPlayerWerewolf.class)
	public static Capability<IPlayerWerewolf> PLAYER_WEREWOLF = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IExtendVillager.class, new ExtVillagerStorage(), ExtendVillager.class);
		CapabilityManager.INSTANCE.register(IExtendPlayer.class, new ExtendedPlayerStorage(), ExtendedPlayer.class);
		CapabilityManager.INSTANCE.register(IPlayerWerewolf.class, new PlayerWerewolfStorage(), PlayerWerewolf.class);
		CapabilityManager.INSTANCE.register(IPlayerVampire.class, new PlayerVampireStorage(), PlayerVampire.class);
	}

	public CapabilityInit() throws InstantiationException, IllegalAccessException {
	}

}
