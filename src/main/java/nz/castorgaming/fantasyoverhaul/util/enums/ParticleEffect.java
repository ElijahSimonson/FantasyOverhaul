package nz.castorgaming.fantasyoverhaul.util.enums;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.Coord;
import nz.castorgaming.fantasyoverhaul.util.classes.TargetPointUtil;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketParticles;

public enum ParticleEffect {
	HUGE_EXPLOSION("hugeexplosion"), LARGE_EXPLODE("largeexplode"), WATER_BUBBLE("bubble"), SUSPENDED(
			"suspended"), DEPTH_SUSPEND("depthsuspend"), TOWN_AURA("townaura"), CRIT("crit"), MAGIC_CRIT(
					"magiccrit"), SMOKE("smoke"), MOB_SPELL("mobspell"), SPELL("spell"), INSTANT_SPELL(
							"instantspell"), NOTE("note"), PORTAL("portal"), ENCHANTMENT_TABLE(
									"enchantmenttable"), EXPLODE("explode"), FLAME("flame"), LAVA(
											"lava"), FOOTSTEP("footstep"), SPLASH("splash"), LARGE_SMOKE(
													"largesmoke"), CLOUD("cloud"), REDDUST("reddust"), SNOWBALL_POOF(
															"snopwballpoof"), DRIP_WATER("dripwater"), DRIP_LAVA(
																	"driplava"), SNOW_SHOVEL("snowshovel"), SLIME(
																			"slime"), HEART("heart"), ICON_CRACK(
																					"iconcrack"), TILE_CRACK(
																							"tilecrack"), SPELL_COLORED(
																									"spell");

	final EnumParticleTypes particleId;

	private ParticleEffect(EnumParticleTypes particleId) {
		this.particleId = particleId;
	}

	private ParticleEffect(String particleId) {
		this.particleId = EnumParticleTypes.getByName(particleId);
	}

	public EnumParticleTypes particle() {
		return particleId;
	}

	public void send(SoundEffect sound, Entity entity, double width, double height, int range) {
		if (!entity.worldObj.isRemote) {
			Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(this, sound, entity, width, height),
					TargetPointUtil.from(entity, range));
		}
	}

	public void send(SoundEffect sound, Entity entity, double width, double height, int range, int color) {
		if (!entity.worldObj.isRemote) {
			Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(this, sound, entity, width, height, color),
					TargetPointUtil.from(entity, range));
		}
	}

	public void send(SoundEffect sound, TileEntity tile, double width, double height, int range, int color) {
		if (!tile.getWorld().isRemote) {
			Reference.PACKET_HANDLER.sendToAllAround(
					new PacketParticles(this, sound, 0.5 + tile.getPos().getX(), 0.5 + tile.getPos().getY(),
							0.5 + tile.getPos().getZ(), width, height, color),
					TargetPointUtil.from(tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(),
							tile.getPos().getZ(), range));
		}
	}

	public void send(SoundEffect sound, World world, BlockPos pos, double width, double height, int range) {
		this.send(sound, world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, width, height, range);
	}

	public void send(SoundEffect sound, World world, Coord center, double width, double height, int range) {
		this.send(sound, world, center.x + 0.5, center.y, center.z + 0.5, width, height, range);
	}

	public void send(SoundEffect sound, World world, double x, double y, double z, double width, double height,
			int range) {
		this.send(sound, world, x, y, z, width, height, range);
	}

	public void send(SoundEffect sound, World world, double x, double y, double z, double width, double height,
			int range, int color) {
		if (!world.isRemote) {
			Reference.PACKET_HANDLER.sendToAllAround(new PacketParticles(this, sound, x, y, z, width, height, color),
					TargetPointUtil.from(world, x, y, z, range));
		}
	}

	@Override
	public String toString() {
		return particleId.getParticleName();
	}
}
