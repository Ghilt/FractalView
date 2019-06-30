package se.admdev.fractalviewer.ancestorconfig.adapter

import android.graphics.Path
import android.os.AsyncTask
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.canvas.CellularFractalArtist
import se.admdev.fractalviewer.canvas.model.FractalGenerator

class AncestorCoreMiniature(
    miniatureSize: Int,
    val core: AncestorCore,
    notifyAdapterOnMiniatureDone: () -> Unit
) {

    var miniatureData: List<Path>? = null

    init {
        val listener: (List<Path>) -> Unit = {
            miniatureData = it
            notifyAdapterOnMiniatureDone.invoke()
        }
        CreateFractalMiniatureTask(miniatureSize, core, listener).execute()
    }

    private class CreateFractalMiniatureTask internal constructor(
        val miniatureSize: Int,
        val core: AncestorCore,
        val listener: (List<Path>) -> Unit
    ) : AsyncTask<Void, Void, List<Path>>() {

        override fun doInBackground(vararg params: Void): List<Path> {
            val generator = FractalGenerator(core)
            repeat(miniatureSize) { generator.generateNextIteration() }
            val artist = CellularFractalArtist()
            return generator.iterateOver { itr, value -> artist.getIterationAsPaths(itr, value) }
        }

        override fun onPostExecute(result: List<Path>) {
            listener.invoke(result)
        }
    }
}