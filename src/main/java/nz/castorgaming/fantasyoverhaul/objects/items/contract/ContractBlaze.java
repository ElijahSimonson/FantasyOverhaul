package nz.castorgaming.fantasyoverhaul.objects.items.contract;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.item.ItemStack;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Contract;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;

public class ContractBlaze extends Contract {

	public ContractBlaze(String name) {
		super(name);
	}

	@Override
	public boolean activate(ItemStack stack, EntityLivingBase targetEntity) {
		EntityCreature blaze = Infusion.spawnCreature(targetEntity.worldObj, EntityBlaze.class, targetEntity, 1, 2,
				ParticleEffect.FLAME, SoundEffect.RANDOM_FIZZ);
		if (blaze != null) {
			blaze.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
			blaze.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
			return true;
		}
		return false;
	}

}
