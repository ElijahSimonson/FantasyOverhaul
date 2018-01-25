package nz.castorgaming.fantasyoverhaul.objects.potions.effects;

import net.minecraft.entity.EntityLivingBase;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.util.classes.EntityUtil;

public class MortalCoil extends PotionBase {

	public MortalCoil(int color) {
		super(color);
		this.setIncurable();
	}

	@Override
	public boolean isReady(final int duration, final int amplifier) {
		return duration == 1;
	}

	@Override
	public void performEffect(final EntityLivingBase entity, final int amplifier) {
		EntityUtil.instantDeath(entity, null);
	}

}
