package nz.castorgaming.fantasyoverhaul.powers.symbols;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import nz.castorgaming.fantasyoverhaul.init.InfusionInit;
import nz.castorgaming.fantasyoverhaul.powers.infusions.Infusion;
import nz.castorgaming.fantasyoverhaul.util.Reference;
import nz.castorgaming.fantasyoverhaul.util.classes.GeneralUtil;
import nz.castorgaming.fantasyoverhaul.util.classes.TimeUtilities;

public abstract class SymbolEffect extends IForgeRegistryEntry.Impl<SymbolEffect> {

	private final int effectID;
	private final int chargeCost;
	private final boolean curse;
	private final boolean fallsToEarth;
	private final String knowledgeKey;
	private final boolean isVisible;
	private byte[] defaultStrokes;
	private final int cooldownTicks;
	private final String unlocalizedName;

	public static final RegistryNamespacedDefaultedByKey<ResourceLocation, SymbolEffect> REGISTRY = (RegistryNamespacedDefaultedByKey<ResourceLocation, SymbolEffect>) GameRegistry
			.findRegistry(SymbolEffect.class);

	public SymbolEffect(String name) {
		this(name, 1, false, false, null, 0, true);
	}

	public SymbolEffect(String name, int cost, boolean curse, boolean fallsToEarth, String knowledgeKey, int cooldown) {
		this(name, cost, curse, fallsToEarth, knowledgeKey, cooldown, true);
	}

	public SymbolEffect(String name, int cost, boolean curse, boolean fallsToEarth, String knowledgeKey, int cooldown,
			boolean isVisible) {
		setRegistryName(name);
		this.curse = curse;
		this.fallsToEarth = fallsToEarth;
		this.knowledgeKey = knowledgeKey;
		this.cooldownTicks = cooldown;
		this.isVisible = isVisible;
		chargeCost = cost;
		unlocalizedName = "symbol." + name;
		effectID = REGISTRY.getIDForObject(this);
	}

	public int getEffectID() {
		return effectID;
	}

	public boolean isCurse() {
		return curse;
	}

	public boolean isUnforgivable() {
		return curse && knowledgeKey == null;
	}

	public String getLocalizedName() {
		return GeneralUtil.resource(getRegistryName());
	}

	public abstract void perform(World world, EntityPlayer player, int level);

	public int getChargeCost(World world, EntityPlayer player, int level) {
		return MathHelper.floor_double(Math.pow(2.0, level - 1) * chargeCost);
	}

	public boolean fallsToEarth() {
		return fallsToEarth;
	}

	public boolean hasValidInfusion(EntityPlayer player, String infusionID) {
		return player.capabilities.isCreativeMode || (infusionID != null
				&& (!isUnforgivable() || infusionID.equals(InfusionInit.INFERNAL.getInfusionName())));
	}

	public boolean isVisible(EntityPlayer player) {
		return isVisible;
	}

	public String getDescription() {
		StringBuffer sb = new StringBuffer();

		sb.append("§n");
		sb.append(GeneralUtil.resource(unlocalizedName));
		sb.append("§r");
		sb.append(Reference.x2_BOOK_NEWLINE);

		String descKey = getRegistryName() + ".info";
		String desc = GeneralUtil.resource(descKey);
		if (desc != null && desc.isEmpty() && !desc.equals(descKey)) {
			sb.append(desc);
			sb.append(Reference.x2_BOOK_NEWLINE);
		}
		sb.append("§8");
		sb.append(GeneralUtil.resource("fo.book.wands.strokes"));
		sb.append("§0");
		sb.append(Reference.BOOK_NEWLINE);

		int i = 1;

		for (byte stroke : defaultStrokes) {
			sb.append(i++);
			sb.append(": ");
			sb.append(GeneralUtil.resource("fo.book.wands.stroke." + stroke));
			sb.append(Reference.BOOK_NEWLINE);
		}

		return sb.toString();
	}

	public void setDefaultStrokes(byte[] strokes) {
		defaultStrokes = strokes;
	}

	public boolean hasValidKnowledge(EntityPlayer player, NBTTagCompound compound) {
		if (player.capabilities.isCreativeMode) {
			return true;
		}
		if (knowledgeKey == null) {
			return true;
		}
		if (compound.hasKey(Reference.SPELL_BOOK_KEY)) {
			NBTTagCompound spells = compound.getCompoundTag(Reference.SPELL_BOOK_KEY);
			return spells.getBoolean(knowledgeKey);
		}
		return false;
	}

	public void acquireKnowledge(EntityPlayer player) {
		if (knowledgeKey != null) {
			NBTTagCompound compound, spells;
			compound = Infusion.getNBT(player);
			if (!compound.hasKey(Reference.SPELL_BOOK_KEY)) {
				compound.setTag(Reference.SPELL_BOOK_KEY, new NBTTagCompound());
			}
			spells = compound.getCompoundTag(Reference.SPELL_BOOK_KEY);
			spells.setBoolean(knowledgeKey, true);
		}
	}

	public static String getKnowledge(EntityPlayer player) {
		StringBuilder sb = new StringBuilder();
		NBTTagCompound compound = Infusion.getNBT(player);
		if (compound != null && compound.hasKey(Reference.SPELL_BOOK_KEY)) {
			for (SymbolEffect effect : SymbolEffect.REGISTRY) {
				if (effect.knowledgeKey != null && effect.hasValidKnowledge(player, compound)) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(effect.getLocalizedName());
				}
			}
		}
		return sb.toString();
	}

	public long cooldownRemaining(EntityPlayer player, NBTTagCompound compound) {
		if (cooldownTicks > 0 && knowledgeKey != null && compound.hasKey(Reference.SPELL_BOOK_KEY)) {
			NBTTagCompound spells = compound.getCompoundTag(Reference.SPELL_BOOK_KEY);
			long lastUseTime = spells.getLong(knowledgeKey + "LastUse");
			long timeNow = TimeUtilities.getServerTimeInTicks();
			if (timeNow < lastUseTime + cooldownTicks) {
				return lastUseTime + cooldownTicks - timeNow;
			}
		}
		return 0L;
	}

	public void setOnCooldown(EntityPlayer player) {
		if (cooldownTicks > 0 && knowledgeKey != null && !player.capabilities.isCreativeMode) {
			NBTTagCompound compound = Infusion.getNBT(player);
			if (compound != null && compound.hasKey(Reference.SPELL_BOOK_KEY)) {
				NBTTagCompound spells = compound.getCompoundTag(Reference.SPELL_BOOK_KEY);
				spells.setLong(knowledgeKey + "LastUse", TimeUtilities.getServerTimeInTicks());
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		SymbolEffect other = (SymbolEffect) obj;
		return other.getRegistryName().equals(getRegistryName());
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + effectID;
		return result;
	}
}
