package com.triskil.orbitvisualizer

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import kotlin.random.Random
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController


class OrbitVisualizerApp : ApplicationAdapter() {
    private lateinit var camera: PerspectiveCamera
    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment
    private lateinit var planeInstance: ModelInstance
    private lateinit var objectInstances: MutableList<ModelInstance>
    private lateinit var camController: CameraInputController

    override fun create() {
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
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
        val rotationRange = 45f
        val randomRotation = Matrix4().rotate(Vector3.X, Random.nextFloat() * rotationRange - rotationRange / 2)
            .rotate(Vector3.Y, Random.nextFloat() * rotationRange - rotationRange / 2)
            .rotate(Vector3.Z, Random.nextFloat() * rotationRange - rotationRange / 2)
        planeInstance.transform.set(randomRotation)

        objectInstances = mutableListOf()
        val planeRadius = 5f
        for (i in 0 until 10) {
            val objectModel = createObjectModel()
            val objectInstance = ModelInstance(objectModel)

            val randomAngle = Random.nextDouble() * 2 * Math.PI
            val randomDistance = Random.nextDouble() * planeRadius
            val x = (randomDistance * Math.cos(randomAngle)).toFloat()
            val y = (randomDistance * Math.sin(randomAngle)).toFloat()
            val z = 0f

            objectInstance.transform.setTranslation(x, y, z)
            objectInstance.transform.mul(randomRotation)
            objectInstances.add(objectInstance)
        }

        camController = CameraInputController(camera)
        Gdx.input.inputProcessor = camController
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        modelBatch.render(planeInstance, environment)
        objectInstances.forEach { modelBatch.render(it, environment) }
        modelBatch.end()
    }


//
//    override fun create() {
//        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
//        camera.position.set(10f, 10f, 10f)
//        camera.lookAt(0f, 0f, 0f)
//        camera.near = 1f
//        camera.far = 1000f
//        camera.update()
//
//        modelBatch = ModelBatch()
//
//        environment = Environment()
//        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
//
//        val planeModel = createPlaneModel(5f, 32)
//        planeInstance = ModelInstance(planeModel)
//        val rotationRange = 45f
//        val randomRotation = Matrix4().rotate(Vector3.X, Random.nextFloat() * rotationRange - rotationRange / 2)
//            .rotate(Vector3.Y, Random.nextFloat() * rotationRange - rotationRange / 2)
//            .rotate(Vector3.Z, Random.nextFloat() * rotationRange - rotationRange / 2)
//        planeInstance.transform.set(randomRotation)
//
//        objectInstances = mutableListOf()
//        val planeRadius = 5f
//        for (i in 0 until 10) {
//            val objectModel = createObjectModel()
//            val objectInstance = ModelInstance(objectModel)
//
//            val randomAngle = Random.nextDouble() * 2 * Math.PI
//            val randomDistance = Random.nextDouble() * planeRadius
//            val x = (randomDistance * Math.cos(randomAngle)).toFloat()
//            val y = (randomDistance * Math.sin(randomAngle)).toFloat()
//            val z = 0f
//
//            objectInstance.transform.setTranslation(x, y, z)
//            objectInstance.transform.mul(randomRotation)
//            objectInstances.add(objectInstance)
//        }
//    }

    override fun dispose() {
        modelBatch.dispose()
    }

