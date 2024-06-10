package net.derfruhling.minecraft.create.trainperspective;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.*;

public class ModConfig {
    private ModConfig() {}
    
    public boolean enabled = true;
    public boolean leanEnabled = true;
    public float leanMagnitude = 1.0f;
    public boolean rollEnabled = true;
    public float rollMagnitude = 1.0f;
    public boolean applyToOthers = true;
    public boolean applyToNonPlayerEntities = false;
    public boolean dbgShowStandingTransforms = false;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final WatchService WATCH_SERVICE;
    private static final Path PATH = Minecraft.getInstance().gameDirectory.toPath()
        .resolve("config")
        .resolve("create-train-perspective.json");

    static {
        try {
            WATCH_SERVICE = FileSystems.getDefault().newWatchService();
            PATH.getParent().register(WATCH_SERVICE, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void tick() {
        var key = WATCH_SERVICE.poll();

        if(key != null) {
            for(var event : key.pollEvents()) {
                if(event.context().toString().equals(PATH.getFileName().toString())) {
                    INSTANCE = loadConfig();
                    key.reset();
                }
            }
        }
    }

    public static ModConfig INSTANCE = loadConfig();

    private static ModConfig loadConfig() {
        if(Files.exists(PATH)) {
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

    private void save() {
        try(var file = Files.newBufferedWriter(PATH)) {
            GSON.toJson(this, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Screen createConfigScreen(Screen parent) {
        var builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("title.create_train_perspective.config"))
            .setSavingRunnable(INSTANCE::save);

        var general = builder.getOrCreateCategory(Component.translatable(
                "category.create_train_perspective.general"
        ));

        var entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
            .startBooleanToggle(
                    Component.translatable("option.create_train_perspective.enabled"),
                    INSTANCE.enabled)
            .setTooltip(Component.translatable("option.create_train_perspective.enabled.tooltip"))
            .setSaveConsumer(value -> INSTANCE.enabled = value)
            .setDefaultValue(true)
            .build());

        var leaning = entryBuilder.startSubCategory(Component.translatable(
                "category.create_train_perspective.leaning"
        ));

        leaning.add(entryBuilder
            .startBooleanToggle(
                    Component.translatable("option.create_train_perspective.leaning.enabled"),
                    INSTANCE.leanEnabled)
            .setTooltip(Component.translatable("option.create_train_perspective.leaning.enabled.tooltip"))
            .setSaveConsumer(value -> INSTANCE.leanEnabled = value)
            .setDefaultValue(true)
            .build());

        leaning.add(entryBuilder
            .startBooleanToggle(
                    Component.translatable("option.create_train_perspective.leaning.roll_enabled"),
                    INSTANCE.rollEnabled)
            .setTooltip(Component.translatable("option.create_train_perspective.leaning.roll_enabled.tooltip"))
            .setSaveConsumer(value -> INSTANCE.rollEnabled = value)
            .setDefaultValue(true)
            .build());

        general.addEntry(leaning.build());

        var multiplayer = entryBuilder.startSubCategory(Component.translatable(
                "category.create_train_perspective.multiplayer"
        ));

        multiplayer.add(entryBuilder
            .startBooleanToggle(
                    Component.translatable("option.create_train_perspective.multiplayer.apply_to_others"),
                    INSTANCE.applyToOthers)
            .setTooltip(Component.translatable("option.create_train_perspective.multiplayer.apply_to_others.tooltip"))
            .setSaveConsumer(value -> INSTANCE.applyToOthers = value)
            .setDefaultValue(true)
            .build());

        general.addEntry(multiplayer.build());

        var advanced = entryBuilder.startSubCategory(Component.translatable(
                "category.create_train_perspective.advanced"
        ));

        advanced.add(entryBuilder
            .startFloatField(
                    Component.translatable("option.create_train_perspective.advanced.lean_magnitude"),
                    INSTANCE.leanMagnitude)
            .setTooltip(Component.translatable("option.create_train_perspective.advanced.lean_magnitude.tooltip"))
            .setSaveConsumer(value -> INSTANCE.leanMagnitude = value)
            .setDefaultValue(1.0f)
            .build());

        advanced.add(entryBuilder
            .startFloatField(
                    Component.translatable("option.create_train_perspective.advanced.roll_magnitude"),
                    INSTANCE.rollMagnitude)
            .setTooltip(Component.translatable("option.create_train_perspective.advanced.roll_magnitude.tooltip"))
            .setSaveConsumer(value -> INSTANCE.rollMagnitude = value)
            .setDefaultValue(1.0f)
            .build());
        
        advanced.add(entryBuilder
            .startBooleanToggle(
                    Component.translatable("option.create_train_perspective.multiplayer.apply_to_entities"),
                    INSTANCE.applyToNonPlayerEntities)
            .setTooltip(Component.translatable("option.create_train_perspective.multiplayer.apply_to_entities.tooltip"))
            .setSaveConsumer(value -> INSTANCE.applyToNonPlayerEntities = value)
            .setDefaultValue(true)
            .build());

        var debug = entryBuilder.startSubCategory(Component.translatable("category.create_train_perspective.debug"));

        debug.add(entryBuilder
                .startTextDescription(Component.translatable("category.create_train_perspective.debug.description").withStyle(ChatFormatting.BOLD))
                .build());

        debug.add(entryBuilder
                .startBooleanToggle(
                        Component.translatable("option.create_train_perspective.debug.standing_transforms"),
                        INSTANCE.dbgShowStandingTransforms)
                .setSaveConsumer(value -> INSTANCE.dbgShowStandingTransforms = value)
                .setTooltip(Component.translatable("option.create_train_perspective.debug.standing_transforms.tooltip"))
                .setDefaultValue(false)
                .build());

        advanced.add(debug.build());
        general.addEntry(advanced.build());

        return builder.build();
    }
}
