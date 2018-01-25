package nz.castorgaming.fantasyoverhaul.powers.infusions.creature;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;

public class CreaturePower extends IForgeRegistryEntry.Impl<CreaturePower> {

	private final String creaturePowerID;
	private final Class<? extends EntityLiving> creatureType;

	public static final RegistryNamespacedDefaultedByKey<ResourceLocation, CreaturePower> REGISTRY = (RegistryNamespacedDefaultedByKey<ResourceLocation, CreaturePower>) GameRegistry
			.findRegistry(CreaturePower.class);

	protected static final int DEFAULT_CHARGES_PER_SACRIFICE = 10;

	public CreaturePower(String creaturePowerID, Class<? extends EntityLiving> creatureType) {
		this.creaturePowerID = creaturePowerID;
		this.creatureType = creatureType;
		setRegistryName(creaturePowerID);
		InitArrays.CREATURE_POWER.add(this);
	}

	public String getCreaturePowerID() {
		return creaturePowerID;
	}

	public int activateCost(World world, EntityPlayer player, int elapsedTicks, RayTraceResult mop) {
		return 1;
	}

	public void onActivate(World world, EntityPlayer player, int elapsedTicks, RayTraceResult mop) {
	}

	public void onUpdate(World world, EntityPlayer player) {
	}

	public void onDamage(World world, EntityPlayer player, LivingHurtEvent event) {
	}

	public void onFalling(World world, EntityPlayer player, LivingFallEvent event) {
	}

	public static String getCreaturePowerID(EntityPlayer player) {
		NBTTagCompound tags = Infusion.getNBT(player);
		return tags.getString(Reference.BEAST_POWER_KEY);
	}

	public static void setCreaturePowerID(EntityPlayer playerEntity, String beastPower, int beastCharges) {
		NBTTagCompound tags = Infusion.getNBT(playerEntity);
		if (beastPower != null) {
			tags.setInteger(Reference.BEAST_POWER_CHARGES_KEY, beastCharges);
			tags.setString(Reference.BEAST_POWER_KEY, beastPower);
		}
		else {
			if (tags.hasKey(Reference.BEAST_POWER_KEY)) {
				tags.removeTag(Reference.BEAST_POWER_KEY);
			}
			if (tags.hasKey(Reference.BEAST_POWER_CHARGES_KEY)) {
				tags.removeTag(Reference.BEAST_POWER_CHARGES_KEY);
			}
		}
	}

	public static int getCreaturePowerCharges(EntityPlayer player) {
		NBTTagCompound tags = Infusion.getNBT(player);
		if (tags.hasKey(Reference.BEAST_POWER_KEY) && tags.hasKey(Reference.BEAST_POWER_CHARGES_KEY)) {
			return tags.getInteger(Reference.BEAST_POWER_CHARGES_KEY);
		}
		return 0;
	}

	public static void setCreaturePowerCharges(EntityPlayer player, int charges) {
		NBTTagCompound tags = Infusion.getNBT(player);
		tags.setInteger(Reference.BEAST_POWER_CHARGES_KEY, charges);
	}

	public int getChargesPerSacrifice() {
		return DEFAULT_CHARGES_PER_SACRIFICE;
	}

	public ResourceLocation getPowerBarIcon(World worldObj, EntityPlayer player) {
		return new ResourceLocation("clay_block");
	}

	public Class<? extends EntityLiving> getCreatureType() {
		return creatureType;
	}

	public static CreaturePower get(EntityLiving livingEnt) {
		for (CreaturePower power : CreaturePower.REGISTRY) {
			if (power.creatureType == livingEnt.getClass()) {
				return power;
			}
		}

		return null;
	}

	public static CreaturePower get(String creaturePowerID) {
		for (CreaturePower power : CreaturePower.REGISTRY) {
			if (power.creaturePowerID.equals(creaturePowerID)) {
				return power;
			}
		}

		return null;
	}

}
