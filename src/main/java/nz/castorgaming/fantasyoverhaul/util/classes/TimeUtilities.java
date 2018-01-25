package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.server.MinecraftServer;

public class TimeUtilities {

	public static int secsToTicks(int seconds) {
		return seconds * 20;
	}

	public static int minsToTicks(int mins) {
		return mins * 1200;
	}

	public static boolean secondsElapsed(int seconds, long ticksExisted) {
		return ticksExisted % secsToTicks(seconds) == 0L;
	}

	public static boolean tickssElapsed(int ticks, long ticksExisted) {
		return ticksExisted % ticks == 0L;
	}

	public static long ticksToSecs(int ticks) {
		return ticks / 20;
	}

	public static long minsToMillisecs(int mins) {
		return mins / 60000;
	}

	public static long secsToMillisecs(int secs) {
		return secs / 1000;
	}

	public static long getServerTimeInTicks() {
		return MinecraftServer.getCurrentTimeMillis() / 50L;
	}
}
