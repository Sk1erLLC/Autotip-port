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

package club.sk1er.mods.autotip;

import club.sk1er.mods.autotip.api.AutotipHttpClient;
import club.sk1er.mods.autotip.auth.AuthManager;
import club.sk1er.mods.autotip.chat.ChatListener;
import club.sk1er.mods.autotip.command.CommandManager;
import club.sk1er.mods.autotip.config.Config;
import club.sk1er.mods.autotip.stats.StatsManager;
import club.sk1er.mods.autotip.tipping.TipManager;
import club.sk1er.mods.autotip.util.HypixelUtil;
import club.sk1er.mods.autotip.util.MessageUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class Autotip implements ClientModInitializer {
    public static final String ID = /*$ mod_id*/ "autotip";
    public static final String NAME = /*$ mod_name*/ "AutoTip";
    public static final String VERSION = /*$ mod_version*/ "3.3";
    public static final String MC_VERSION = /*$ minecraft*/ "26.3";
    public static final String ICON = "/assets/" + ID + "/logo.png";

    // NEVER TRUE IN PRODUCTION
    public static final boolean DEBUG = false;
    private MessageUtil messageUtil;
    private AuthManager authManager;
    private AutotipHttpClient autotipHttpClient;
    private static Autotip instance;
    private CommandManager commandManager;
    private TipManager tipManager;
    private StatsManager statsManager;
    private ChatListener chatListener;
    private Config config;

    @Override
    public void onInitializeClient() {
        instance = this;
        messageUtil = new MessageUtil();
        config = new Config();
        authManager = new AuthManager();
        autotipHttpClient = new AutotipHttpClient();
        commandManager = new CommandManager();
        tipManager = new TipManager();
        statsManager = new StatsManager();
        chatListener = new ChatListener();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            HypixelUtil.onServerJoin();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            HypixelUtil.onServerDisconnect();
        });
    }

    public static Autotip getInstance() {
        return instance;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public AutotipHttpClient getAutotipHttpClient() {
        return autotipHttpClient;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public TipManager getTipManager() {
        return tipManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public Config getConfig() {
        return config;
    }
}
