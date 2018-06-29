package nz.castorgaming.fantasyoverhaul.util.enums;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketSound;

public class SoundEffect {

	public static final SoundEffect RANDOM_ORB = new AMBIENT(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
	public static final SoundEffect RANDOM_FIZZ = new AMBIENT(SoundEvents.BLOCK_FIRE_EXTINGUISH);
	public static final SoundEffect RANDOM_POP = new AMBIENT(SoundEvents.BLOCK_LAVA_POP);
	public static final SoundEffect FIRE_FIRE = new AMBIENT(SoundEvents.BLOCK_FIRE_AMBIENT);
	public static final SoundEffect FIREWORKS_BLAST1 = new AMBIENT(SoundEvents.ENTITY_FIREWORK_BLAST);
	public static final SoundEffect RANDOM_EXPLODE = new AMBIENT(SoundEvents.ENTITY_GENERIC_EXPLODE);
	public static final SoundEffect RANDOM_SWORD_DRAW = new AMBIENT("fantasyoverhaul:random.sworddraw");
	public static final SoundEffect RANDOM_SWORD_SHEATHE = new AMBIENT("fantasyoverhaul:random.swordsheathe");
	public static final SoundEffect RANDOM_POOF = new AMBIENT("fantasyoverhaul:random.poof");
	public static final SoundEffect RANDOM_SPLASH = new AMBIENT("fantasyoverhaul:random.splash");

	public static final SoundEffect WATER_SPLASH = new PLAYER(SoundEvents.ENTITY_PLAYER_SPLASH);
	public static final SoundEffect DAMAGE_HIT = new PLAYER(SoundEvents.ENTITY_PLAYER_HURT);
	public static final SoundEffect WATER_SWIM = new PLAYER(SoundEvents.ENTITY_PLAYER_SWIM);
	public static final SoundEffect RANDOM_BREATH = new PLAYER(SoundEvents.ENTITY_PLAYER_BREATH);
	public static final SoundEffect RANDOM_LEVELUP = new PLAYER(SoundEvents.ENTITY_PLAYER_LEVELUP);
	public static final SoundEffect RANDOM_HYPNOSIS = new PLAYER("fantasyoverhaul:random.hypnosis");
	public static final SoundEffect RANDOM_DRINK = new PLAYER("fantasyoverhaul:random.drink");

	public static final SoundEffect MOB_GHAST_DEATH = new HOSTILE(SoundEvents.ENTITY_GHAST_DEATH);
	public static final SoundEffect MOB_CREEPER_DEATH = new HOSTILE(SoundEvents.ENTITY_CREEPER_DEATH);
	public static final SoundEffect MOB_BLAZE_DEATH = new HOSTILE(SoundEvents.ENTITY_BLAZE_DEATH);
	public static final SoundEffect MOB_ENDERDRAGON_HIT = new HOSTILE(SoundEvents.ENTITY_ENDERDRAGON_HURT);
	public static final SoundEffect MOB_ENDERMAN_IDLE = new HOSTILE(SoundEvents.ENTITY_ENDERMEN_AMBIENT);
	public static final SoundEffect MOB_WITHER_DEATH = new HOSTILE(SoundEvents.ENTITY_WITHER_DEATH);
	public static final SoundEffect MOB_ENDERDRAGON_GROWL = new HOSTILE(SoundEvents.ENTITY_ENDERDRAGON_GROWL);
	public static final SoundEffect MOB_HORSE_SKELETON_HIT = new HOSTILE(SoundEvents.ENTITY_SKELETON_HORSE_HURT);
	public static final SoundEffect MOB_GHAST_FIREBALL = new HOSTILE(SoundEvents.ENTITY_GHAST_SHOOT);
	public static final SoundEffect MOB_WITHER_SPAWN = new HOSTILE(SoundEvents.ENTITY_WITHER_SPAWN);
	public static final SoundEffect MOB_HORSE_SKELETON_DEATH = new HOSTILE(SoundEvents.ENTITY_SKELETON_HORSE_DEATH);
	public static final SoundEffect MOB_SILVERFISH_KILL = new HOSTILE(SoundEvents.ENTITY_SILVERFISH_DEATH);
	public static final SoundEffect MOB_ZOMBIE_INFECT = new HOSTILE(SoundEvents.ENTITY_ZOMBIE_INFECT);
	public static final SoundEffect MOB_SLIME_BIG = new HOSTILE(SoundEvents.ENTITY_SLIME_HURT);
	public static final SoundEffect MOB_SLIME_SMALL = new HOSTILE(SoundEvents.ENTITY_SMALL_SLIME_HURT);
	public static final SoundEffect MOB_ZOMBIE_DEATH = new HOSTILE(SoundEvents.ENTITY_ZOMBIE_DEATH);
	public static final SoundEffect MOB_ENDERMEN_PORTAL = new HOSTILE(SoundEvents.ENTITY_ENDERMEN_TELEPORT);
	public static final SoundEffect MOB_SPIDER_SAY = new HOSTILE(SoundEvents.ENTITY_SPIDER_AMBIENT);
	public static final SoundEffect MOB_ZOMBIE_SAY = new HOSTILE(SoundEvents.ENTITY_ZOMBIE_AMBIENT);
	public static final SoundEffect GOLEM_THROW = new HOSTILE(SoundEvents.ENTITY_IRONGOLEM_ATTACK);
	public static final SoundEffect BLAZE_BREATH = new HOSTILE(SoundEvents.ENTITY_BLAZE_AMBIENT);
	public static final SoundEffect MOB_WITHER_HURT = new HOSTILE(SoundEvents.ENTITY_WITHER_HURT);
	public static final SoundEffect MOB_BABA_DEATH = new HOSTILE("fantasyoverhaul:mob.baba.baba_death");
	public static final SoundEffect MOB_BABA_LIVING = new HOSTILE("fantasyoverhaul:mob.baba.baba_living");
	public static final SoundEffect MOB_SPECTRE_SPECTRE_HIT = new HOSTILE("fantasyoverhaul:mob.spectre.spectre_hit");
	public static final SoundEffect MOB_SPECTRE_SPECTRE_SAY = new HOSTILE("fantasyoverhaul:mob.spectre.spectre_say");
	public static final SoundEffect MOB_IMP_LAUGH = new HOSTILE("fantasyoverhaul:mob.imp.laugh");
	public static final SoundEffect MOB_WOLFMAN_HOWL = new HOSTILE("fantasyoverhaul:mob.wolfman.howl");
	public static final SoundEffect MOB_WOLFMAN_EAT = new HOSTILE("fantasyoverhaul:mob.wolfman.eat");
	public static final SoundEffect MOB_WOLFMAN_LORD = new HOSTILE("fantasyoverhaul:mob.wolfman.lord");
	public static final SoundEffect RANDOM_MANTRAP = new HOSTILE("fantasyoverhaul:random.mantrap");
	public static final SoundEffect MOB_WOLFMAN_TALK = new HOSTILE("fantasyoverhaul:mob.wolfman.say");
	public static final SoundEffect MOB_REFLECTION_SPEECH = new HOSTILE("fantasyoverhaul:mob.reflection.speech");
	public static final SoundEffect MOB_REFLECTION_HURT = new HOSTILE("fantasyoverhaul:mob.reflection.hit");
	public static final SoundEffect MOB_REFLECTION_DEATH = new HOSTILE("fantasyoverhaul:mob.reflection.death");
	public static final SoundEffect RANDOM_BOW = new HOSTILE("entity.arrow.shoot");
	public static final SoundEffect NIGHTMARE_LIVE = new HOSTILE("fantasyoverhaul:mob.nightmare.live");
	public static final SoundEffect NIGHTMARE_HURT = new HOSTILE("fantasyoverhaul:mob.nightmare.live");
	public static final SoundEffect NIGHTMARE_DEATH = new HOSTILE("fantasyoverhaul:mob.nightmare.live");
	public static final SoundEffect MOB_LILITH_TALK = new HOSTILE("fantasyoverhaul:mob.lilith.say");
	public static final SoundEffect RANDOM_THEYCOME = new HOSTILE("fantasyoverhaul:random.theycome");

	public static final SoundEffect MOB_OCELOT_DEATH = new NEUTRAL(SoundEvents.ENTITY_CAT_DEATH);
	public static final SoundEffect MOB_WOLF_DEATH = new NEUTRAL(SoundEvents.ENTITY_WOLF_DEATH);
	public static final SoundEffect RANDOM_LOVED = new NEUTRAL("fantasyoverhaul:random.loved");

	public static final SoundEffect DIG_CLOTH = new BLOCK(SoundEvents.BLOCK_CLOTH_BREAK);
	public static final SoundEffect FIRE_IGNITE = new BLOCK(SoundEvents.ITEM_FLINTANDSTEEL_USE);
	public static final SoundEffect RANDOM_CHALK = new BLOCK("fantasyoverhaul:random.chalk");
	public static final SoundEffect RANDOM_CLICK = new BLOCK("fantasyoverhaul:random.click");
	public static final SoundEffect RANDOM_WINDUP = new BLOCK("fantasyoverhaul:random.wind_up");
	public static final SoundEffect RANDOM_HORN = new BLOCK("fantasyoverhaul:random.horn");;
	public static final SoundEffect NOTE_SNARE = new MUSIC(SoundEvents.BLOCK_NOTE_SNARE);
	public static final SoundEffect NOTE_HARP = new MUSIC(SoundEvents.BLOCK_NOTE_HARP);
	public static final SoundEffect NOTE_PLING = new MUSIC(SoundEvents.BLOCK_NOTE_PLING);

	public static final SoundEffect NONE = new MASTER("");

	private final SoundEvent sound;
	private final SoundCategory category;

	public SoundEffect(SoundEvent sound, SoundCategory category) {
		this.sound = sound;
		this.category = category;
	}

	@Override
	public String toString() {
		return this.getSound().getSoundName().toString();
	}

	public SoundCategory category() {
		return this.category;
	}

	public SoundEvent event() {
		return this.getSound();
	}

	public void playAtPlayer(World world, EntityPlayer player) {
		this.playAtPlayer(world, player, 0.5f);
	}

	public void playAtPlayer(World world, EntityPlayer player, float volume) {
		if (!world.isRemote) {
			world.playSound(null, player.getPosition(), this.getSound(), this.category, volume,
					0.4f / ((float) world.rand.nextDouble() * 0.4f + 0.8f));
		}
	}

	public void playAtPlayer(World world, EntityPlayer player, float volume, float pitch) {
		if (!world.isRemote) {
			world.playSound(null, player.getPosition(), this.getSound(), this.category, volume, pitch);
		}
	}

	public void playAt(EntityLiving entity) {
		this.playAt(entity, 0.5f);
	}

	public void playAt(EntityLiving entity, float volume) {
		this.playAt(entity, volume, 0.4f / ((float) entity.worldObj.rand.nextDouble() * 0.4f + 0.8f));
	}

	public void playAt(EntityLiving entity, float volume, float pitch) {
		if (!entity.worldObj.isRemote) {
			entity.playSound(this.getSound(), volume, pitch);
		}
	}

	public void playAt(TileEntity tile) {
		this.playAt(tile, 0.5f);
	}

	public void playAt(TileEntity tile, float volume) {
		this.playAt(tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), volume);
	}

