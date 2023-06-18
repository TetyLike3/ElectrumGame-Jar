package org.electrumgame;

import static org.lwjgl.vulkan.VK10.*;

public class Settings {
    public static class RendererSettings {
        public record WindowSettings(int width, int height, String title) {}
        public WindowSettings windowSettings = new WindowSettings(
                1280,
                720,
                "ElectrumGame"
        );

        public record ApplicationSettings(String name, int engineVersion, int applicationVersion, int apiVersion) {}
        public ApplicationSettings applicationSettings = new ApplicationSettings(
                "ElectrumGame",
                VK_MAKE_API_VERSION(1,0,1,0),
                VK_MAKE_API_VERSION(1,0,1,0),
                VK_API_VERSION_1_0
        );
    }
    public static RendererSettings rendererSettings = new RendererSettings();
}
