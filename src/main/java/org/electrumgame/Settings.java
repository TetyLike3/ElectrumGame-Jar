package org.electrumgame;

public class Settings {
    public static class RendererSettings {
        public record WindowSettings(int width, int height, String title) {}
        public WindowSettings windowSettings = new WindowSettings(1280, 720, "ElectrumGame");
    }
    public static RendererSettings rendererSettings = new RendererSettings();
}
