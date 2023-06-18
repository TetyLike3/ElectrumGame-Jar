package org.electrumgame.steelvox.graphics.devices;

public class QueueFamilyIndices {
    Integer graphicsFamily = null;
    Integer presentFamily = null;

    public boolean isComplete() {
        return graphicsFamily != null;
    }
}
