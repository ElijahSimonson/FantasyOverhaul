package nz.castorgaming.fantasyoverhaul.util.classes;

public class StatBoost {
	public final double jump;
	public final double leap;
	public final int health;
	public final float damage;
	public final float resistance;
	public final float speed;
	public int fall;
	public final float damageCap;
	public boolean flying;

	public StatBoost(final float damage) {
		this(0.0f, 0.0, 0.0, 0, damage, 0.0f, 0, 0.0f);
	}

	public StatBoost(final float speed, final double jump, final double leap, final int health, final float damage, final float resistance, final int fall, final float damageCap) {
		this.jump = jump;
		this.leap = leap;
		this.health = health;
		this.damage = damage;
		this.resistance = resistance;
		this.speed = speed;
		this.fall = fall;
		this.damageCap = damageCap;
	}

	public StatBoost setFlying(final boolean active) {
		this.flying = active;
		this.fall = -1;
		return this;
	}
}