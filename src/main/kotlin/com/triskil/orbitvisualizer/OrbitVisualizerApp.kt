package com.triskil.orbitvisualizer

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import kotlin.random.Random

class OrbitVisualizerApp : ApplicationAdapter() {
    private lateinit var camera: PerspectiveCamera
    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment
    private lateinit var planeInstance: ModelInstance
    private lateinit var centerInstance: ModelInstance
    private lateinit var objectInstances: MutableList<ModelInstance>
    private lateinit var camController: CameraInputController

    override fun create() {
        camera = PerspectiveCamera(60f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(10f, 10f, 10f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 1f
        camera.far = 1000f
        camera.update()

        modelBatch = ModelBatch()

        environment = Environment()
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)
        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
        Gdx.gl.glCullFace(GL20.GL_NONE)
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))

        val planeModel = createPlaneModel(5f, 32)
        planeInstance = ModelInstance(planeModel)

        val centerObjRadius = 0.3f
        val centerModel = createObjectModel(0.3f, Color.WHITE)
        centerInstance = ModelInstance(centerModel)

        objectInstances = mutableListOf()
        val planeRadius = 5f
        val objectDrift = 0.5f
        val objectMinSize = 0.05f
        val objectMaxSize = 0.2f
        val minCenterDistance = centerObjRadius * 2.5f

        for (i in 0 until 10) {
            val objectModel = createObjectModel(Random.nextFloat() * (objectMaxSize - objectMinSize) + objectMinSize, Color.GREEN)
            val objectInstance = ModelInstance(objectModel)

            val randomAngle = Random.nextDouble() * 2 * Math.PI
            var randomDistance = Random.nextDouble() * planeRadius
            while(randomDistance < minCenterDistance) {
                randomDistance = Random.nextDouble() * planeRadius
            }

            val x = (randomDistance * Math.cos(randomAngle)).toFloat()
            val y = Random.nextFloat() * objectDrift - objectDrift / 2
            val z = (randomDistance * Math.sin(randomAngle)).toFloat()

            objectInstance.transform.setTranslation(x, y, z)
            objectInstances.add(objectInstance)
        }

        val rotationRange = 45f
        val randomRotation = Matrix4().rotate(Vector3.X, Random.nextFloat() * rotationRange - rotationRange / 2)
            .rotate(Vector3.Y, Random.nextFloat() * rotationRange - rotationRange / 2)
            .rotate(Vector3.Z, Random.nextFloat() * rotationRange - rotationRange / 2)

        // Apply the random rotation to the plane
        planeInstance.transform.set(randomRotation)

        // Apply the random rotation to the center object
        centerInstance.transform.set(randomRotation)

        // Apply the random rotation to the boxes and then translate them to the new position
        objectInstances.forEach {
            val boxPosition = Vector3(it.transform.getTranslation(Vector3()))
            it.transform.mul(randomRotation)
            val rotatedBoxPosition = boxPosition.mul(randomRotation)
            it.transform.setTranslation(rotatedBoxPosition)
        }

        camController = CameraInputController(camera)
        Gdx.input.inputProcessor = camController
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        Gdx.gl.glEnable(GL20.GL_BLEND) // Enable blending
        modelBatch.render(planeInstance, environment)
        Gdx.gl.glDisable(GL20.GL_BLEND) // Disable blending
        modelBatch.render(centerInstance, environment)
        objectInstances.forEach { modelBatch.render(it, environment) }
        modelBatch.end()
    }

    override fun dispose() {
        modelBatch.dispose()
    }

    private fun createPlaneModel(radius: Float, segments: Int): Model {
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val material = Material()
        val semiTransparentBlue = Color(0f, 0f, 1f, 0.5f) // Set the color to semi-transparent blue
        material.set(ColorAttribute.createDiffuse(semiTransparentBlue))
        material.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)) // Enable blending
        val node = modelBuilder.node()
        node.translation.set(0f, 0f, 0f)
        node.scale.set(1f, 0.001f, 1f)
        modelBuilder.part("cylinder", GL20.GL_TRIANGLES, Usage.Position.toLong() or Usage.Normal.toLong(), material)
            .cylinder(radius * 2, 0.001f, radius * 2, segments)
        return modelBuilder.end()
    }

    private fun createObjectModel(radius: Float = 0.5f, color: Color = Color.GREEN): Model {
        val modelBuilder = ModelBuilder()
        val divisions = 16
        val material = Material(ColorAttribute.createDiffuse(color))
        val usageCode = Usage.Position.toLong() or Usage.Normal.toLong()

        modelBuilder.begin()
        modelBuilder.node().id = "sphere"
        modelBuilder.part("sphere", GL20.GL_TRIANGLES, usageCode, material).apply {
            SphereShapeBuilder.build(this, Matrix4(), radius, radius, radius, divisions, divisions)
        }

        return modelBuilder.end()
    }
}
