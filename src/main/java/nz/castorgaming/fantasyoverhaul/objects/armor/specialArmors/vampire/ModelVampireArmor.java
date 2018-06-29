package nz.castorgaming.fantasyoverhaul.objects.armor.specialArmors.vampire;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import nz.castorgaming.fantasyoverhaul.objects.entities.mobs.EntityVampire;

public class ModelVampireArmor extends ModelBiped {

	private ModelRenderer skirtFront, skirtMiddle, skirtMiddle2, skirtMiddle3, skirtBack, cloakMain, cloakLeft,
			cloakRight;
	public ModelRenderer hat, hatBrim, chest;
	private boolean legs, female, metal;

	public ModelVampireArmor(float scale, boolean legs, boolean female, boolean metal) {

		super(scale, 0.0F, 64, 96);

		this.legs = legs;
		this.female = female;
		this.metal = metal;

		skirtBack = new ModelRenderer(this, 26, 32);
		skirtBack.setRotationPoint(0.0F, 11.0F, 0.0F);
		skirtBack.addBox(-4.5F, 0.0F, -2.5F, 9, 12, 5, 0.0F);

		skirtFront = new ModelRenderer(this, 26, 50);
		skirtFront.setRotationPoint(0.0F, 11.0F, 0.0F);
		skirtFront.addBox(-4.5F, 0.0F, -2.5F, 9, 12, 5, 0.0F);

		skirtMiddle = new ModelRenderer(this, 26, 28);
		skirtMiddle.setRotationPoint(0.0F, 11.0F, 0.0F);
		skirtMiddle.addBox(-4.5F, 0.0F, -2.5F, 9, 12, 5, 0.0F);
		skirtMiddle2 = new ModelRenderer(this, 26, 68);
		skirtMiddle2.setRotationPoint(0.0F, 11.0F, 0.0F);
		skirtMiddle2.addBox(-4.5F, 0.0F, -2.5F, 9, 12, 5, 0.0F);

		skirtMiddle3 = new ModelRenderer(this, 26, 68);
		skirtMiddle3.setRotationPoint(0.0F, 11.0F, 0.0F);
		skirtMiddle3.addBox(-4.5F, 0.0F, -2.5F, 9, 12, 5, 0.0F);

		cloakLeft = new ModelRenderer(this, 0, 56);
		cloakLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
		cloakLeft.addBox(-3.5F, -8.0F, 4.0F, 7, 7, 1, 0.0F);
		setRotateAngle(cloakLeft, -0.34906584F, 0.5108652F, 0.41086525F);

		cloakRight = new ModelRenderer(this, 0, 56);
		cloakRight.setRotationPoint(0.0F, 0.0F, 0.0F);
		cloakRight.addBox(-3.5F, -8.0F, 4.0F, 7, 7, 1, 0.0F);
		setRotateAngle(cloakRight, -0.34906584F, -0.5108652F, -0.41086525F);

		cloakMain = new ModelRenderer(this, 0, 33);
		cloakMain.setRotationPoint(0.0F, 1.0F, 0.0F);
		cloakMain.addBox(-6.0F, 0.0F, 2.5F, 12, 22, 1, 0.0F);
		setRotateAngle(cloakMain, 0.045553092F, 0.0F, 0.0F);

		float hatScale = 0.6F;

		hatBrim = new ModelRenderer(this, 0, 85);
		hatBrim.setRotationPoint(0.0F, 0.0F, 0.0F);
		hatBrim.addBox(-5.0F, -7.0F, -5.0F, 10, 1, 10, hatScale + 1.0F);

		hat = new ModelRenderer(this, 0, 67);
		hat.setRotationPoint(0.0F, 0.0F, 0.0F);
		hat.addBox(-4.0F, -15.0F, -4.0F, 8, 8, 8, hatScale);

		if (!metal) {
			bipedHead.addChild(hat);
			bipedHead.addChild(hatBrim);
		}

		chest = new ModelRenderer(this, 16, 27);
		chest.setRotationPoint(0.0F, 2.0F, 0.0F);
		chest.addBox(-4.0F, -2.0F, -5.0F, 8, 4, 4, 0.0F);
		setRotateAngle(chest, 0.7853982F, 0.0F, 0.0F);

	}

