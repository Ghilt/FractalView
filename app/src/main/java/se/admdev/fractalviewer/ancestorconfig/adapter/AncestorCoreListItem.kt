package se.admdev.fractalviewer.ancestorconfig.adapter

import android.graphics.Path
import android.os.AsyncTask
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.canvas.CellularFractalArtist
import se.admdev.fractalviewer.canvas.model.FractalGenerator

class AncestorCoreListItem(
    val core: AncestorCore,
    notifyAdapterOnMiniatureDone: () -> Unit
) {

    var miniatureData: List<List<Path>>? = null

    init {
        val listener: (List<List<Path>>) -> Unit = {
            miniatureData = it
            notifyAdapterOnMiniatureDone.invoke()
        }
        CreateFractalMiniatureTask(core, listener).execute()
    }

    private class CreateFractalMiniatureTask internal constructor(
        val core: AncestorCore,
        val listener: (List<List<Path>>) -> Unit
    ) : AsyncTask<Void, Void, List<List<Path>>>() {

        override fun doInBackground(vararg params: Void): List<List<Path>> {
            val generator = FractalGenerator(core)
            repeat(ITERATIONS_MINIATURE) { generator.generateNextIteration() }
            val artist = CellularFractalArtist()
            return generator.iterateOver { itr, value -> artist.getIterationAsPaths(itr, value) }
        }

        override fun onPostExecute(result: List<List<Path>>) {
            listener.invoke(result)
        }
    }

    companion object {
        private const val ITERATIONS_MINIATURE = 10
    }
}