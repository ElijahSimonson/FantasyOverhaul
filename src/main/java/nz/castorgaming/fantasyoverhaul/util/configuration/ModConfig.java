package nz.castorgaming.fantasyoverhaul.util.configuration;

import net.minecraftforge.common.config.Configuration;

public class ModConfig {

	private static final ModConfig INSTANCE;
	public Configuration configuration;

	// Allow Integrations

	public boolean allowModIntegration;

	// Debugging

	public boolean isDebugging;

	public static ModConfig instance() {
		return ModConfig.INSTANCE;
	}

	public void sync() {
		// Allow Integrations

		allowModIntegration = configuration.getBoolean("AllowModIntegration", "integration", true,
				"Should Mod Integration be enabled?");

		// Debugging

		isDebugging = configuration.getBoolean("IsDebugging", "debugging", false, "Is Debug mode active");
	}

	static {
		INSTANCE = new ModConfig();
	}

}
