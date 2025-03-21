package com.github.razorplay01.geowaremod.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import lombok.Setter;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardCommand {
    @Setter
    private static Scoreboard scoreboard;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("showscoreboard")
                        .then(ClientCommandManager.argument("fadeIn", IntegerArgumentType.integer(0))
                                .then(ClientCommandManager.argument("stay", IntegerArgumentType.integer(0))
                                        .then(ClientCommandManager.argument("fadeOut", IntegerArgumentType.integer(0))
                                                .then(ClientCommandManager.argument("offsetX", IntegerArgumentType.integer())
                                                        .then(ClientCommandManager.argument("offsetY", IntegerArgumentType.integer())
                                                                .then(ClientCommandManager.argument("scale", FloatArgumentType.floatArg())
                                                                        .then(ClientCommandManager.argument("texts", StringArgumentType.greedyString())
                                                                                .executes(context -> {
                                                                                    int fadeIn = IntegerArgumentType.getInteger(context, "fadeIn");
                                                                                    int stay = IntegerArgumentType.getInteger(context, "stay");
                                                                                    int fadeOut = IntegerArgumentType.getInteger(context, "fadeOut");
                                                                                    int offsetX = IntegerArgumentType.getInteger(context, "offsetX");
                                                                                    int offsetY = IntegerArgumentType.getInteger(context, "offsetY");
                                                                                    float scale = FloatArgumentType.getFloat(context, "scale");
                                                                                    String textsInput = StringArgumentType.getString(context, "texts");

                                                                                    List<String> texts = new ArrayList<>();
                                                                                    String[] splitTexts = textsInput.split(",");
                                                                                    for (String text : splitTexts) {
                                                                                        texts.add(text.trim());
                                                                                    }

                                                                                    if (scoreboard != null) {
                                                                                        scoreboard.showScoreboard(texts, fadeIn, stay, fadeOut, offsetX, offsetY, scale);
                                                                                    }
                                                                                    return 1;
                                                                                })
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }
}