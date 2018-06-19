package nz.castorgaming.fantasyoverhaul.objects.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBroom extends Entity{
	
	private boolean isBoatEmpty;
	private double speedMulti;
	private int broomPosRotationInc;
	private double broomX, broomY, broomZ, broomYaw, broomPitch;
	@SideOnly(Side.CLIENT)
	private double veloctiyX, velocityY, velocityZ;
	boolean riderHasOwlFamiliar;
	boolean riderHasOwlFamilar, riderHasSoaringBrew;
	
	private DataParameter<Integer> BRUSH_COLOR = EntityDataManager.createKey(EntityBroom.class, DataSerializers.VARINT);
	private DataParameter<String> NAME = EntityDataManager.createKey(EntityBroom.class, DataSerializers.STRING);
	
	public EntityBroom(World worldIn) {
		super(worldIn);
		riderHasOwlFamiliar = false;
		riderHasSoaringBrew = false;
		isBoatEmpty = true;
		speedMulti = 0.07;
		preventEntitySpawning = true;
		setSize(1.2f, 0.5f);
	}
	
	public EntityBroom(World world, double x, double y, double z) {
		this(world);
		setPosition(x, y + getYOffset(), z);
		motionX = 0.0;
		motionY = 0.0;
		motionZ = 0.0;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}
	
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}
	
	@Override
	protected void entityInit() {
		dataManager.register(BRUSH_COLOR, EnumDyeColor.BROWN.getDyeDamage());
		dataManager.register(NAME, "");		
	}
	
	@Override
	protected void dealFireDamage(int amount) {
	}
	
	public void setBrushColor(EnumDyeColor color) {
		dataManager.set(BRUSH_COLOR, color.getDyeDamage());
	}
	
	public void setBrushColor(int color) {
		if (color < EnumDyeColor.values().length) {
			setBrushColor(EnumDyeColor.byDyeDamage(color));
		}
	}
	
	public EnumDyeColor getBrushColor() {
		return EnumDyeColor.byDyeDamage(dataManager.get(BRUSH_COLOR));
	}
	
	@Override
	public double getYOffset() {
		return height / 2.0f;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}
	
	@Override
	public double getMountedYOffset() {
		return height * 0.55;
	}
	
	@Override
	public String getCustomNameTag() {
		return dataManager.get(NAME);
	}
	
	@Override
	public boolean hasCustomName() {
		return dataManager.get(NAME).length() > 0;
	}
	
	public void setCustomNameTag(String name) {
		dataManager.set(NAME, name);
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !isDead && isBeingRidden();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void performHurtAnimation() {
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() * 11.0f);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void setVelocity(double x, double y, double z) {
		veloctiyX = x;
		velocityY = y;
		velocityZ = z;
		super.setVelocity(x, y, z);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setString("CustomName", getCustomNameTag());
		int brushColor = getBrushColor().getDyeDamage();
		if (brushColor >= 0) {
			compound.setByte("BrushColor", Byte.valueOf((byte) brushColor));
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("CustomName") && compound.getString("CustomName").length() > 0) {
			setCustomNameTag(compound.getString("CustomName"));
		}
		if (compound.hasKey("BrushColor")) {
			setBrushColor(compound.getByte("BrushColor"));
		}
	}
	

}
