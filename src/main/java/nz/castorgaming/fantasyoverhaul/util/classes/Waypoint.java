package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nz.castorgaming.fantasyoverhaul.init.ItemInit;

public class Waypoint {

	    public final boolean valid;
	    public final double X;
	    public final double Y;
	    public final double Z;
	    public final double D;
	    
	    public Waypoint(final World world, final double homeX, final double homeY, final double homeZ) {
	        this.X = homeX;
	        this.Y = homeY;
	        this.Z = homeZ;
	        this.D = world.provider.getDimension();
	        this.valid = true;
	    }
	    
	    public Waypoint(final World world, final BlockPos pos) {
	        this.X = pos.getX();
	        this.Y = pos.getY();
	        this.Z = pos.getZ();
	        this.D = world.provider.getDimension();
	        this.valid = true;
	    }
	    
	    public Waypoint(final World world, final ItemStack stack, final double homeX, final double homeY, final double homeZ) {
	        if (ItemInit.WAYSTONE_BOUND.isMatch(stack)) {
	            final NBTTagCompound nbtWaystone = stack.getTagCompound();
	            final int x = nbtWaystone.getInteger("PosX");
	            final int z = nbtWaystone.getInteger("PosZ");
	            if (world.getChunkFromBlockCoords(new BlockPos(x, nbtWaystone.getInteger("PosY"), z)).isLoaded()) {
	                this.X = x + 0.5;
	                this.Y = nbtWaystone.getInteger("PosY") + 1.5;
	                this.Z = z + 0.5;
	                this.D = nbtWaystone.getInteger("PosD");
	                this.valid = true;
	            }
	            else {
	                this.X = homeX;
	                this.Y = homeY;
	                this.Z = homeZ;
	                this.D = world.provider.getDimension();
	                this.valid = false;
	            }
	        }
	        else if (ItemInit.WAYSTONE_BOUND_PLAYER.isMatch(stack)) {
	            final EntityLivingBase entity = ItemInit.TAGLOCK_KIT.getBoundEntity(world, null, stack, 1);
	            if (entity != null) {
	                this.X = entity.posX;
	                this.Y = entity.posY + 1.0;
	                this.Z = entity.posZ;
	                this.D = entity.dimension;
	                this.valid = true;
	            }
	            else {
	                this.X = homeX;
	                this.Y = homeY;
	                this.Z = homeZ;
	                this.D = world.provider.getDimension();
	                this.valid = false;
	            }
	        }
	        else if (stack != null && stack.getItem() == ItemInit.TAGLOCK_KIT) {
	            final EntityLivingBase entity = ItemInit.TAGLOCK_KIT.getBoundEntity(world, null, stack, 1);
	            if (entity != null) {
	                this.X = entity.posX;
	                this.Y = entity.posY + 1.0;
	                this.Z = entity.posZ;
	                this.D = entity.dimension;
	                this.valid = true;
	            }
	            else {
	                this.X = homeX;
	                this.Y = homeY;
	                this.Z = homeZ;
	                this.D = world.provider.getDimension();
	                this.valid = false;
	            }
	        }
	        else {
	            this.X = homeX;
	            this.Y = homeY;
	            this.Z = homeZ;
	            this.D = world.provider.getDimension();
	            this.valid = false;
	        }
	    }

}
