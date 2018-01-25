package nz.castorgaming.fantasyoverhaul.util.classes;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ChatUtilities {

	public static void sendTranslated(ICommandSender player, String key, Object... params) {
		player.addChatMessage(new TextComponentTranslation(key, params));
	}

	public static void sendTranslated(TextFormatting color, ICommandSender player, String key, Object... params) {
		player.addChatMessage(new TextComponentTranslation(key, params).setStyle(new Style().setColor(color)));
	}

	public static void sendPlain(ICommandSender player, String text) {
		player.addChatMessage(new TextComponentString(text));
	}

	public static void sendPlain(TextFormatting color, ICommandSender player, String text) {
		player.addChatMessage(new TextComponentString(text).setStyle(new Style().setColor(color)));
	}

}
