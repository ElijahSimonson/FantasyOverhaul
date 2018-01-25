package nz.castorgaming.fantasyoverhaul.objects.items.tools;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jcraft.jorbis.Block;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.objects.items.ToolSword;

public class HuntsmanSpear extends ToolSword {

	protected static final UUID UUID_KB, UUID_AD;
	private static final float BONUS_DAMAGE = 1.0f;
	static {
		UUID_KB = UUID.fromString("032f4b80-ad10-11e3-a5e2-0800200c9a66");
		UUID_AD = UUID.fromString("8519ee53-9c5d-421e-affe-94cee3b7e215");

	}
	private float effectiveWeaponDamage;

	private ToolMaterial effectiveToolMaterial;

	public HuntsmanSpear(String itemName) {
		super(itemName, ToolMaterial.WOOD);
		effectiveToolMaterial = ToolMaterial.DIAMOND;
		effectiveWeaponDamage = 4.0f + effectiveToolMaterial.getDamageVsEntity() + BONUS_DAMAGE;
		setMaxDamage(effectiveToolMaterial.getMaxUses());
	}

	public boolean canHarvestBlock(Block block, ItemStack stack) {
		return false;
	}

	@Override
	public float getDamageVsEntity() {
		return effectiveToolMaterial.getDamageVsEntity();
	}

	public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
		Multimap<String, AttributeModifier> multimap = HashMultimap.create();
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(),
				new AttributeModifier(HuntsmanSpear.UUID_AD, "Weapon Modifier", effectiveWeaponDamage, 0));
		multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getAttributeUnlocalizedName(),
				new AttributeModifier(HuntsmanSpear.UUID_KB, "Knockback Resist", 1.0, 0));
		return multimap;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.EPIC;
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return true;
	}
}
