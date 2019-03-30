package se.admdev.fractalviewer.ancestorconfig

import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintSet
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
            R.id.grid_container, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
    }

    fun updateNodeCreationMode(hasSelectedTile: Boolean) = fragment.apply {

        if (hasSelectedTile && !isAddOperationState) {
            ancestor_grid.elevation = resources.getDimension(R.dimen.view_elevation_dialog)
            grid_container.elevation = resources.getDimension(R.dimen.view_elevation_dialog)
            val translateAnim = AnimationUtils.loadAnimation(context, R.anim.fab_to_dialog_translate)
            val started = startCreateOperationNodeFragment()
            if (started) animateCreateNodeDialog(create_node_frame, translateAnim)


            val transition = ChangeBounds()
            transition.interpolator = AccelerateInterpolator()
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            editState.applyTo(fragment_layout)


        } else if (!hasSelectedTile && isAddOperationState) {
            dimming_overlay.visibility = false.viewVisibility
            ancestor_grid.elevation = resources.getDimension(R.dimen.view_elevation_small)
            grid_container.elevation = resources.getDimension(R.dimen.view_elevation_small)

            val transition = ChangeBounds()
            transition.interpolator = AccelerateInterpolator()
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            originalState.applyTo(fragment_layout)
        }

        isAddOperationState = hasSelectedTile
    }
}