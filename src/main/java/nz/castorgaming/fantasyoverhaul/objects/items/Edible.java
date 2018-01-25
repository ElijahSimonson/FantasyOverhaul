package nz.castorgaming.fantasyoverhaul.objects.items;

public class Edible extends ItemBase {

	private int healAmount;
	private int saturationModifier;

	public Edible(String name, int heal, int sat) {
		super(name);
		healAmount = heal;
		setSaturationModifier(sat);
	}

	public int getHealAmount() {
		return healAmount;
	}

	public void setHealAmount(int healAmount) {
		this.healAmount = healAmount;
	}

	public int getSaturationModifier() {
		return saturationModifier;
	}

	public void setSaturationModifier(int saturationModifier) {
		this.saturationModifier = saturationModifier;
	}

}
