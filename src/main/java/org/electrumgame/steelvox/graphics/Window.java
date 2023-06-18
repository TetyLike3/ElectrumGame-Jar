package org.electrumgame.steelvox.graphics;

import org.electrumgame.Settings;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public Window(Settings.RendererSettings.WindowSettings windowSettings) {
        initWindow(windowSettings);
    }

    public void mainLoop() {
        while (!glfwWindowShouldClose(windowHandle)) {
            glfwPollEvents();
        }
    }

    private long windowHandle;

    private void initWindow(Settings.RendererSettings.WindowSettings windowSettings) {
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialise GLFW!");
        }

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        int width = windowSettings.width();
        int height = windowSettings.height();
        String title = windowSettings.title();

        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);

        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window!");
        }
    }

    public void cleanup() {
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }
}
