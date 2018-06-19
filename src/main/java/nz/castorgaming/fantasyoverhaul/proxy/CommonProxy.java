package nz.castorgaming.fantasyoverhaul.proxy;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import nz.castorgaming.fantasyoverhaul.FantasyOverhaul;
import nz.castorgaming.fantasyoverhaul.init.CapabilityInit;
import nz.castorgaming.fantasyoverhaul.util.Log;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.handlers.PacketHandler;

public class CommonProxy {

	public static Configuration config;

	public void preInit(FMLPreInitializationEvent e) {
		PacketHandler.registerMessages(Reference.MODID);

		File directory = e.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "fantasyOverhaul.cfg"));

		CapabilityInit.register();

		checkLoaded();
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

	public void generateParticle(final World worldObj, final double posX, final double posY, final double posZ, final float f, final float g, final float h, final int i, final float j) {
	}

	public void showParticleEffect(World world, double x, double y, double z, double width, double height, SoundEffect sound, int color, ParticleEffect particle) {
	}

	public EntityPlayer getPlayer(MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			return ctx.getServerHandler().playerEntity;
		}
		return null;
	}

	private void checkLoaded() {
		for (FantasyOverhaul.LoadedMod mod : FantasyOverhaul.LoadedMod.values()) {
			if (Loader.isModLoaded(mod.id)) {
				mod.setLoaded();
				Log.instance().debug(mod.id + " is detected as LOADED");
			}
			else {
				Log.instance().debug(mod.id + " is detected as UNLOADED");
			}
		}
	}

}
