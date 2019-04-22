package se.admdev.fractalviewer.canvas

import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_fractal_canvas.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.canvas.model.FractalGenerator
import se.admdev.fractalviewer.canvas.model.ThreadManager


class FractalCanvasFragment : Fragment() {

    var ancestorCore: AncestorCore? = null
    lateinit var generator: FractalGenerator
    private lateinit var workManager: ThreadManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fractal_canvas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val core = FractalCanvasFragmentArgs.fromBundle(it).ancestorCore

            generator = if (core == null) {
                throw Exception("Error: No ancestor core for FractalCanvasFragment")
            } else {
                FractalGenerator(core)
            }
            workManager = ThreadManager(generator, ::onGeneratedIteration)
            ancestorCore = core
        }

        val button = button_itr
        button.setOnClickListener {
            workManager.toggleGenerationThread()
            button.setText(if (workManager.isRunning()) R.string.canvas_stop_iteration else R.string.canvas_start_iteration)
        }
    }

    override fun onStop() {
        super.onStop()
        workManager.stopWork()
    }

    private fun onGeneratedIteration(pathUpdate: List<Path>) {
        activity?.runOnUiThread {
            iteration_counter_text.text = getString(R.string.canvas_iteration_count, shape_view.iterationCount)
            shape_view?.addPaths(pathUpdate)
            shape_view?.invalidate()
        }
    }
}
