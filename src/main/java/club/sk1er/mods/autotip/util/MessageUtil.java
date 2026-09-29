/**
 Autotip - tips online boosters, and other online Autotip users.
 You send and receive tips, increasing your coin and experience gain.
 Copyright (C) 2026 QWERTZ, Sk1erLLC

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package club.sk1er.mods.autotip.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {
    private final Pattern PARAM_PATTERN = Pattern.compile("\\{}");
    public static final String PREFIX = "[Autotip] "; // TODO: Improve prefix

    public MessageUtil() {
    }

    public void send(String message, Object... replacements) {
        send(format(message, replacements));
    }
    public void error(String message, Object... replacements) {
        send(PREFIX + message, replacements);
    }
    public void send(String message) {
        Minecraft mc = Minecraft.getInstance();
        // Must run on render thread
        mc.execute(() -> {
            if (mc.player != null) {
                //? if < 26.1 {
                // mc.player.displayClientMessage(Component.literal(message), false);
                //?} else {
                mc.player.sendSystemMessage(Component.literal(message));
                //?}
            } else {
                System.out.println(PREFIX + message);
            }
        });
    }

    public void log(String log) {
        System.out.println(PREFIX + log);
    }

    public void sendCommand(String command) {
        String cmd = command.startsWith("/") ? command.substring(1) : command;
        Minecraft mc = Minecraft.getInstance();
        // Must run on render thread
        mc.execute(() -> {
            ClientPacketListener connection = mc.getConnection();
            if (connection != null) {
                connection.sendCommand(cmd);
            }
        });
    }

    private String format(String input, Object... params) {
        if (params == null) {
            return input;
        }
        for (Object o : params) {
            if (o != null) {
                String replacement = Matcher.quoteReplacement(format(o.toString()));
                input = PARAM_PATTERN.matcher(input).replaceFirst(replacement);
            }
        }
        return input;
    }
}
