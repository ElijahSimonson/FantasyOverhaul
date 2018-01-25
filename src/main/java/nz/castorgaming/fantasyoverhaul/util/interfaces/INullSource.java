package nz.castorgaming.fantasyoverhaul.util.interfaces;

import net.minecraft.world.World;

public interface INullSource {

	World getWorld();

	int getPosX();

	int getPosY();

	int getPosZ();

	float getRange();

	boolean isPowerInvalid();

}
