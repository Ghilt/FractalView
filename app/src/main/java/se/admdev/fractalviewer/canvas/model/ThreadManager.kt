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
    listener: (Path) -> Unit,
    pausedListener: () -> Unit
) {

    var pauseAfterReachingIteration = -1
    private val artist = CellularFractalArtist()
    private var threadPool = Executors.newScheduledThreadPool(5) as ScheduledThreadPoolExecutor

    private val periodicTask = Runnable {
        try {
            if (generator.iterationsCompleted == pauseAfterReachingIteration){
                // Did have some race-condition bug here, it seem to have gone away #famous_last_words
                pausedListener()
                pauseAfterReachingIteration = -1
            } else {
                generator.generateNextIteration()
                val update = artist.getIterationAsPaths(generator.iterationsCompleted - 1, generator.getLastIteration())
                listener(update)
            }

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
        Log.d("ThreadManager", "Shutting down fractal thread manager: $this, $threadPool")
    }

    fun isRunning() = future != null

    fun clearGenerationPauseIteration() {
        pauseAfterReachingIteration = -1
    }

    fun refreshThreadPoolIfNeeded() {
        if (threadPool.isTerminated) {
            threadPool = Executors.newScheduledThreadPool(5) as ScheduledThreadPoolExecutor
        }
    }

    companion object {
        private const val DELAY_GENERATION_MS = 10L
    }
}