package se.admdev.fractalviewer.canvas


import android.graphics.Matrix
import android.graphics.Path
import android.view.ScaleGestureDetector

class PinchListener(
    val path: Path,
    val requestRefresh: () -> Unit
) : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    private val scaleMatrix = Matrix()

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        //https://www.dev2qa.com/android-pinch-zoom-layout-example/
        detector?.let {
            val scale = it.scaleFactor
            scaleMatrix.setScale(scale, scale, it.focusX, it.focusY)
            path.transform(scaleMatrix)
        }

//        Log.d("spx", "Scaling: $scale" )

        requestRefresh()
        return true
    }
}