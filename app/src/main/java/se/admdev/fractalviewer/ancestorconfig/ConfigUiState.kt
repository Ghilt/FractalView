package se.admdev.fractalviewer.ancestorconfig

import android.animation.AnimatorInflater
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintSet
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_core_config.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.viewVisibility

class ConfigUiState(
    val fragment: CoreConfigFragment,
    private var isAddOperationState: Boolean
) {

    private val originalState = ConstraintSet().apply {
        clone(fragment.context, R.layout.fragment_core_config)
    }

    private val editState = ConstraintSet().apply {
        clone(fragment.context, R.layout.fragment_core_config)
        connect(
            R.id.grid_background, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
    }

    fun updateNodeCreationMode(hasSelectedTile: Boolean) = fragment.apply {

        val duration = resources.getInteger(R.integer.animation_ms_long).toLong()

        if (hasSelectedTile && !isAddOperationState) {
            val translateAnim = AnimationUtils.loadAnimation(context, R.anim.fab_to_dialog_translate)
            val started = startCreateOperationNodeFragment()
            if (started) animateCreateNodeDialog(create_node_frame, translateAnim) // TODO, fix

            AnimatorInflater.loadAnimator(context, R.animator.grid_focus).apply { setTarget(ancestor_grid) }.start()
            val transition = ChangeBounds()
            transition.interpolator = FastOutSlowInInterpolator()
            transition.duration = duration
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            editState.applyTo(fragment_layout)

        } else if (!hasSelectedTile && isAddOperationState) {

            AnimatorInflater.loadAnimator(context, R.animator.grid_unfocus).apply { setTarget(ancestor_grid) }.start()
            val transition = ChangeBounds()
            transition.duration = duration
            transition.interpolator = FastOutSlowInInterpolator()
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            originalState.applyTo(fragment_layout)
        } else if (!hasSelectedTile) {
            dimming_overlay.visibility = false.viewVisibility // TODO animate
        }

        isAddOperationState = hasSelectedTile
    }

}