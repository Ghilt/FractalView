package se.admdev.fractalviewer.canvas

import androidx.fragment.app.Fragment
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import kotlinx.android.synthetic.main.fragment_fractal_canvas.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.canvas.model.DragonCurve
import se.admdev.fractalviewer.canvas.model.FractalGenerator
import java.lang.Exception

class FractalCanvasFragment : Fragment() {

    var currentIteration = 0
    var ancestorCore: AncestorCore? = null
    lateinit var generator: FractalGenerator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fractal_canvas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val curve = DragonCurve()

        val testPanView: FractalView = shape_view
        val path = Path()
        testPanView.path = path

        arguments?.let {
            val core = FractalCanvasFragmentArgs.fromBundle(it).ancestorCore

            generator = if (core == null){
                throw Exception("Error: No ancestor core for FractalCanvasFragment")
            } else {
                FractalGenerator(core)
            }
            ancestorCore = core
        }





//        val animation = FractalView.DragonCurveAnimation(testPanView)
//        animation.duration = Long.MAX_VALUE
//        startAnimation(animation)

        val button = button_itr
        button.setOnClickListener {
            val newEnd = curve.getDirectionAt(currentIteration)
            path.rLineTo((newEnd.x * 14).toFloat(), (-newEnd.y * 14).toFloat())
            currentIteration++
            generator.generateNextIteration()
            testPanView.invalidate()
        }
    }

    inner class DragonCurveAnimation(private val view: FractalView) : Animation() {

        override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
            view.requestLayout()
        }
    }
}
