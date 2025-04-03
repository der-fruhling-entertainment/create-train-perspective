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

package net.derfruhling.minecraft.create.trainperspective.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.derfruhling.minecraft.create.trainperspective.MixinUtil;
import net.derfruhling.minecraft.create.trainperspective.ModConfigScreenFactory;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CreateTrainPerspectiveMod.MODID)
public class ModForgeEntrypoint {
    public CreateTrainPerspectiveMod mod = new CreateTrainPerspectiveMod();

    public ModForgeEntrypoint() {
        //NeoForge.EVENT_BUS.addListener(this::onClientSetupEvent);
        if(ModList.get().isLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (container, screen) -> ModConfigScreenFactory.createConfigScreen(screen));
        }

        //var bus = ModLoadingContext.get().getActiveContainer().getEventBus();
        NeoForge.EVENT_BUS.addListener(ViewportEvent.ComputeCameraAngles.class, computeCameraAngles -> {
            var camera3d = MixinUtil.asCamera3D(Minecraft.getInstance().gameRenderer.getMainCamera());
            computeCameraAngles.setRoll(computeCameraAngles.getRoll() + camera3d.getZRot());
        });
    }

    /*private void onClientSetupEvent(FMLClientSetupEvent event) {
        EventBuses.registerModEventBus(CreateTrainPerspectiveMod.MODID, FMLJavaModLoadingContext.get().getModEventBus());
    }*/
}
