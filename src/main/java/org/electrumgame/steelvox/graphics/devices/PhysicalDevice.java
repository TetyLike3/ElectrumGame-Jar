package org.electrumgame.steelvox.graphics.devices;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import java.nio.IntBuffer;
import java.util.stream.IntStream;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class PhysicalDevice {
    private VkPhysicalDevice physicalDevice;

    public PhysicalDevice(VkInstance instance) {
        pickPhysicalDevice(instance);
    }

    private void pickPhysicalDevice(VkInstance instance) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(instance, deviceCount, null);

            if (deviceCount.get(0) == 0) {
                throw new RuntimeException("Failed to find GPUs with Vulkan support!");
            }

            PointerBuffer pPhysicalDevices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, deviceCount, pPhysicalDevices);
            for (int i = 0; i < pPhysicalDevices.capacity(); i++) {
                VkPhysicalDevice physicalDevice = new VkPhysicalDevice(pPhysicalDevices.get(i), instance);
                if (isDeviceSuitable(physicalDevice)) {
                    this.physicalDevice = physicalDevice;
                    return;
                }
            }

            throw new RuntimeException("Failed to find a suitable GPU!");
        }
    }

    private boolean isDeviceSuitable(VkPhysicalDevice candidateDevice) {
        QueueFamilyIndices indices = findQueueFamilyIndices(candidateDevice);

        return indices.isComplete();
    }

    private QueueFamilyIndices findQueueFamilyIndices(VkPhysicalDevice device) {
        QueueFamilyIndices indices = new QueueFamilyIndices();

        try (MemoryStack stack = stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);

            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

            IntStream.range(0, queueFamilies.capacity())
                    .filter(i -> (queueFamilies.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0)
                    .findFirst()
                    .ifPresent(i -> indices.graphicsFamily = i);
        }

        return indices;
    }
}
