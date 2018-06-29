package nz.castorgaming.fantasyoverhaul.objects.potions.effects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import nz.castorgaming.fantasyoverhaul.objects.potions.PotionBase;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.EntitySizeInfo;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingAttack;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingHurt;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingJump;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleLivingUpdate;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandlePreRenderLiving;
import nz.castorgaming.fantasyoverhaul.util.interfaces.potions.IHandleRenderLiving;
import nz.castorgaming.fantasyoverhaul.util.packets.PacketSyncEntitySize;

public class Resizing extends PotionBase implements IHandlePreRenderLiving, IHandleRenderLiving, IHandleLivingUpdate,
		IHandleLivingHurt, IHandleLivingJump, IHandleLivingAttack {

	private static Method methodEntitySetSize;
	private static Method methodZombieSetSize;
	private static Method methodZombieSetSize2;
	private static Method methodAgeableSetSize;
	private static Method methodAgeableSetSize2;

	public static float getDamageMultiplier(final PotionEffect amplifierA, final PotionEffect amplifierB) {
		final int sizeA = getSize(amplifierA);
		final int sizeB = getSize(amplifierB);
		final float sizeDiff = sizeA / sizeB;
		return sizeDiff;
	}

	public static float getModifiedScaleFactor(final EntityLivingBase entity, final int amplifier) {
		final float currentHeight = entity.height;
		final EntitySizeInfo sizeInfo = new EntitySizeInfo(entity);
		final float ratio = currentHeight / sizeInfo.defaultHeight;
		final float factor = getScaleFactor(amplifier);
		final float scale = factor < 1.0f ? Math.max(ratio, factor) : Math.min(ratio, factor);
		return scale;
	}

	public static float getScaleFactor(final int amplifier) {
		switch (amplifier) {
		default: {
			return 0.25f;
		}
		case 1: {
			return 0.4f;
		}
		case 2: {
			return 2.0f;
		}
		case 3: {
			return 3.0f;
		}
		}
	}

	private static int getSize(final PotionEffect amplifier) {
		if (amplifier == null) {
			return 3;
		}
		switch (amplifier.getAmplifier()) {
		default: {
			return 3;
		}
		case 0: {
			return 1;
		}
		case 1: {
			return 2;
		}
		case 2: {
			return 4;
		}
		case 3: {
			return 5;
		}
		}
	}

	public static void setEntitySize(final Entity entity, final float width, final float height) {
		try {
			if (entity instanceof EntityZombie) {
				if (Resizing.methodZombieSetSize == null) {
					Resizing.methodZombieSetSize = ReflectionHelper.findMethod((Class) EntityZombie.class,
							(Object) entity, new String[] { "setSize", "setSize", "a" },
							new Class[] { Float.TYPE, Float.TYPE });
				}
				if (Resizing.methodZombieSetSize2 == null) {
					Resizing.methodZombieSetSize2 = ReflectionHelper.findMethod((Class) EntityZombie.class,
							(Object) entity, new String[] { "func_146069_a", "a" }, new Class[] { Float.TYPE });
				}
				Resizing.methodZombieSetSize.invoke(entity, width, height);
				Resizing.methodZombieSetSize2.invoke(entity, 1.0f);
			} else if (entity instanceof EntityAgeable) {
				if (Resizing.methodAgeableSetSize == null) {
					Resizing.methodAgeableSetSize = ReflectionHelper.findMethod((Class) EntityAgeable.class,
							(Object) entity, new String[] { "setSize", "setSize", "a" },
							new Class[] { Float.TYPE, Float.TYPE });
				}
				if (Resizing.methodAgeableSetSize2 == null) {
					Resizing.methodAgeableSetSize2 = ReflectionHelper.findMethod((Class) EntityAgeable.class,
							(Object) entity, new String[] { "setScale", "setScale", "a" }, new Class[] { Float.TYPE });
				}
				Resizing.methodAgeableSetSize.invoke(entity, width, height);
				Resizing.methodAgeableSetSize2.invoke(entity, 1.0f);
			} else {
				if (Resizing.methodEntitySetSize == null) {
					Resizing.methodEntitySetSize = ReflectionHelper.findMethod((Class) Entity.class, (Object) entity,
							new String[] { "setSize", "setSize", "a" }, new Class[] { Float.TYPE, Float.TYPE });
				}
				Resizing.methodEntitySetSize.invoke(entity, width, height);
			}
		} catch (IllegalAccessException ex) {
		} catch (IllegalArgumentException ex2) {
		} catch (InvocationTargetException ex3) {
		}
	}

	public Resizing(int liquidColorIn) {
		super(false, liquidColorIn);
	}

	@Override
	public boolean handleAllHurtEvents() {
		return true;
	}

	@Override
	public void onLivingAttack(final World world, final EntityLivingBase entity, final LivingAttackEvent event,
			final int amplifier) {
		if (Reference.modHooks.isAM2Present && !world.isRemote && event.getSource() == DamageSource.inWall
				&& amplifier <= 1 && entity instanceof EntityPlayer
				&& !event.getEntity().worldObj
						.getBlockState(new BlockPos(MathHelper.floor_double(event.getEntity().posX),
								MathHelper.floor_double(event.getEntity().posY),
								MathHelper.floor_double(event.getEntity().posZ)))
						.isNormalCube()) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onLivingHurt(final World world, final EntityLivingBase entity, final LivingHurtEvent event,
			final int amplifier) {
		if (!world.isRemote) {
			final PotionEffect effectDefender = entity.getActivePotionEffect(this);
			final boolean isDefenderShrunken = effectDefender != null;
			final DamageSource source = event.getSource();
			if (source.getDamageType() == "mob" || source.getDamageType() == "player") {
				if (source.getEntity() != null && source.getEntity() instanceof EntityLivingBase) {
					final EntityLivingBase attacker = (EntityLivingBase) source.getEntity();
					final PotionEffect effectAttacker = attacker.getActivePotionEffect(this);
					if (isDefenderShrunken || effectAttacker != null) {
						final float scale = getDamageMultiplier(effectAttacker, effectDefender);
						event.setAmount(event.getAmount() * Math.max(Math.min(scale, 3.0f), 0.5f));
					}
				}
			} else if (source == DamageSource.fall && isDefenderShrunken
					&& getScaleFactor(effectDefender.getAmplifier()) > event.getAmount()) {
				event.setCanceled(true);
			}
		}
	}

	@Override
	public void onLivingJump(final World world, final EntityLivingBase entity, final LivingEvent.LivingJumpEvent event,
			final int amplifier) {
		final float scale = getScaleFactor(amplifier);
		if (scale > 1.0f) {
			final EntityLivingBase entityLiving = event.getEntityLiving();
			entityLiving.motionY *= scale * 0.5 + 0.5;
		} else {
			final EntityLivingBase entityLiving2 = event.getEntityLiving();
			entityLiving2.motionY *= Math.max(scale, 0.5) * 1.5;
		}
	}

	@Override
	public void onLivingRender(final World world, final EntityLivingBase entity, final RenderLivingEvent.Post event,
			final int amplifier) {
		GL11.glPopMatrix();
	}

	@Override
	public void onLivingRender(final World world, final EntityLivingBase entity, final RenderLivingEvent.Pre event,
			final int amplifier) {
		GL11.glPushMatrix();
		GL11.glTranslated(event.getX(), event.getY(), event.getZ());
		final float scale = getModifiedScaleFactor(entity, amplifier);
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslated(-event.getX(), -event.getY(), -event.getZ());
	}

	@Override
	public void onLivingUpdate(final World world, final EntityLivingBase entity,
			final LivingEvent.LivingUpdateEvent event, final int amplifier, final int duration) {
		final float reductionFactor = 0.03f * (event.getEntity().worldObj.isRemote ? 1 : 20);
		if (world.isRemote || entity.ticksExisted % 20 == 0) {
			final EntitySizeInfo sizeInfo = new EntitySizeInfo(entity);
			final float scale = getScaleFactor(amplifier);
			final float requiredHeight = sizeInfo.defaultHeight * scale;
			final float requiredWidth = sizeInfo.defaultWidth * scale;
			final float currentHeight = event.getEntityLiving().height;
			if (requiredHeight != currentHeight) {
				if (entity instanceof EntityPlayer) {
					final EntityPlayer player = (EntityPlayer) entity;
					if (!world.isRemote) {
						player.eyeHeight = currentHeight * 0.92f;
					}
				}
				entity.stepHeight = scale < 1.0f ? 0.0f : scale - 1.0f;
				if (scale < 1.0f) {
					setEntitySize(entity, Math.max(entity.width - reductionFactor, requiredWidth),
							Math.max(currentHeight - reductionFactor, requiredHeight));
				} else {
					setEntitySize(entity, Math.min(entity.width + reductionFactor, requiredWidth),
							Math.min(currentHeight + reductionFactor, requiredHeight));
				}
			}
		}
	}

	@Override
	public void removeAttributesModifiersFromEntity(final EntityLivingBase entity,
			final AbstractAttributeMap attributes, final int amplifier) {
		final EntitySizeInfo sizeInfo = new EntitySizeInfo(entity);
		setEntitySize(entity, sizeInfo.defaultWidth, sizeInfo.defaultHeight);
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			player.eyeHeight = sizeInfo.eyeHeight;
		}
		entity.stepHeight = sizeInfo.stepSize;
		Reference.PACKET_HANDLER.sendToAll(new PacketSyncEntitySize(entity));
		super.removeAttributesModifiersFromEntity(entity, attributes, amplifier);
	}

}
