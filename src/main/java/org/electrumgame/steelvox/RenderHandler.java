package org.electrumgame.steelvox;

import org.electrumgame.Settings;
import org.electrumgame.steelvox.graphics.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;


public class RenderHandler {

    private static Window window;
    private static VkInstance instance;

    public static void run() {
        System.out.println("Initialising SteelVox rendering engine...");

        window = new Window(Settings.rendererSettings.windowSettings);
        initVulkan();

        System.out.println("Initialisation complete!");

        mainLoop();
        cleanup();

        System.out.println("SteelVox renderer terminated.");
    }

    private static void initVulkan() {
        System.out.println("Initialising Vulkan...");

        createInstance();
    }

    private static void mainLoop() {
        window.mainLoop();
    }

    private static void cleanup() {
        System.out.println("Destroying Vulkan instance...");
        vkDestroyInstance(instance, null);

        System.out.println("Cleaning up window...");
        window.cleanup();
    }


    private static void createInstance() {
        System.out.println("Creating Vulkan instance...");

        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(stack.UTF8("ElectrumGame"))
                .applicationVersion(Settings.rendererSettings.applicationSettings.applicationVersion())
                .pEngineName(stack.UTF8("SteelVox"))
                .engineVersion(Settings.rendererSettings.applicationSettings.engineVersion())
                .apiVersion(Settings.rendererSettings.applicationSettings.apiVersion());

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(glfwGetRequiredInstanceExtensions())
                .ppEnabledLayerNames(null);

            PointerBuffer instancePtr = stack.mallocPointer(1);
            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create Vulkan instance!");
            }

            instance = new VkInstance(instancePtr.get(0), createInfo);
        }
    }
}
