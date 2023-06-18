package org.electrumgame.steelvox;

import org.electrumgame.Settings;
import org.electrumgame.steelvox.graphics.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;


public class RenderHandler {

    private Window window;
    private VkInstance instance;
    private ValidationLayers validationLayers;

    public void run() {
        System.out.println("Initialising SteelVox rendering engine...");

        window = new Window(Settings.rendererSettings.windowSettings);
        initVulkan();

        System.out.println("Initialisation complete!");

        mainLoop();
        cleanup();

        System.out.println("SteelVox renderer terminated.");
    }

    private void initVulkan() {
        System.out.println("Initialising Vulkan...");

        createInstance();

        System.out.println("Initialising validation layers...");
        validationLayers.setupDebugMessenger(instance);
    }

    private void mainLoop() {
        window.mainLoop();
    }

    private void cleanup() {
        System.out.println("Cleaning up validation layers...");
        validationLayers.cleanup(instance);

        System.out.println("Destroying Vulkan instance...");
        vkDestroyInstance(instance, null);

        System.out.println("Cleaning up window...");
        window.cleanup();
    }


    private void createInstance() {
        validationLayers = new ValidationLayers();

        if (validationLayers.getValidationLayerState() == ValidationLayerState.NOT_PRESENT) {
            throw new RuntimeException("Validation layers requested, but not available!");
        }
        System.out.println("Validation layers are " + validationLayers.getValidationLayerState().toString());


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
                .ppEnabledExtensionNames(getRequiredExtensions(stack));

            if (validationLayers.isEnabled()) {
                createInfo.ppEnabledLayerNames(validationLayers.validationLayersAsPointerBuffer(stack));

                VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
                validationLayers.populateDebugMessengerCreateInfo(debugCreateInfo);
                createInfo.pNext(debugCreateInfo.address());
            }

            PointerBuffer instancePtr = stack.mallocPointer(1);
            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create Vulkan instance!");
            }

            instance = new VkInstance(instancePtr.get(0), createInfo);
        }
    }

    private PointerBuffer getRequiredExtensions(MemoryStack stack) {
        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

        if(validationLayers.isEnabled()) {
            assert glfwExtensions != null;
            PointerBuffer extensions = stack.mallocPointer(glfwExtensions.capacity() + 1);
            extensions.put(glfwExtensions);
            extensions.put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));

            return extensions.rewind();
        }

        return glfwExtensions;
    }
}
