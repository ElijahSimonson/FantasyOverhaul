package nz.castorgaming.fantasyoverhaul.objects.potions.effects;

import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public class KeepEffects extends PotionBase {

	public KeepEffects(int liquidColorIn) {
		super(false, liquidColorIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void postConstructInitialize() {
		this.setPermanent();
	}
}
