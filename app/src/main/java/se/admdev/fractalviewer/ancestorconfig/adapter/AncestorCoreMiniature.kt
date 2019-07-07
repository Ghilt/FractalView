package se.admdev.fractalviewer.ancestorconfig.adapter

import android.graphics.Path
import android.os.AsyncTask
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.canvas.CellularFractalArtist
import se.admdev.fractalviewer.canvas.model.FractalPyramidGenerator

class AncestorCoreMiniature(
    miniatureSize: Int,
    val core: AncestorCore,
    notifyOnMiniatureDone: (List<Path>?) -> Unit
) {

    var miniatureData: List<Path>? = null

    init {
        val listener: (List<Path>) -> Unit = {
            miniatureData = it
            notifyOnMiniatureDone.invoke(it)
        }
        CreateFractalMiniatureTask(miniatureSize, core, listener).execute()
    }

    private class CreateFractalMiniatureTask internal constructor(
        val miniatureSize: Int,
        val core: AncestorCore,
        val listener: (List<Path>) -> Unit
    ) : AsyncTask<Void, Void, List<Path>>() {

        override fun doInBackground(vararg params: Void): List<Path> {
            val generator = FractalPyramidGenerator(core)
            repeat(miniatureSize) { generator.generateNextIteration() }
            val artist = CellularFractalArtist()
            return generator.iterateOver { value -> artist.getIterationAsPaths(value) }
        }

        override fun onPostExecute(result: List<Path>) {
            listener.invoke(result)
        }
    }
}