	public void playAt(World world, double x, double y, double z) {
		this.playAt(world, x, y, z, 0.5f);
	}

	public void playAt(World world, double x, double y, double z, float volume) {
		this.playAt(world, x, y, z, volume, 0.4f / ((float) world.rand.nextDouble() * 0.4f + 0.8f));
	}

	public void playAt(World world, double x, double y, double z, float volume, float pitch) {
		if (!world.isRemote) {
			world.playSound(null, new BlockPos(x, y, z), this.getSound(), this.category, volume, pitch);
		}
	}

	public void playOnlyTo(EntityPlayer player) {
		this.playOnlyTo(player, -1.0f, -1.0f);
	}

	public void playOnlyTo(EntityPlayer player, float volume, float pitch) {
		if (this != SoundEffect.MASTER.NONE) {
			Reference.PACKET_HANDLER.sendTo(new PacketSound(this, player, volume, pitch), player);
		}
	}

	public SoundEvent getSound() {
		return sound;
	}

	static class AMBIENT extends SoundEffect {

		private AMBIENT(SoundEvent event) {
			super(event, SoundCategory.AMBIENT);
		}

		private AMBIENT(String event) {
			this(new SoundEvent(new ResourceLocation(event)));
		}
	}

	static class PLAYER extends SoundEffect {

