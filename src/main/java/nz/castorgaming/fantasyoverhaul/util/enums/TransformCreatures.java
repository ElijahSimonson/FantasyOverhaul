package nz.castorgaming.fantasyoverhaul.util.enums;

import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;

public enum TransformCreatures {
	NONE, WOLF, WOLFMAN, BAT, PLAYER, TOAD;

	public static int toInt(Enum e) {
		return e.ordinal();
	}

	public int toInt() {
		return this.ordinal();
	}

	public static TransformCreatures fromInt(int ordinal) {
		return TransformCreatures.values()[ordinal];
	}

	public static boolean isWolfForm(IExtendPlayer player) {
		return player.getCreatureType() == WOLF || player.getCreatureType() == WOLFMAN;
	}
}
