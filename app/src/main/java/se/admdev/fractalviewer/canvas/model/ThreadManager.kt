package se.admdev.fractalviewer.canvas.model

import android.graphics.Path
import android.util.Log
import se.admdev.fractalviewer.canvas.CellularFractalArtist
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadManager(
    private val generator: FractalGenerator,
    listener: (List<Path>) -> Unit
) {

    private val artist = CellularFractalArtist()
    private val threadPool = Executors.newScheduledThreadPool(5) as ScheduledThreadPoolExecutor

    private val periodicTask = Runnable {
        try {
            generator.generateNextIteration()
            val update = artist.getIterationAsPaths(generator.iterationsCompleted - 1, generator.getLastIteration())
            listener(update)
        } catch (e: Exception) {
            Log.d("ThreadManager", e.message)
        }
    }

    private var future: ScheduledFuture<*>? = null

    fun toggleGenerationThread() {
        if (future?.isCancelled == false) {
            future?.cancel(false)
            future = null
        } else {
            future = threadPool.scheduleAtFixedRate(
                periodicTask,
                DELAY_GENERATION_MS,
                DELAY_GENERATION_MS,
                TimeUnit.MILLISECONDS
            )
        }
    }

    fun stopWork() {
        future?.cancel(false)
        future = null
        threadPool.shutdown()
    }

    fun isRunning() = future != null

    companion object {
        private const val DELAY_GENERATION_MS = 10L
    }
}