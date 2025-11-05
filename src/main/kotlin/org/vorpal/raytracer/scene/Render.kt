package org.vorpal.raytracer.scene

import kotlinx.coroutines.*
import kotlin.math.min

object Render {

    /**
     * Java parallelStream-backed per-pixel iteration.
     * Runs [block] for every (x, y) in [0,width) × [0,height).
     */
    fun forEachPixelParallelStream(
        width: Int,
        height: Int,
        block: (x: Int, y: Int) -> Unit
    ) {
        (0 until height).forEach { y ->
            (0 until width).toList().parallelStream().forEach { x ->
                block(x, y)
            }
        }
    }

    fun forEachRowParallelStream(
        rows: Int,
        block: (yStart: Int, yEnd: Int) -> Unit
    ) {
        (0 until rows).toList().parallelStream().forEach { y ->
            block(y, y + 1)
        }
    }

    /**
     * Core coroutine primitive: runs [block] for chunks of rows [yStart, yEnd).
     * The block is responsible for doing whatever it wants over those rows.
     */
    fun parallelForEachRowChunk(
        height: Int,
        rowsPerChunk: Int = 8,
        block: (yStart: Int, yEnd: Int) -> Unit
    ) {
        runBlocking {
            val jobs = mutableListOf<Job>()
            var y = 0

            while (y < height) {
                val yStart = y
                val yEnd = min(y + rowsPerChunk, height)

                jobs += launch(Dispatchers.Default) {
                    block(yStart, yEnd)
                }

                y = yEnd
            }

            jobs.joinAll()
        }
    }

    /**
     * Coroutine-backed per-pixel iteration,
     * using row chunks internally.
     */
    fun parallelForEachPixel(
        width: Int,
        height: Int,
        rowsPerChunk: Int,
        block: (x: Int, y: Int) -> Unit
    ) {
        parallelForEachRowChunk(height, rowsPerChunk) { yStart, yEnd ->
            for (y in yStart until yEnd) {
                for (x in 0 until width) {
                    block(x, y)
                }
            }
        }
    }

    fun parallelForEachPixel(width: Int, height: Int, block: (x: Int, y: Int) -> Unit) =
        parallelForEachPixel(width, height, 8, block)
}