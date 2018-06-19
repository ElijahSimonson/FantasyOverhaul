package thaumcraft.api.casters;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusCore.FocusEffect;

public interface IFocusPartEffect extends IFocusPart {
	
	public default float getBaseCost() { return 0; }

	/**
	 * This method is triggered when the medium interacts with a target or block
	 */
	public default boolean onEffectTrigger(World world, RayTraceResult ray, Entity caster, @Nullable ItemStack casterStack, Entity mediumEntity, FocusEffect effect, float charge) { return true; }
	
	/**
	 * This method is triggered when the effect is first cast by the player. Intended to be used for FX or similar things.
	 */
	public default void onEffectCast(World world, Entity caster, @Nullable ItemStack casterStack, float charge) { } 
	
		
}
