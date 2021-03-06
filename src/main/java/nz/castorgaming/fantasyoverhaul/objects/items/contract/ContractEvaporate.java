package nz.castorgaming.fantasyoverhaul.objects.items.contract;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import nz.castorgaming.fantasyoverhaul.init.PlayerEffectInit;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Contract;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;

public class ContractEvaporate extends Contract {

	public ContractEvaporate(String name) {
		super(name);
	}

	@Override
	public boolean activate(ItemStack stack, EntityLivingBase targetEntity) {
		if (targetEntity instanceof EntityPlayer) {
			PlayerEffectInit.IMP_EVAPORATION.applyTo((EntityPlayer) targetEntity, TimeUtilities.minsToTicks(10));
			return true;
		}
		return false;
	}

}
