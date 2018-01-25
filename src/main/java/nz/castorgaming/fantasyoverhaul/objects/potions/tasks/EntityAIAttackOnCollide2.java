package nz.castorgaming.fantasyoverhaul.objects.potions.tasks;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class EntityAIAttackOnCollide2 extends EntityAIAttackMelee {

	private Class<?> clazz;

	public EntityAIAttackOnCollide2(EntityCreature creature, Class<?> clazz, double speedIn, boolean useLongMemory) {
		super(creature, speedIn, useLongMemory);
		this.clazz = clazz;
	}

	public boolean appliesToClass(Class<?> victimClass) {
		return victimClass == clazz;
	}
}
