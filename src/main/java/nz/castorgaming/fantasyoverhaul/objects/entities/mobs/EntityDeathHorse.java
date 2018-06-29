package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;

public class EntityDeathHorse extends EntityHorse {

	public EntityDeathHorse(World worldIn) {
		super(worldIn);
		experienceValue = 0;
	}

}
