package nz.castorgaming.fantasyoverhaul.objects.potions;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import nz.castorgaming.fantasyoverhaul.init.InitArrays;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleRenderLiving;

public class PotionBase extends Potion {

	private boolean inventoryTextHidden;
	private boolean incurable;
	private boolean permanent;
	private static Field fieldPotionIsBadEffect;

	public PotionBase(int color) {
		this(false, color);
	}

	protected PotionBase(boolean isBadEffectIn, int liquidColorIn) {
		super(isBadEffectIn, liquidColorIn);
		InitArrays.POTIONS.add(this);
	}

	public void postConstructInitialize() {
	}

	public static boolean isDebuff(Potion potion) {
		try {
			if (PotionBase.fieldPotionIsBadEffect == null) {
				PotionBase.fieldPotionIsBadEffect = ReflectionHelper.findField(Potion.class,
						new String[] { "isBadEffect", "isBadEffect", "K" });
			}

			boolean isDebuff = (Boolean) PotionBase.fieldPotionIsBadEffect.get(potion);

			return isDebuff;
		} catch (IllegalAccessException e) {
			return false;
		}
	}

	protected boolean isDebuff() {
		return false;
	}

	public PotionBase getPotion() {
		return this;
	}

	public static boolean isCurable(Potion potion) {
		return !(potion instanceof PotionBase) || ((PotionBase) potion).isCurable();
	}

	public static boolean isPermanent(Potion potion) {
		return potion instanceof PotionBase && ((PotionBase) potion).isPermanent();
	}

	public boolean isCurable() {
		return !incurable;
	}

	public boolean isPermanent() {
		return permanent;
	}

	protected void setIncurable() {
		incurable = true;
	}

	protected void setPermanent() {
		this.permanent = true;
	}

	protected void hideInventoryText() {
		this.inventoryTextHidden = true;
	}

	public void applyAttributeModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributes,
			int amplifier) {
		super.applyAttributesModifiersToEntity(entity, attributes, amplifier);
		if (this instanceof IHandleRenderLiving) {
			PotionEffect effect = entity.getActivePotionEffect(this);
			Reference.PACKET_HANDLER.sendToAll(new SPacketEntityEffect(entity.getEntityId(), effect));
		}
	}

	public void removeAttributeModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributes,
			int amplifier) {
		super.removeAttributesModifiersFromEntity(entity, attributes, amplifier);

		if (this instanceof IHandleRenderLiving) {
			Reference.PACKET_HANDLER.sendToAll(new SPacketRemoveEntityEffect(entity.getEntityId(), this));
		}
	}

	@Override
	public boolean shouldRenderInvText(PotionEffect effect) {
		return !this.inventoryTextHidden;
	}

	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		if (this.inventoryTextHidden) {
			mc.fontRendererObj.drawStringWithShadow((new ResourceLocation("Witchery:potion.unknown")).toString(),
					x + 10, y + 10, 16777215);
		}
	}

}
