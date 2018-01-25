package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import nz.castorgaming.fantasyoverhaul.util.Reference;

public class GeneralUtil {

	public static String resource(ResourceLocation location) {
		return resource(location.getResourcePath());
	}

	public static String resource(String id) {

		ResourceLocation resource = new ResourceLocation(Reference.MODID, id);
		String toolTip = I18n.format(resource.toString());
		if (toolTip != null) {
			toolTip = toolTip.replace("|", "\n");
		}
		return toolTip;
	}

}
