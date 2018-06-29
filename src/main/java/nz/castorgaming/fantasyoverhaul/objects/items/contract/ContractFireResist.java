package nz.castorgaming.fantasyoverhaul.objects.items.contract;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Contract;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;

public class ContractFireResist extends Contract {

	public ContractFireResist(String name) {
		super(name);
	}

	@Override
	public boolean activate(ItemStack stack, EntityLivingBase targetEntity) {
		targetEntity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, TimeUtilities.minsToTicks(15)));
		return true;
	}

}
