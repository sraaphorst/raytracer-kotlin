package apps

// By Sebastian Raaphorst, 2023.

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.vorpal.raytracer.material.Material
import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.pattern.CheckerPattern
import org.vorpal.raytracer.scene.Camera
import org.vorpal.raytracer.scene.World
import org.vorpal.raytracer.shapes.Cube
import org.vorpal.raytracer.shapes.Cylinder
import org.vorpal.raytracer.shapes.Group
import org.vorpal.raytracer.shapes.Sphere
import java.io.File
import kotlin.math.PI
import kotlin.math.min
import kotlin.system.measureTimeMillis

data class RenderJob(
    val name: String,
    val camera: Camera,
    val world: World,
    val outputFile: File
)

fun runJobsInParallel(jobs: List<RenderJob>) {
    val elapsed = measureTimeMillis {
        runBlocking {
            jobs.map { job ->
                async(Dispatchers.Default) {
                    println("Starting render: ${job.name}")
                    val canvas = job.camera.render(job.world)
                    job.outputFile.parentFile?.mkdirs()
                    canvas.toPPMFile(job.outputFile)
                    println("Finished render: ${job.name}")
                }
            }.awaitAll()
        }
    }
    println("Total time for ${jobs.size} images: ${elapsed / 1000.0} s")
}

// The shared side of a hexagon.
val side = run {
    val corner = Sphere(Matrix.translate(0, 0, -1) * Matrix.scale(0.25, 0.25, 0.25))
    val edge = Cylinder(
        0, 1, false,
        Matrix.translate(0, 0, -1) * Matrix.rotateY(-PI / 6)
                * Matrix.rotateZ(-PI / 2) * Matrix.scale(0.25, 1, 0.25)
    )
    Group(listOf(corner, edge))
}

val room = run {
    val t = Matrix.scale(6, 4, 8)
    val p = CheckerPattern(Color(0.9, 0.4, 0.4), Color.WHITE, Matrix.scale(0.5, 0.5, 0.5))
    val m = Material(p, specular = 0)
    Cube(t, m)
}