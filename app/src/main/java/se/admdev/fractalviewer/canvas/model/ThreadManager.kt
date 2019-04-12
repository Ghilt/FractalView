package se.admdev.fractalviewer.canvas.model

import android.graphics.Path
import se.admdev.fractalviewer.canvas.CellularFractalArtist
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadManager(
    private val generator: FractalGenerator,
    listener: ((List<Path>) -> Unit) -> Unit
) {
    private val artist = CellularFractalArtist()
    private val threadPool = Executors.newScheduledThreadPool(5) as ScheduledThreadPoolExecutor

    private val periodicTask = Runnable {
        try {
            generator.generateNextIteration()
            val update = artist.getIterationAsPathUpdate(generator.iterationsCompleted - 1, generator.getLastIteration())
            listener(update)
        } catch (e: Exception) {

        }
    }

    private var future: ScheduledFuture<*>? = null

    fun toggleGenerationThread() {
        if (future?.isCancelled == false) {
            future?.cancel(false)
            future = null
        } else {
            future = threadPool.scheduleAtFixedRate(periodicTask, 10, 10, TimeUnit.MILLISECONDS)
        }
    }

    fun stopWork() {
        future?.cancel(false)
        future = null
        threadPool.shutdown()
    }
}