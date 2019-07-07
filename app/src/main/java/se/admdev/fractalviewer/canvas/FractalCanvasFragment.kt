package se.admdev.fractalviewer.canvas

import android.annotation.SuppressLint
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.alert_dialog_edit_text.view.*
import kotlinx.android.synthetic.main.fragment_fractal_canvas.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.ancestorconfig.saveAncestorCore
import se.admdev.fractalviewer.canvas.model.FractalGenerator
import se.admdev.fractalviewer.canvas.model.FractalPyramidGenerator
import se.admdev.fractalviewer.canvas.model.FractalSpiralGenerator
import se.admdev.fractalviewer.canvas.model.ThreadManager
import se.admdev.fractalviewer.playAnimatedDrawable
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

            generator = FractalPyramidGenerator(ancestorCore)
            workManager = ThreadManager(generator, ::onGeneratedIteration, ::onGenerationPaused)
        }

        button_itr.setOnClickListener {
            shape_view.activate()
            workManager.clearGenerationPauseIteration()
            toggleFractalGeneration()
        }

        button_toggle_fractal_type.setOnClickListener {
            shape_view.resetAndDisable()
            if (workManager.isRunning()) button_itr.playAnimatedDrawable(R.drawable.anim_pause_to_play)
            workManager.stopWork()
            generator = when (generator is FractalPyramidGenerator) {
                true ->  FractalSpiralGenerator(ancestorCore)
                false -> FractalPyramidGenerator(ancestorCore)
            }
            workManager = ThreadManager(generator, ::onGeneratedIteration, ::onGenerationPaused)
        }

        button_save.setOnClickListener {
            button_save.playAnimatedDrawable(R.drawable.anim_save)
            context?.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle(R.string.dialog_save_fractal_title)
                builder.setOnCancelListener {
                    button_save.playAnimatedDrawable(R.drawable.anim_cancel_save)
                }

                @SuppressLint("InflateParams") // Maybe deal with this later
                val layout = layoutInflater.inflate(R.layout.alert_dialog_edit_text, null)
                builder.setView(layout)

                builder.setPositiveButton(R.string.general_save) { _, _ ->
                    activity.saveAncestorCore(ancestorCore, layout.dialog_name_input.text.toString())
                    Snackbar.make(button_save, R.string.canvas_save_configuration_feedback, Snackbar.LENGTH_SHORT)
                        .show()
                    button_save.setGone()
                }
                builder.setNegativeButton(R.string.general_cancel) { dialog, _ ->
                    dialog.cancel()
                }
                builder.show()
            }
        }

        workManager.pauseAfterReachingIteration = PAUSE_AUTO_START_FRACTAL_GENERATION
        toggleFractalGeneration()
    }

    private fun toggleFractalGeneration() {
        workManager.toggleGenerationThread()
        if (workManager.isRunning()) {
            button_itr.playAnimatedDrawable(R.drawable.anim_play_to_pause)
        } else {
            button_itr.playAnimatedDrawable(R.drawable.anim_pause_to_play)
        }
    }

    override fun onResume() {
        super.onResume()
        workManager.refreshThreadPoolIfNeeded()

    }

    override fun onStop() {
        super.onStop()
        workManager.stopWork()
        button_itr.playAnimatedDrawable(R.drawable.anim_pause_to_play)
    }

    private fun onGeneratedIteration(pathUpdate: Path) {
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

    private fun onGenerationPaused() {
        workManager.toggleGenerationThread()
        button_itr?.post {
            button_itr?.playAnimatedDrawable(R.drawable.anim_pause_to_play)
        }
    }

    companion object {

        // Autostart fractal generation but pause it after #nbr of iterations
        const val PAUSE_AUTO_START_FRACTAL_GENERATION = 251
    }
}