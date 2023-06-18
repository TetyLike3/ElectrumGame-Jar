package org.electrumgame;

// This will be a minecraft clone with a more industrial taste, and improved graphics.
// It will be written in Java, and use Vulkan for rendering.

import org.electrumgame.steelvox.RenderHandler;

import java.io.IOException;

public class Main {
    // Settings struct

    public static void main(String[] args) {
        System.out.println("Starting Electrum...");

        RenderHandler.run(Settings.rendererSettings);
    }
}