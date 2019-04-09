package se.admdev.fractalviewer.canvas.model

import se.admdev.fractalviewer.canvas.CellularFractalArtist
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadManager(
    private val generator: FractalGenerator,
    listener: (Int, List<Cell>) -> Unit
) {
//    private val artist = CellularFractalArtist()
    private val threadPool = Executors.newScheduledThreadPool(5) as ScheduledThreadPoolExecutor

    private val periodicTask = Runnable {
        try {
            generator.generateNextIteration()
            listener(generator.iterationsCompleted - 1, generator.getLastIteration())
        } catch (e: Exception) {

        }
    }

    private var future: ScheduledFuture<*>? = null

    fun toggleGenerationThread() {
        if (future?.isCancelled == false) {
            future?.cancel(false)
            future = null
        } else {
            future = threadPool.scheduleAtFixedRate(periodicTask, 1, 1, TimeUnit.MICROSECONDS)
        }
    }

    fun stopWork() {
        future?.cancel(false)
        future = null
        threadPool.shutdown()
    }
}