package nz.castorgaming.fantasyoverhaul.objects.entities.particles;

import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.world.World;

public class ColoredSpell extends ParticleSpell {

	public ColoredSpell(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i1229_8_,
			double ySpeed, double p_i1229_12_) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, p_i1229_8_, ySpeed, p_i1229_12_);
	}

	public void shouldCollide(boolean shouldCollide) {
		this.canCollide = shouldCollide;
	}

}