		protected PLAYER(SoundEvent event) {
			super(event, SoundCategory.PLAYERS);
		}

		private PLAYER(String event) {
			this(new SoundEvent(new ResourceLocation(event)));
		}

	}

	static class HOSTILE extends SoundEffect {

		private HOSTILE(SoundEvent event) {
			super(event, SoundCategory.HOSTILE);
		}

		private HOSTILE(String event) {
			this(new SoundEvent(new ResourceLocation(event)));
		}

	}

	static class NEUTRAL extends SoundEffect {

		protected NEUTRAL(SoundEvent event) {
			super(event, SoundCategory.NEUTRAL);
		}

		private NEUTRAL(String event) {
			this(new SoundEvent(new ResourceLocation(event)));
		}
	}

	static class BLOCK extends SoundEffect {

		private BLOCK(SoundEvent event) {
			super(event, SoundCategory.BLOCKS);
		}

		private BLOCK(String event) {
			this(new SoundEvent(new ResourceLocation(event)));
		}

	}

	static class MUSIC extends SoundEffect {

		private MUSIC(SoundEvent event) {
			super(event, SoundCategory.MUSIC);
		}

		private MUSIC(String event) {
			this(new SoundEvent(new ResourceLocation(event)));
		}

	}

	static class MASTER extends SoundEffect {

		private MASTER(SoundEvent event) {
			super(event, SoundCategory.MASTER);
		}

		private MASTER(String event) {
			this(new SoundEvent(new ResourceLocation(event)));
		}
	}
}
