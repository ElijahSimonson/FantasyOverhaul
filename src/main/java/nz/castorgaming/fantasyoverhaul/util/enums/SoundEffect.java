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

public enum SoundEffect {
	// No Sound
	NONE("", SoundCategory.MASTER),

	// Minecraft Sounds
	// Ambient
	RANDOM_ORB(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT), RANDOM_FIZZ(SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT), RANDOM_POP(SoundEvents.BLOCK_LAVA_POP,
			SoundCategory.AMBIENT), FIRE_FIRE(SoundEvents.BLOCK_FIRE_AMBIENT,
					SoundCategory.AMBIENT), FIREWORKS_BLAST1(SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.AMBIENT), RANDOM_EXPLODE(SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT),

	// Player
	WATER_SPLASH(SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.PLAYERS), DAMAGE_HIT(SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS), WATER_SWIM(SoundEvents.ENTITY_PLAYER_SWIM,
			SoundCategory.PLAYERS), RANDOM_BREATH(SoundEvents.ENTITY_PLAYER_BREATH, SoundCategory.PLAYERS), RANDOM_LEVELUP(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS),

	// Hostile
	MOB_GHAST_DEATH(SoundEvents.ENTITY_GHAST_DEATH, SoundCategory.HOSTILE), MOB_CREEPER_DEATH(SoundEvents.ENTITY_CREEPER_DEATH, SoundCategory.HOSTILE), MOB_BLAZE_DEATH(SoundEvents.ENTITY_BLAZE_DEATH,
			SoundCategory.HOSTILE), MOB_ENDERDRAGON_HIT(SoundEvents.ENTITY_ENDERDRAGON_HURT, SoundCategory.HOSTILE), MOB_ENDERMAN_IDLE(SoundEvents.ENTITY_ENDERMEN_AMBIENT,
					SoundCategory.HOSTILE), MOB_WITHER_DEATH(SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.HOSTILE), MOB_ENDERDRAGON_GROWL(SoundEvents.ENTITY_ENDERDRAGON_GROWL,
							SoundCategory.HOSTILE), MOB_HORSE_SKELETON_HIT(SoundEvents.ENTITY_SKELETON_HORSE_HURT, SoundCategory.HOSTILE), MOB_GHAST_FIREBALL(SoundEvents.ENTITY_GHAST_SHOOT,
									SoundCategory.HOSTILE), MOB_WITHER_SPAWN(SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE), MOB_HORSE_SKELETON_DEATH(SoundEvents.ENTITY_SKELETON_HORSE_DEATH,
											SoundCategory.HOSTILE), MOB_SILVERFISH_KILL(SoundEvents.ENTITY_SILVERFISH_DEATH, SoundCategory.HOSTILE), MOB_ZOMBIE_INFECT(SoundEvents.ENTITY_ZOMBIE_INFECT,
													SoundCategory.HOSTILE), MOB_SLIME_BIG(SoundEvents.ENTITY_SLIME_HURT, SoundCategory.HOSTILE), MOB_SLIME_SMALL(SoundEvents.ENTITY_SMALL_SLIME_HURT,
															SoundCategory.HOSTILE), MOB_ZOMBIE_DEATH(SoundEvents.ENTITY_ZOMBIE_DEATH, SoundCategory.HOSTILE), MOB_ENDERMEN_PORTAL(
																	SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE), MOB_SPIDER_SAY(SoundEvents.ENTITY_SPIDER_AMBIENT,
																			SoundCategory.HOSTILE), MOB_ZOMBIE_SAY(SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE),

	// Neutral
	MOB_OCELOT_DEATH(SoundEvents.ENTITY_CAT_DEATH, SoundCategory.NEUTRAL), MOB_WOLF_DEATH(SoundEvents.ENTITY_WOLF_DEATH, SoundCategory.NEUTRAL),

	// Block
	DIG_CLOTH(SoundEvents.BLOCK_CLOTH_BREAK, SoundCategory.BLOCKS), FIRE_IGNITE(SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS),

	// Music
	NOTE_SNARE(SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.MUSIC), NOTE_HARP(SoundEvents.BLOCK_NOTE_HARP, SoundCategory.MUSIC), NOTE_PLING(SoundEvents.BLOCK_NOTE_PLING, SoundCategory.MUSIC),

