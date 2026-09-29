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

package club.sk1er.mods.autotip.command;

import club.sk1er.mods.autotip.Autotip;
import club.sk1er.mods.autotip.auth.AuthManager;
import club.sk1er.mods.autotip.config.MessageMode;
import club.sk1er.mods.autotip.stats.Stats;
import club.sk1er.mods.autotip.stats.StatsManager;
import club.sk1er.mods.autotip.util.HypixelUtil;
import club.sk1er.mods.autotip.util.MessageUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//? if < 26.1 {
// import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//?} else {
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
//?}
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class CommandManager {

    private final MessageUtil messageUtil;

    // IMPORTANT: Games that use Tokens instead of Coins
    private static final java.util.Set<String> TOKEN_GAMES = java.util.Set.of(
            "The TNT Games",
            "TNT Games"
    );

    public CommandManager() {
        this.messageUtil = Autotip.getInstance().getMessageUtil();
        registerCommands();
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(buildCommand("autotip"));
            dispatcher.register(buildCommand("at"));
        });
    }

    private LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String name) {
        //? if < 26.1 {
        // return ClientCommandManager.literal(name)
        //?} else {
        return ClientCommands.literal(name)
        //?}
                .executes(context -> {
                    showHelp();
                    return 1;
                })
                .then(buildInfoSubcommand("info"))
                .then(buildInfoSubcommand("i"))
                .then(buildStatsSubcommand("stats"))
                .then(buildStatsSubcommand("s"))
                .then(buildCurrencySubcommand("currency"))
                .then(buildCurrencySubcommand("c"))
                .then(buildMessagesSubcommand("messages"))
                .then(buildMessagesSubcommand("m"));
    }

    private LiteralArgumentBuilder<FabricClientCommandSource> buildInfoSubcommand(String name) {
        //? if < 26.1 {
        // return ClientCommandManager.literal(name)
        //?} else {
        return ClientCommands.literal(name)
        //?}
                .executes(context -> {
                    showInfo();
                    return 1;
                });
    }

    private LiteralArgumentBuilder<FabricClientCommandSource> buildStatsSubcommand(String name) {
        //? if < 26.1 {
        // return ClientCommandManager.literal(name)
        //?} else {
        return ClientCommands.literal(name)
        //?}
                .executes(context -> {
                    showStats(StatsManager.StatsPeriod.LIFETIME, null);
                    return 1;
                })
                //? if < 26.1 {
                // .then(ClientCommandManager.argument("period", StringArgumentType.greedyString())
                //?} else {
                .then(ClientCommands.argument("period", StringArgumentType.greedyString())
                //?}
                        .suggests((context, builder) -> {
                            builder.suggest("daily");
                            builder.suggest("weekly");
                            builder.suggest("monthly");
                            builder.suggest("yearly");
                            builder.suggest("lifetime");
                            builder.suggest(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String periodStr = StringArgumentType.getString(context, "period");
                            LocalDate date = parseDate(periodStr);
                            if (date != null) {
                                showStats(null, date);
                                return 1;
                            }
                            StatsManager.StatsPeriod period = parsePeriod(periodStr);
                            if (period == null) {
                                messageUtil.send("§cUnknown period: " + periodStr);
                                messageUtil.send("§cValid: daily, weekly, monthly, yearly, lifetime, or DD/MM/YYYY");
                                return 0;
                            }
                            showStats(period, null);
                            return 1;
                        }));
    }

    private LiteralArgumentBuilder<FabricClientCommandSource> buildCurrencySubcommand(String name) {
        //? if < 26.1 {
        // return ClientCommandManager.literal(name)
        //?} else {
        return ClientCommands.literal(name)
        //?}
                .executes(context -> {
                    showCurrency(StatsManager.StatsPeriod.LIFETIME, null);
                    return 1;
                })
                //? if < 26.1 {
                // .then(ClientCommandManager.argument("period", StringArgumentType.greedyString())
                //?} else {
                .then(ClientCommands.argument("period", StringArgumentType.greedyString())
                //?}
                        .suggests((context, builder) -> {
                            builder.suggest("daily");
                            builder.suggest("weekly");
                            builder.suggest("monthly");
                            builder.suggest("yearly");
                            builder.suggest("lifetime");
                            builder.suggest(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String periodStr = StringArgumentType.getString(context, "period");
                            LocalDate date = parseDate(periodStr);
                            if (date != null) {
                                showCurrency(null, date);
                                return 1;
                            }
                            StatsManager.StatsPeriod period = parsePeriod(periodStr);
                            if (period == null) {
                                messageUtil.send("§cUnknown period: " + periodStr);
                                messageUtil.send("§cValid: daily, weekly, monthly, yearly, lifetime, or DD/MM/YYYY");
                                return 0;
                            }
                            showCurrency(period, null);
                            return 1;
                        }));
    }

    private LiteralArgumentBuilder<FabricClientCommandSource> buildMessagesSubcommand(String name) {
        //? if < 26.1 {
        // return ClientCommandManager.literal(name)
        //?} else {
        return ClientCommands.literal(name)
        //?}
                .executes(context -> {
                    showCurrentMessageMode();
                    return 1;
                })
                //? if < 26.1 {
                // .then(ClientCommandManager.argument("mode", StringArgumentType.greedyString())
                //?} else {
                .then(ClientCommands.argument("mode", StringArgumentType.greedyString())
                //?}
                        .suggests((context, builder) -> {
                            builder.suggest("all");
                            builder.suggest("off");
                            builder.suggest("success");
                            builder.suggest("error");
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String modeStr = StringArgumentType.getString(context, "mode");
                            MessageMode mode = MessageMode.fromString(modeStr);
                            if (mode == null) {
                                messageUtil.send("§cUnknown mode: " + modeStr);
                                messageUtil.send("§cValid: all, off, success, error");
                                return 0;
                            }
                            setMessageMode(mode);
                            return 1;
                        }));
    }

    private void showHelp() {
        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        messageUtil.send("§a§lAutotip v" + Autotip.VERSION + " §aby §6QWERTZ_EXE §aand §6Sk1erLLC");
        messageUtil.send("");
        messageUtil.send("§e/autotip info §7- Show connection info");
        messageUtil.send("§e/autotip stats [period] §7- Show tipping stats");
        messageUtil.send("§e/autotip currency [period] §7- Show coins/tokens");
        messageUtil.send("§e/autotip messages [mode] §7- Set message visibility");
        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void showInfo() {
        boolean onHypixel = HypixelUtil.isOnHypixel();
        boolean loggedIn = AuthManager.loggedIn;
        boolean tipping = Autotip.getInstance().getTipManager().isRunning();

        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        messageUtil.send("§a§lAutotip Info");
        messageUtil.send("");
        messageUtil.send("§7On Hypixel: " + (onHypixel ? "§aYes" : "§cNo"));
        messageUtil.send("§7Logged in: " + (loggedIn ? "§aYes" : "§cNo"));
        messageUtil.send("§7Tipping: " + (tipping ? "§aActive" : "§cInactive"));

        if (tipping) {
            long nextWave = Autotip.getInstance().getTipManager().getNextTipWave();
            if (nextWave > 0) {
                long secondsLeft = (nextWave - System.currentTimeMillis()) / 1000;
                if (secondsLeft > 0) {
                    messageUtil.send("§7Next tip wave: §e" + formatTime(secondsLeft));
                }
            }
        }
        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void showStats(StatsManager.StatsPeriod period, LocalDate date) {
        Stats stats;
        String periodName;

        if (date != null) {
            stats = Autotip.getInstance().getStatsManager().getStatsForDate(date);
            periodName = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            stats = Autotip.getInstance().getStatsManager().getStats(period);
            periodName = period.name().charAt(0) + period.name().substring(1).toLowerCase();
        }

        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        messageUtil.send("§a§lAutotip Stats §7(" + periodName + ")");
        messageUtil.send("");
        messageUtil.send("§6Tips Sent: §e" + stats.getTipsSentFormatted());
        messageUtil.send("§6Tips Received: §e" + stats.getTipsReceivedFormatted());
        messageUtil.send("§6Total Tips: §e" + stats.getTipsTotalFormatted());
        messageUtil.send("");
        messageUtil.send("§3XP Sent: §b" + stats.getXpSentFormatted());
        messageUtil.send("§3XP Received: §b" + stats.getXpReceivedFormatted());
        messageUtil.send("§3Total XP: §b" + stats.getXpTotalFormatted());
        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void showCurrency(StatsManager.StatsPeriod period, LocalDate date) {
        Stats stats;
        String periodName;

        if (date != null) {
            stats = Autotip.getInstance().getStatsManager().getStatsForDate(date);
            periodName = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            stats = Autotip.getInstance().getStatsManager().getStats(period);
            periodName = period.name().charAt(0) + period.name().substring(1).toLowerCase();
        }

        Map<String, Integer> coinsSent = stats.getCoinsSent();
        Map<String, Integer> coinsReceived = stats.getCoinsReceived();

        Set<String> allGames = new TreeSet<>();
        allGames.addAll(coinsSent.keySet());
        allGames.addAll(coinsReceived.keySet());
        List<String> sortedGames = new ArrayList<>(allGames);

        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        messageUtil.send("§a§lAutotip Currency §7(" + periodName + ")");

        if (!coinsSent.isEmpty()) {
            messageUtil.send("");
            messageUtil.send("§6§lSent:");
            for (String game : sortedGames) {
                Integer amount = coinsSent.get(game);
                if (amount != null) {
                    boolean isToken = TOKEN_GAMES.contains(game);
                    if (isToken) {
                        messageUtil.send("  §a" + game + ": §2" + Stats.formatNumber(amount) + " Tokens");
                    } else {
                        messageUtil.send("  §6" + game + ": §e" + Stats.formatNumber(amount) + " Coins");
                    }
                }
            }
        }

        if (!coinsReceived.isEmpty()) {
            messageUtil.send("");
            messageUtil.send("§6§lReceived:");
            for (String game : sortedGames) {
                Integer amount = coinsReceived.get(game);
                if (amount != null) {
                    boolean isToken = TOKEN_GAMES.contains(game);
                    if (isToken) {
                        messageUtil.send("  §a" + game + ": §2" + Stats.formatNumber(amount) + " Tokens");
                    } else {
                        messageUtil.send("  §6" + game + ": §e" + Stats.formatNumber(amount) + " Coins");
                    }
                }
            }
        }

        if (coinsSent.isEmpty() && coinsReceived.isEmpty()) {
            messageUtil.send("");
            messageUtil.send("§7No currency earned yet.");
        } else {
            messageUtil.send("");
            messageUtil.send("§eTotal: §6" + stats.getTotalCoinsFormatted() + " §7currency earned");
        }

        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void showCurrentMessageMode() {
        MessageMode mode = Autotip.getInstance().getConfig().getMessageMode();
        String modeDescription = switch (mode) {
            case ALL -> "§aall §7- Showing all tip messages";
            case OFF -> "§coff §7- Hiding all tip messages";
            case SUCCESS -> "§2success §7- Showing only success messages";
            case ERROR -> "§eerror §7- Showing only error messages";
        };

        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        messageUtil.send("§a§lAutotip Messages");
        messageUtil.send("");
        messageUtil.send("§7Current mode: " + modeDescription);
        messageUtil.send("");
        messageUtil.send("§7Available modes:");
        messageUtil.send("  §aall §7- Show all tip messages");
        messageUtil.send("  §coff §7- Hide all tip messages");
        messageUtil.send("  §2success §7- Only show success (you tipped/were tipped)");
        messageUtil.send("  §eerror §7- Only show errors (offline/already tipped)");
        messageUtil.send("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void setMessageMode(MessageMode mode) {
        Autotip.getInstance().getConfig().setMessageMode(mode);
        String modeDescription = switch (mode) {
            case ALL -> "§aAll §7- Showing all tip messages";
            case OFF -> "§cOff §7- Hiding all tip messages";
            case SUCCESS -> "§2Success §7- Showing only success messages";
            case ERROR -> "§eError §7- Showing only error messages";
        };
        messageUtil.send("§6[Autotip] §7Message mode set to: " + modeDescription);
    }

    private String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else {
            return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m";
        }
    }

    private StatsManager.StatsPeriod parsePeriod(String str) {
        return switch (str.toLowerCase()) {
            case "daily", "today", "day", "d" -> StatsManager.StatsPeriod.DAILY;
            case "weekly", "week", "w" -> StatsManager.StatsPeriod.WEEKLY;
            case "monthly", "month", "m" -> StatsManager.StatsPeriod.MONTHLY;
            case "yearly", "year", "y" -> StatsManager.StatsPeriod.YEARLY;
            case "lifetime", "all", "total", "l" -> StatsManager.StatsPeriod.LIFETIME;
            default -> null;
        };
    }

    private LocalDate parseDate(String str) {
        // Try dd/MM/yyyy format
        try {
            return LocalDate.parse(str, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ignored) {}

        // Try dd-MM-yyyy format
        try {
            return LocalDate.parse(str, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException ignored) {}

        // Try yyyy-MM-dd format
        try {
            return LocalDate.parse(str);
        } catch (DateTimeParseException ignored) {}

        return null;
    }
}
