package nz.castorgaming.fantasyoverhaul.proxy;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class CommonProxy {

	public static Configuration config;

	public void preInit(FMLPreInitializationEvent e) {
		Reference.PACKET_HANDLER.registerMessages(Reference.MODID);

		File directory = e.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "fantasyOverhaul.cfg"));
		// Config.readConfig();

		CapabilityInit.register();
	}

	public void init(FMLInitializationEvent e) {

	}

	public void postInit(FMLPostInitializationEvent e) {
		if (config.hasChanged()) {
			config.save();
		}
	}

	public void registerItemRenderer(Item item, int i, String string) {
	}

	public void generateParticle() {
	}

	public void showParticleEffect(World world, double x, double y, double z, double width, double height,
			SoundEffect sound, int color, ParticleEffect particle) {
	}

	public EntityPlayer getPlayer(MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			return ctx.getServerHandler().playerEntity;
		}
		return null;
	}

}