	// fantasyoverhaul Sounds
	// Ambient
	fantasyoverhaul_RANDOM_SWORD_DRAW("fantasyoverhaul:random.sworddraw", SoundCategory.AMBIENT), fantasyoverhaul_RANDOM_SWORD_SHEATHE("fantasyoverhaul:random.swordsheathe",
			SoundCategory.AMBIENT), fantasyoverhaul_RANDOM_POOF("fantasyoverhaul:random.poof", SoundCategory.AMBIENT), RANDOM_SPLASH("fantasyoverhaul:random.splash", SoundCategory.AMBIENT),

	// Player
	fantasyoverhaul_RANDOM_HYPNOSIS("fantasyoverhaul:random.hypnosis", SoundCategory.PLAYERS), fantasyoverhaul_RANDOM_DRINK("fantasyoverhaul:random.drink", SoundCategory.PLAYERS),

	// Hostile
	fantasyoverhaul_MOB_BABA_DEATH("fantasyoverhaul:mob.baba.baba_death", SoundCategory.HOSTILE), fantasyoverhaul_MOB_BABA_LIVING("fantasyoverhaul:mob.baba.baba_living",
			SoundCategory.HOSTILE), fantasyoverhaul_MOB_SPECTRE_SPECTRE_HIT("fantasyoverhaul:mob.spectre.spectre_hit", SoundCategory.HOSTILE), fantasyoverhaul_MOB_SPECTRE_SPECTRE_SAY(
					"fantasyoverhaul:mob.spectre.spectre_say",
					SoundCategory.HOSTILE), fantasyoverhaul_MOB_IMP_LAUGH("fantasyoverhaul:mob.imp.laugh", SoundCategory.HOSTILE), fantasyoverhaul_MOB_WOLFMAN_HOWL("fantasyoverhaul:mob.wolfman.howl",
							SoundCategory.HOSTILE), fantasyoverhaul_MOB_WOLFMAN_EAT("fantasyoverhaul:mob.wolfman.eat", SoundCategory.HOSTILE), fantasyoverhaul_MOB_WOLFMAN_LORD(
									"fantasyoverhaul:mob.wolfman.lord", SoundCategory.HOSTILE), fantasyoverhaul_RANDOM_MANTRAP("fantasyoverhaul:random.mantrap",
											SoundCategory.HOSTILE), fantasyoverhaul_MOB_WOLFMAN_TALK("fantasyoverhaul:mob.wolfman.say", SoundCategory.HOSTILE), fantasyoverhaul_MOB_REFLECTION_SPEECH(
													"fantasyoverhaul:mob.reflection.speech", SoundCategory.HOSTILE), fantasyoverhaul_MOB_REFLECTION_HURT("fantasyoverhaul:mob.reflection.hit",
															SoundCategory.HOSTILE), fantasyoverhaul_MOB_REFLECTION_DEATH("fantasyoverhaul:mob.reflection.death",
																	SoundCategory.HOSTILE), RANDOM_BOW("entity.arrow.shoot", SoundCategory.HOSTILE),

	// Neutral
	fantasyoverhaul_RANDOM_LOVED("fantasyoverhaul:random.loved", SoundCategory.NEUTRAL),

	// Block
	fantasyoverhaul_RANDOM_CHALK("fantasyoverhaul:random.chalk", SoundCategory.BLOCKS), fantasyoverhaul_RANDOM_CLICK("fantasyoverhaul:random.click",
			SoundCategory.BLOCKS), fantasyoverhaul_RANDOM_WINDUP("fantasyoverhaul:random.wind_up",
					SoundCategory.BLOCKS), fantasyoverhaul_RANDOM_HORN("fantasyoverhaul:random.horn", SoundCategory.BLOCKS),

	// Master
	fantasyoverhaul_MOB_LILITH_TALK("fantasyoverhaul:mob.lilith.say", SoundCategory.MASTER), fantasyoverhaul_RANDOM_THEYCOME("fantasyoverhaul:random.theycome", SoundCategory.MASTER);

	// final String sound;
	private final SoundEvent sound;
	final SoundCategory category;

	private SoundEffect(String sound, SoundCategory category) {
		this(new ResourceLocation(sound), category);
	}

	private SoundEffect(ResourceLocation sound, SoundCategory category) {
		this(new SoundEvent(sound), category);
	}

	private SoundEffect(SoundEvent sound, SoundCategory category) {
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
			world.playSound(null, player.getPosition(), this.getSound(), this.category, volume, 0.4f / ((float) world.rand.nextDouble() * 0.4f + 0.8f));
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
		if (this != NONE) {
			Reference.PACKET_HANDLER.sendTo(new PacketSound(this, player, volume, pitch), player);
		}
	}

	public SoundEvent getSound() {
		return sound;
	}
}
