package org.electrumgame.steelvox;

import org.electrumgame.Settings;
import org.electrumgame.steelvox.graphics.*;



public class RenderHandler {

    private static Window window;
    public static void run(Settings.RendererSettings rendererSettings) {
        System.out.println("Initialising SteelVox rendering engine...");

        window = new Window(rendererSettings.windowSettings);
        initVulkan();
        mainLoop();
        cleanup();

        System.out.println("Initialisation complete!");
    }

    private static void initVulkan() {

    }

    private static void mainLoop() {
        window.mainLoop();
    }

    private static void cleanup() {

    }
}
