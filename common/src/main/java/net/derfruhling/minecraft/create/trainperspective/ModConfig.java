/*
 * Part of the Create: Train Perspective project.
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 der_frühling
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.derfruhling.minecraft.create.trainperspective;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final WatchService WATCH_SERVICE;
    private static final Path PATH = Minecraft.getInstance().gameDirectory.toPath()
            .resolve("config")
            .resolve("create-train-perspective.json");
    public static ModConfig INSTANCE = loadConfig();

    static {
        try {
            WATCH_SERVICE = FileSystems.getDefault().newWatchService();
            PATH.getParent().register(WATCH_SERVICE, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean enabled = true;
    public boolean leanEnabled = true;
    public float leanMagnitude = 1.0f;
    public boolean rollEnabled = true;
    public float rollMagnitude = 1.0f;
    public boolean applyToOthers = true;
    public boolean applyToNonPlayerEntities = true;
    public List<ResourceLocation> blockedEntities = new ArrayList<>();
    public boolean dbgShowStandingTransforms = false;
    public boolean dbgShowValueScales = false;

    private ModConfig() {
    }

    public static void tick() {
        var key = WATCH_SERVICE.poll();

        if (key != null) {
            for (var event : key.pollEvents()) {
                if (event.context().toString().equals(PATH.getFileName().toString())) {
                    INSTANCE = loadConfig();
                    key.reset();
                }
            }
        }
    }

    private static ModConfig loadConfig() {
        if (Files.exists(PATH)) {
            try {
                var configJson = Files.readString(PATH);
                return GSON.fromJson(configJson, ModConfig.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            var value = new ModConfig();
            value.save();
            return value;
        }
    }

    public static Screen createConfigScreen(Screen parent) {
        var builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableComponent("title.create_train_perspective.config"))
                .setSavingRunnable(INSTANCE::save);

        var general = builder.getOrCreateCategory(new TranslatableComponent(
                "category.create_train_perspective.general"
        ));

        var entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
                .startBooleanToggle(
                        new TranslatableComponent("option.create_train_perspective.enabled"),
                        INSTANCE.enabled)
                .setTooltip(new TranslatableComponent("option.create_train_perspective.enabled.tooltip"))
                .setSaveConsumer(value -> INSTANCE.enabled = value)
                .setDefaultValue(true)
                .build());

        var leaning = entryBuilder.startSubCategory(new TranslatableComponent(
                "category.create_train_perspective.leaning"
        ));

        leaning.add(entryBuilder
                .startBooleanToggle(
                        new TranslatableComponent("option.create_train_perspective.leaning.enabled"),
                        INSTANCE.leanEnabled)
                .setTooltip(new TranslatableComponent("option.create_train_perspective.leaning.enabled.tooltip"))
                .setSaveConsumer(value -> INSTANCE.leanEnabled = value)
                .setDefaultValue(true)
                .build());

        leaning.add(entryBuilder
                .startBooleanToggle(
                        new TranslatableComponent("option.create_train_perspective.leaning.roll_enabled"),
                        INSTANCE.rollEnabled)
                .setTooltip(new TranslatableComponent("option.create_train_perspective.leaning.roll_enabled.tooltip"))
                .setSaveConsumer(value -> INSTANCE.rollEnabled = value)
                .setDefaultValue(true)
                .build());

        general.addEntry(leaning.build());

        var multiplayer = entryBuilder.startSubCategory(new TranslatableComponent(
                "category.create_train_perspective.multiplayer"
        ));

        multiplayer.add(entryBuilder
                .startBooleanToggle(
                        new TranslatableComponent("option.create_train_perspective.multiplayer.apply_to_others"),
                        INSTANCE.applyToOthers)
                .setTooltip(new TranslatableComponent("option.create_train_perspective.multiplayer.apply_to_others.tooltip"))
                .setSaveConsumer(value -> INSTANCE.applyToOthers = value)
                .setDefaultValue(true)
                .build());

        general.addEntry(multiplayer.build());

        var advanced = entryBuilder.startSubCategory(new TranslatableComponent(
                "category.create_train_perspective.advanced"
        ));

        advanced.add(entryBuilder
                .startFloatField(
                        new TranslatableComponent("option.create_train_perspective.advanced.lean_magnitude"),
                        INSTANCE.leanMagnitude)
                .setTooltip(new TranslatableComponent("option.create_train_perspective.advanced.lean_magnitude.tooltip"))
                .setSaveConsumer(value -> INSTANCE.leanMagnitude = value)
                .setDefaultValue(1.0f)
                .build());

        advanced.add(entryBuilder
                .startFloatField(
                        new TranslatableComponent("option.create_train_perspective.advanced.roll_magnitude"),
                        INSTANCE.rollMagnitude)
                .setTooltip(new TranslatableComponent("option.create_train_perspective.advanced.roll_magnitude.tooltip"))
                .setSaveConsumer(value -> INSTANCE.rollMagnitude = value)
                .setDefaultValue(1.0f)
                .build());

        advanced.add(entryBuilder
                .startBooleanToggle(
                        new TranslatableComponent("option.create_train_perspective.advanced.apply_to_entities"),
                        INSTANCE.applyToNonPlayerEntities)
                .setTooltip(new TranslatableComponent("option.create_train_perspective.advanced.apply_to_entities.tooltip"))
                .setSaveConsumer(value -> INSTANCE.applyToNonPlayerEntities = value)
                .setDefaultValue(true)
                .build());

        advanced.add(entryBuilder
                .startStrList(
                        new TranslatableComponent("option.create_train_perspective.advanced.blocked_entities"),
                        INSTANCE.blockedEntities.stream().map(ResourceLocation::toString).toList())
                .setTooltip(new TranslatableComponent("option.create_train_perspective.advanced.blocked_entities.tooltip"))
                .setSaveConsumer(value -> INSTANCE.blockedEntities = value.stream().map(ResourceLocation::new).toList())
                .setDefaultValue(new ArrayList<>())
                .build());

        var debug = entryBuilder.startSubCategory(new TranslatableComponent("category.create_train_perspective.debug"));

        debug.add(entryBuilder
                .startTextDescription(new TranslatableComponent("category.create_train_perspective.debug.description").withStyle(ChatFormatting.BOLD))
                .build());

        debug.add(entryBuilder
                .startBooleanToggle(
                        new TranslatableComponent("option.create_train_perspective.debug.standing_transforms"),
                        INSTANCE.dbgShowStandingTransforms)
                .setSaveConsumer(value -> INSTANCE.dbgShowStandingTransforms = value)
                .setTooltip(new TranslatableComponent("option.create_train_perspective.debug.standing_transforms.tooltip"))
                .setDefaultValue(false)
                .build());


        debug.add(entryBuilder
                .startBooleanToggle(
                        Component.translatable("option.create_train_perspective.debug.value_scales"),
                        INSTANCE.dbgShowValueScales)
                .setSaveConsumer(value -> INSTANCE.dbgShowValueScales = value)
                .setTooltip(Component.translatable("option.create_train_perspective.debug.value_scales.tooltip"))
                .setDefaultValue(false)
                .build());

        advanced.add(debug.build());
        general.addEntry(advanced.build());

        return builder.build();
    }

    private void save() {
        try (var file = Files.newBufferedWriter(PATH)) {
            GSON.toJson(this, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
