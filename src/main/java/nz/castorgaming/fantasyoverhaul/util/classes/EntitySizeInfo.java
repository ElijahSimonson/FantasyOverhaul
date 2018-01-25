package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import nz.castorgaming.fantasyoverhaul.capabilities.extendedPlayer.IExtendPlayer;
import nz.castorgaming.fantasyoverhaul.util.enums.TransformCreatures;

public class EntitySizeInfo {
	public final float defaultWidth;
	public final float defaultHeight;
	public final float eyeHeight;
	public final float stepSize;
	public final boolean isDefault;
	public final TransformCreatures creature;

	public EntitySizeInfo(final EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			creature = IExtendPlayer.get(player).getCreatureType();
			final NBTTagCompound nbtEntity = entity.getEntityData();
			switch (creature) {
				default: {
					isDefault = true;
					defaultWidth = 0.6f;
					defaultHeight = 1.8f;
					stepSize = 0.5f;
					eyeHeight = player.getDefaultEyeHeight();
					break;
				}
				case WOLF: {
					isDefault = false;
					defaultWidth = 0.6f;
					defaultHeight = 0.8f;
					eyeHeight = defaultHeight * 0.92f;
					stepSize = 1.0f;
					break;
				}
				case WOLFMAN: {
					isDefault = true;
					defaultWidth = 0.6f;
					defaultHeight = 1.8f;
					eyeHeight = player.getDefaultEyeHeight();
					stepSize = 1.0f;
					break;
				}
				case BAT: {
					isDefault = false;
					defaultWidth = 0.3f;
					defaultHeight = 0.6f;
					eyeHeight = defaultHeight * 0.8f;
					stepSize = 0.5f;
					break;
				}
				case TOAD: {
					isDefault = false;
					defaultWidth = 0.3f;
					defaultHeight = 0.5f;
					eyeHeight = defaultHeight * 0.92f;
					stepSize = 0.5f;
					break;
				}
			}
		}
		else {
			final NBTTagCompound nbtEntity2 = entity.getEntityData();
			defaultWidth = nbtEntity2.getFloat("WITCInitialWidth");
			defaultHeight = nbtEntity2.getFloat("WITCInitialHeight");
			stepSize = entity instanceof EntityHorse || entity instanceof EntityEnderman ? 1.0f : 0.5f;
			eyeHeight = 0.12f;
			isDefault = true;
			creature = TransformCreatures.NONE;
		}
	}
}
