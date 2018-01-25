package nz.castorgaming.fantasyoverhaul.client.renderer;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityReflection;
import nz.castorgaming.fantasyoverhaul.util.Reference;

@SideOnly(Side.CLIENT)
public class RenderReflection extends RenderBiped {

	private RenderWolfman wolfman;
	public static final ResourceLocation SKIN = new ResourceLocation(Reference.MODID, "relection_base");

	public RenderReflection(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBiped(), 0.5f);
		wolfman = new RenderWolfman(renderManagerIn, new ModelWolfman(), 0.5f);
	}

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (((EntityReflection) entity).getModel() == 0) {
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
		else {
			wolfman.setRenderManager(renderManager);
			wolfman.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		if (((EntityReflection) entityIn).getModel() == 0) {
			super.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
		}
		else {
			wolfman.setRenderManager(renderManager);
			wolfman.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity) {
		return getEntityTexture((EntityReflection) entity);
	}

	protected ResourceLocation getEntityTexture(EntityReflection entity) {
		if (entity.getModel() == 0) {
			ResourceLocation skin = entity.getLocationSkin();
			if (skin == null) {
				skin = RenderReflection.SKIN;
			}
			return skin;
		}
		return RenderWolfman.TEXTURE;
	}

}
