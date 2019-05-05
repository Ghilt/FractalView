package se.admdev.fractalviewer.canvas

import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_fractal_canvas.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.ancestorconfig.saveAncestorCore
import se.admdev.fractalviewer.canvas.model.FractalGenerator
import se.admdev.fractalviewer.canvas.model.ThreadManager
import se.admdev.fractalviewer.setGone

class FractalCanvasFragment : Fragment() {

    lateinit var ancestorCore: AncestorCore
    lateinit var generator: FractalGenerator
    private lateinit var workManager: ThreadManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fractal_canvas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val core = FractalCanvasFragmentArgs.fromBundle(it).ancestorCore
            ancestorCore = core ?: throw IllegalArgumentException("Error: No ancestor core for FractalCanvasFragment")

            generator = FractalGenerator(ancestorCore)
            workManager = ThreadManager(generator, ::onGeneratedIteration)
        }

        button_itr.setOnClickListener {
            workManager.toggleGenerationThread()
            button_itr.setText(if (workManager.isRunning()) R.string.canvas_stop_iteration else R.string.canvas_start_iteration)
        }

        button_save.setOnClickListener {
            activity.saveAncestorCore(ancestorCore)
            Toast.makeText(activity, R.string.canvas_save_configuration_feedback, Toast.LENGTH_SHORT).show()
            button_save.setGone()
        }
    }

    override fun onStop() {
        super.onStop()
        workManager.stopWork()
    }

    private fun onGeneratedIteration(pathUpdate: List<Path>) {
        activity?.runOnUiThread {
            if (view != null) {
                iteration_counter_text.text = getString(R.string.canvas_iteration_count, shape_view.iterationCount)
                shape_view?.addPaths(pathUpdate)
                shape_view?.invalidate()
            } else {
                Log.d(
                    "Fractal",
                    "FractalCanvasFragment.OnGeneratedIteration(): Fragment already detached, no UI to update"
                )
            }
        }
    }
}
