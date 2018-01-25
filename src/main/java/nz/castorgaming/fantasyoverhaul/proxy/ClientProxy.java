package nz.castorgaming.fantasyoverhaul.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.objects.entities.particles.ColoredSpell;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public EntityPlayer getPlayer(MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			return ctx.getServerHandler().playerEntity;
		}
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
	}

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}

	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}

	@Override
	public void showParticleEffect(World world, double x, double y, double z, double width, double height,
			SoundEffect sound, int color, ParticleEffect particle) {
		if (sound != SoundEffect.NONE) {
			world.playSound(null, new BlockPos(x, y, z), sound.event(), sound.category(), 0.5f,
					0.5f / ((float) world.rand.nextDouble() * 0.4f + 0.8f));
		}
		int effectCount = Math.min(MathHelper.ceiling_double_int(Math.max(width, 1.0) * 20), 300);
		for (int i = 0; i < effectCount; ++i) {
			if (particle == ParticleEffect.SPELL_COLORED) {
				ColoredSpell sparkle = new ColoredSpell(world, x + world.rand.nextDouble() * width * 2 - width,
						y + world.rand.nextDouble() * height, z + world.rand.nextFloat() * width * 2.0 - width, 0.0,
						0.0, 0.0);
				sparkle.shouldCollide(false);
				float red = (color >>> 16 & 255) / 256.0f;
				float green = (color >>> 8 & 255) / 256.0f;
				float blue = (color & 255) / 256.0f;
				sparkle.setRBGColorF(red, green, blue);
				Minecraft.getMinecraft().effectRenderer.addEffect(sparkle);
				continue;
			}
			world.spawnParticle(particle.particle(), x + world.rand.nextDouble() * width * 2.0 - width,
					y + world.rand.nextDouble() * height, z + world.rand.nextFloat() * width * 2.0 - width, 0.0, 0.0,
					0.0);
		}
	}
}