    private fun createPlaneModel(radius: Float, segments: Int): Model {
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val material = Material()
        material.set(ColorAttribute.createDiffuse(Color.BLUE))
        val node = modelBuilder.node()
        node.translation.set(0f, 0f, 0f)
        node.scale.set(1f, 0.001f, 1f)
        modelBuilder.part("cylinder", GL20.GL_TRIANGLES, Usage.Position.toLong() or Usage.Normal.toLong(), material)
            .cylinder(radius * 2, 0.001f, radius * 2, segments)
        return modelBuilder.end()
    }






// Mostly working plane
//    private fun createPlaneModel(radius: Float, segments: Int): Model {
//        val vertexSize = 3 + 3
//        val numVertices = (segments + 1) * 2
//        val indices = ShortArray(segments * 3)
//        val vertices = FloatArray(numVertices * vertexSize)
//        val angle = 2 * Math.PI / segments
//
//        for (i in 0 until segments) {
//            val x = radius * Math.cos(i * angle).toFloat()
//            val z = radius * Math.sin(i * angle).toFloat()
//
//            val normal = Vector3(x, 0f, z).nor()
//
//            vertices[i * vertexSize * 2] = x
//            vertices[i * vertexSize * 2 + 1] = 0f
//            vertices[i * vertexSize * 2 + 2] = z
//            vertices[i * vertexSize * 2 + 3] = normal.x
//            vertices[i * vertexSize * 2 + 4] = normal.y
//            vertices[i * vertexSize * 2 + 5] = normal.z
//
//            vertices[i * vertexSize * 2 + 6] = 0f
//            vertices[i * vertexSize * 2 + 7] = 0f
//            vertices[i * vertexSize * 2 + 8] = 0f
//            vertices[i * vertexSize * 2 + 9] = normal.x
//            vertices[i * vertexSize * 2 + 10] = normal.y
//            vertices[i * vertexSize * 2 + 11] = normal.z
//
//            if (i < segments - 1) {
//                indices[i * 3] = (i * 2).toShort()
//                indices[i * 3 + 1] = (i * 2 + 1).toShort()
//                indices[i * 3 + 2] = (i * 2 + 2).toShort()
//            }
//        }
//
//        val mesh = Mesh(true, numVertices, indices.size,
//            VertexAttribute(Usage.Position, 3, "a_position"),
//            VertexAttribute(Usage.Normal, 3, "a_normal")
//        )
//        mesh.setVertices(vertices)
//        mesh.setIndices(indices)
//        val material = Material()
//        material.set(ColorAttribute.createDiffuse(Color.BLUE))
//        material.set(IntAttribute(IntAttribute.CullFace, 0)) // Disable backface culling
//
//        val modelBuilder = ModelBuilder()
//        modelBuilder.begin()
//        modelBuilder.part("planePart", mesh, GL20.GL_TRIANGLES, material)
//        return modelBuilder.end()
//    }


//
//    private fun createPlaneModel(radius: Float, segments: Int): Model {
//        val vertexSize = 3 + 3
//        val numVertices = segments * 2
//        val vertices = FloatArray(numVertices * vertexSize)
//        val angle = 2 * Math.PI / segments
//
//        for (i in 0 until segments) {
//            val x = radius * Math.cos(i * angle).toFloat()
//            val z = radius * Math.sin(i * angle).toFloat()
//
//            val normal = Vector3(x, 0f, z).nor()
//
//            vertices[i * vertexSize * 2] = x
//            vertices[i * vertexSize * 2 + 1] = 0f
//            vertices[i * vertexSize * 2 + 2] = z
//            vertices[i * vertexSize * 2 + 3] = normal.x
//            vertices[i * vertexSize * 2 + 4] = normal.y
//            vertices[i * vertexSize * 2 + 5] = normal.z
//
//            vertices[i * vertexSize * 2 + 6] = 0f
//            vertices[i * vertexSize * 2 + 7] = 0f
//            vertices[i * vertexSize * 2 + 8] = 0f
//            vertices[i * vertexSize * 2 + 9] = normal.x
//            vertices[i * vertexSize * 2 + 10] = normal.y
//            vertices[i * vertexSize * 2 + 11] = normal.z
//        }
//
//        val mesh = Mesh(true, numVertices, 0,
//            VertexAttribute(Usage.Position, 3, "a_position"),
//            VertexAttribute(Usage.Normal, 3, "a_normal")
//        )
//        mesh.setVertices(vertices)
//
//        val material = Material()
//        material.set(ColorAttribute.createDiffuse(Color.BLUE))
//
//        val modelBuilder = ModelBuilder()
//        modelBuilder.begin()
//        modelBuilder.part("planePart", mesh, GL20.GL_TRIANGLE_STRIP, material)
//        return modelBuilder.end()
//    }


//
//    private fun createPlaneModel(radius: Float, segments: Int): Model {
//        val vertexSize = 3 + 3
//        val numVertices = segments * 2
//        val vertices = FloatArray(numVertices * vertexSize)
//        val angle = 2 * Math.PI / segments
//
//        for (i in 0 until segments) {
//            val x = radius * Math.cos(i * angle).toFloat()
//            val z = radius * Math.sin(i * angle).toFloat()
//
//            val normal = Vector3(x, 0f, z).nor()
//
//            vertices[i * vertexSize * 2] = x
//            vertices[i * vertexSize * 2 + 1] = 0f
//            vertices[i * vertexSize * 2 + 2] = z
//            vertices[i * vertexSize * 2 + 3] = normal.x
//            vertices[i * vertexSize * 2 + 4] = normal.y
//            vertices[i * vertexSize * 2 + 5] = normal.z
//
//            vertices[i * vertexSize * 2 + 6] = 0f
//            vertices[i * vertexSize * 2 + 7] = 0f
//            vertices[i * vertexSize * 2 + 8] = 0f
//            vertices[i * vertexSize * 2 + 9] = normal.x
//            vertices[i * vertexSize * 2 + 10] = normal.y
//            vertices[i * vertexSize * 2 + 11] = normal.z
//        }
//
//        val mesh = Mesh(true, numVertices, 0,
//            VertexAttribute(Usage.Position, 3, "a_position"),
//            VertexAttribute(Usage.Normal, 3, "a_normal")
//        )
//        mesh.setVertices(vertices)
//
//        val material = Material()
//
//        val modelBuilder = ModelBuilder()
//        modelBuilder.begin()
//        modelBuilder.part("planePart", mesh, GL20.GL_TRIANGLE_STRIP, material)
//        return modelBuilder.end()
//    }

    private fun createObjectModel(): Model {
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        val material = Material(ColorAttribute.createDiffuse(Color.GREEN))
        val boxBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes, material)
        boxBuilder.box(0f, 0f, 0f, 0.25f, 0.25f, 0.25f)
        return modelBuilder.end()
    }
}