	ResourceLocation chain = new ResourceLocation("witchery", "textures/entities/vampirearmor_chain.png");

	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

		if (!metal) {
			bipedHeadwear.showModel = false;
		}

		if (legs) {
			if ((!isRiding) && (bipedRightLeg.showModel) && (female)) {
				skirtBack.render(scale);
				skirtFront.render(scale);
				skirtMiddle.render(scale);
				skirtMiddle2.render(scale);
				skirtMiddle3.render(scale);
			}
		} else if (bipedBody.showModel) {
			if (!(entityIn instanceof EntityVampire)) {
				cloakRight.render(scale);
				cloakLeft.render(scale);
				cloakMain.render(scale);
			}
			if (female) {
				chest.render(scale);
			}
			if (metal) {
				GL11.glPushMatrix();
				float scale2 = 1.06F;
				GL11.glScalef(scale2, scale2, scale2);

				Minecraft.getMinecraft().getTextureManager().bindTexture(chain);
				if (female) {
					chest.render(scale);
				}

				bipedBody.render(scale);

				GL11.glScalef(scale2, scale2, scale2);
				ModelRenderer tmp243_240 = bipedRightArm;
				tmp243_240.rotationPointY = ((float) (tmp243_240.rotationPointY - 0.05D));
				ModelRenderer tmp260_257 = bipedLeftArm;
				tmp260_257.rotationPointY = ((float) (tmp260_257.rotationPointY - 0.05D));
				bipedLeftArm.render(scale);
				bipedRightArm.render(scale);
				GL11.glPopMatrix();
			}
		}
	}

	public void setRotateAngle(ModelRenderer renderer, float x, float y, float z) {
		renderer.rotateAngleX = x;
		renderer.rotateAngleY = y;
		renderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

		bipedRightArm.rotationPointY = 2.0F;
		bipedLeftArm.rotationPointY = 2.0F;

		hat.rotationPointY = -0.5F;

		skirtBack.rotateAngleX = Math.max(bipedRightLeg.rotateAngleX, bipedLeftLeg.rotateAngleX);
		skirtMiddle.rotateAngleX = Math.max(bipedRightLeg.rotateAngleX, bipedLeftLeg.rotateAngleX * 0.5F);

		skirtFront.rotateAngleX = Math.min(bipedRightLeg.rotateAngleX, bipedLeftLeg.rotateAngleX);
		skirtMiddle2.rotateAngleX = Math.min(bipedRightLeg.rotateAngleX, bipedLeftLeg.rotateAngleX * 0.5F);

		if (isSneak) {
			skirtBack.rotationPointZ = (skirtFront.rotationPointZ = skirtMiddle3.rotationPointZ = skirtMiddle.rotationPointZ = skirtMiddle2.rotationPointZ = 4.0F);
			skirtBack.rotationPointY = (skirtFront.rotationPointY = skirtMiddle3.rotationPointY = skirtMiddle.rotationPointY = skirtMiddle2.rotationPointY = 8.0F);

			cloakMain.rotateAngleX = 0.6F;
		} else {
			skirtBack.rotationPointZ = (skirtFront.rotationPointZ = skirtMiddle3.rotationPointZ = skirtMiddle.rotationPointZ = skirtMiddle2.rotationPointZ = 0.0F);
			skirtBack.rotationPointY = (skirtFront.rotationPointY = skirtMiddle3.rotationPointY = skirtMiddle.rotationPointY = skirtMiddle2.rotationPointY = 11.0F);

			cloakMain.rotateAngleX = 0.045553092F;
			if (limbSwingAmount > 0.1D) {
				ModelRenderer tmp346_343 = cloakMain;
				tmp346_343.rotateAngleX = ((float) (tmp346_343.rotateAngleX + (limbSwingAmount * 0.8D - 0.1D)));
			}
		}

		if (bipedHead.rotateAngleX < -0.15D) {
			cloakLeft.rotateAngleX = (bipedHead.rotateAngleX - 0.15F);
			cloakRight.rotateAngleX = (bipedHead.rotateAngleX - 0.15F);
		} else {
			cloakLeft.rotateAngleX = (cloakRight.rotateAngleX = -0.3F);
		}
	}

}
