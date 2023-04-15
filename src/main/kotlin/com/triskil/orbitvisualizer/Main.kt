package com.triskil.orbitvisualizer

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

fun main(args: Array<String>) {
    val config = LwjglApplicationConfiguration().apply {
        title = "Orbit Visualizer"
        width = 1280
        height = 720
        resizable = true
    }
    LwjglApplication(OrbitVisualizerApp(), config)
}