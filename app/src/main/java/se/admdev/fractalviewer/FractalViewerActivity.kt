package se.admdev.fractalviewer

import android.graphics.Path
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_fractal_viewer.*
import se.admdev.fractalviewer.model.AncestorCore
import se.admdev.fractalviewer.model.DragonCurve
import se.admdev.fractalviewer.model.FractalGenerator

class FractalViewerActivity : AppCompatActivity() {

    var currentIteration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fractal_viewer)

        val curve = DragonCurve()

        val testPanView: FractalView = shape_view
        val path = Path()
        testPanView.path = path

        val core = AncestorCore()
        val gen = FractalGenerator(core)

        val button = button_itr
        button.setOnClickListener {
            val newEnd = curve.getDirectionAt(currentIteration)
            path.rLineTo((newEnd.x * 14).toFloat(), (-newEnd.y * 14).toFloat())
            currentIteration++
            gen.generateNextIteration()
        }
    }
}
