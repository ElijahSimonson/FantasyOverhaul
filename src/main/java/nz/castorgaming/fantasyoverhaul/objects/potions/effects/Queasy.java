package nz.castorgaming.fantasyoverhaul.objects.potions.effects;

import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;

public class Queasy extends PotionBase {
	public Queasy(final int color) {
		super(true, color);
		setIncurable();
	}

}