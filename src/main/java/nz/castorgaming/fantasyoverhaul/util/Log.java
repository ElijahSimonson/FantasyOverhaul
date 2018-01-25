package nz.castorgaming.fantasyoverhaul.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.FMLCommonHandler;
import nz.castorgaming.fantasyoverhaul.util.configuration.ModConfig;

public class Log {

	static final Log INSTANCE;
	final Logger logger;

	public static Log instance() {
		return Log.INSTANCE;
	}

	Log() {
		logger = LogManager.getLogger(getModPrefix() + FMLCommonHandler.instance().getEffectiveSide());
	}

	static String getModPrefix() {
		return "fantasyoverhaul: ";
	}

	public void warning(String msg) {
		logger.log(Level.WARN, getModPrefix() + msg);
	}

	public void warning(Throwable exception, String msg) {
		logger.log(Level.WARN, getModPrefix() + msg);
		exception.printStackTrace();
	}

	public void debug(String msg) {
		if (ModConfig.instance().isDebugging) {
			logger.log(Level.INFO, getModPrefix() + msg);
		}
	}
	/*
	 * public void traceRite(String msg) { if (ModConfig.instance().traceRites()) {
	 * logger.log(Level.INFO, getModPrefix() + msg); } }
	 */

	static {
		INSTANCE = new Log();
	}
}
