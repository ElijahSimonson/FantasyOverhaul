package nz.castorgaming.fantasyoverhaul.objects.items.brew;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.ExtendedPlayer;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.objects.items.main.Brew;
import nz.castorgaming.fantasyoverhaul.util.classes.ShapeShift;
import nz.castorgaming.fantasyoverhaul.util.enums.ParticleEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.SoundEffect;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;

public class BrewRevealing extends Brew {

	public BrewRevealing(String name) {
		super(name);
	}

	@Override
	public BrewResult onImpact(World world, EntityLivingBase thrower, RayTraceResult rtr, boolean enhanced, double x,
			double y, double z, AxisAlignedBB bounds) {
		double R = enhanced ? 8.0 : 5.0;
		double RSQ = R * R;
		AxisAlignedBB aoe = bounds.expand(R, R, R);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aoe);
		if (entities != null && !entities.isEmpty()) {
			for (EntityLivingBase entity : entities) {
				double distSq = entity.getDistance(x, y, z);
				if (distSq <= RSQ) {
					double scale = 1.0 - Math.sqrt(distSq) / R;
					if (entity == rtr.entityHit) {
						scale = 1.0;
					}
					if (entity.isPotionActive(MobEffects.INVISIBILITY)) {
						entity.removeActivePotionEffect(MobEffects.INVISIBILITY);
					}
					if (entity instanceof EntityPlayerMP && entity.isInvisible()) {
						entity.setInvisible(false);
					}
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						ExtendedPlayer extPlayer = IExtendPlayer.get(player);
						if (extPlayer != null && extPlayer.getCreatureType() == TransformCreatures.PLAYER) {
							ParticleEffect.SMOKE.send(SoundEffect.RANDOM_POOF, player, 0.5, 2.0, 16);
							ShapeShift.INSTANCE.shiftTo(player, TransformCreatures.NONE);
						}
					}
					if (!(entity instanceof EntitySummonedUndead) || !((EntitySummonedUndead) entity).isObscured) {
						continue;
					}
					((EntitySummonedUndead) entity).setObscured(false);
				}
			}
		}
		return BrewResult.SHOW_EFFECT;
	}

}
