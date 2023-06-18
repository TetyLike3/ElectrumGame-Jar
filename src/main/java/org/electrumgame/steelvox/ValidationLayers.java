package org.electrumgame.steelvox;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.lwjgl.system.Configuration.DEBUG;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;


// State enum
enum ValidationLayerState {
    ENABLED,
    DISABLED,
    NOT_PRESENT
}

public class ValidationLayers {

    // Variables
    private static final boolean ENABLE_VALIDATION_LAYERS = DEBUG.get(true);
    private static ValidationLayerState validationLayerState = ValidationLayerState.DISABLED;
    private long debugMessenger;

    private static final Set<String> VALIDATION_LAYERS;
    static {
        if (ENABLE_VALIDATION_LAYERS) {
            VALIDATION_LAYERS = new HashSet<>();
            VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_validation");
        } else {
            VALIDATION_LAYERS = null;
        }
    }

    // Get/set methods
    public boolean isEnabled() {
        return ENABLE_VALIDATION_LAYERS;
    }

    public ValidationLayerState getValidationLayerState() {

        // Check for presence
        if (ENABLE_VALIDATION_LAYERS && !checkValidationLayerSupport()) {
            validationLayerState = ValidationLayerState.NOT_PRESENT;
        } else if (ENABLE_VALIDATION_LAYERS) {
            validationLayerState = ValidationLayerState.ENABLED;
        } else {
            validationLayerState = ValidationLayerState.DISABLED;
        }
        return validationLayerState;
    }



    // Public methods
    public void setupDebugMessenger(VkInstance instance) {
        if (!ENABLE_VALIDATION_LAYERS) return;

        try (MemoryStack stack = stackPush()) {

            VkDebugUtilsMessengerCreateInfoEXT createInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
            populateDebugMessengerCreateInfo(createInfo);

            LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);

            if (createDebugUtilsMessengerEXT(instance, createInfo, pDebugMessenger) != VK_SUCCESS) {
                throw new RuntimeException("Failed to set up debug messenger!");
            }

            debugMessenger = pDebugMessenger.get(0);
            validationLayerState = ValidationLayerState.ENABLED;
        }
    }

    public void cleanup(VkInstance instance) {
        if (ENABLE_VALIDATION_LAYERS) {
            destroyDebugUtilsMessengerEXT(instance, debugMessenger);
        }
    }



    public PointerBuffer validationLayersAsPointerBuffer(MemoryStack stack) {
        assert VALIDATION_LAYERS != null;
        PointerBuffer validationLayersBuffer = stack.mallocPointer(VALIDATION_LAYERS.size());

        VALIDATION_LAYERS.stream()
                .map(stack::UTF8)
                .forEach(validationLayersBuffer::put);

        return validationLayersBuffer.rewind();
    }

    public boolean checkValidationLayerSupport() {

        try (MemoryStack stack = stackPush()) {
            IntBuffer layerCount = stack.ints(0);

            vkEnumerateInstanceLayerProperties(
                    layerCount,
                    null
            );

            VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(layerCount.get(0), stack);

            vkEnumerateInstanceLayerProperties(
                    layerCount,
                    availableLayers
            );

            Set<String> availableLayerNames = availableLayers.stream()
                    .map(VkLayerProperties::layerNameString)
                    .collect(toSet());

            assert VALIDATION_LAYERS != null;
            return availableLayerNames.containsAll(VALIDATION_LAYERS);
        }
    }

    public void populateDebugMessengerCreateInfo(VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo) {
        debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
        debugCreateInfo.messageSeverity(
                VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT |
                        VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT |
                        VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT
        );
        debugCreateInfo.messageType(
                VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
                        VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
                        VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT
        );
        debugCreateInfo.pfnUserCallback(ValidationLayers::debugCallback);
    }




    // Private methods
    private static int debugCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
        System.err.println("Validation layers: " + callbackData.pMessageString());

        return VK_FALSE;
    }

    private static int createDebugUtilsMessengerEXT(VkInstance instance, VkDebugUtilsMessengerCreateInfoEXT pCreateInfo, LongBuffer pDebugMessenger) {
        if (vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != NULL) {
            return vkCreateDebugUtilsMessengerEXT(instance, pCreateInfo, null, pDebugMessenger);
        }

        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }

    private static void destroyDebugUtilsMessengerEXT(VkInstance instance, long debugMessenger) {
        if (vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT") != NULL) {
            vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, null);
        }
    }

}
