package se.admdev.fractalviewer.ancestorconfig

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_core_config.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.startBackgroundAnimation
import se.admdev.fractalviewer.viewVisibility

class ConfigUiState(
    val fragment: CoreConfigFragment,
    private var isCreateGroupOperationState: Boolean = false,
    private var isCreateOperationState: Boolean = false
) {

    private val animEnterFromDepth = AnimatorInflater.loadAnimator(fragment.context, R.animator.enter_from_depth)
    private val animFocusGrid = AnimatorInflater.loadAnimator(fragment.context, R.animator.grid_focus)
    private val animUnFocusGrid = AnimatorInflater.loadAnimator(fragment.context, R.animator.grid_unfocus)

    private val constraintOriginalState = ConstraintSet().apply {
        clone(fragment.fragment_layout)
    }

    private val constraintCreateGroupOperation = ConstraintSet().apply {
        clone(fragment.fragment_layout)
        connect(R.id.grid_background, BOTTOM, PARENT_ID, BOTTOM)

        /*Below are constraints which doesn't mess up for portrait mode but fixes landscape mode */
        connect(R.id.grid_background, END, PARENT_ID, END)
    }

    // TODO Cloning from layout inflates the layout, seems suboptimal,
    // TODO should prefer to clone from already inflated layout instead
    private val constraintCreateConfigNode = ConstraintSet().apply {
        clone(fragment.fragment_layout)
        connect(R.id.grid_background, BOTTOM, PARENT_ID, TOP)
        connect(R.id.list_empty_switcher, TOP, PARENT_ID, TOP)
        connect(R.id.list_empty_switcher, BOTTOM, R.id.inline_edit_controls_anchor, TOP)
        connect(R.id.ancestor_grid, BOTTOM, PARENT_ID, TOP, 10) // Otherwise slightly visible
        clear(R.id.ancestor_grid, TOP)
        connect(R.id.inline_create_operator_controls, BOTTOM, PARENT_ID, BOTTOM)
        clear(R.id.inline_create_operator_controls, TOP)
    }

    fun updateGroupNodeCreationMode(hasSelectedTile: Boolean) = fragment.apply {
        val duration = resources.getInteger(R.integer.animation_ms_group_operator_reveal).toLong()

        if (hasSelectedTile && !isCreateGroupOperationState) {
            animEnterFromDepth.setTarget(create_node_frame)
            animFocusGrid.setTarget(ancestor_grid)

            val started = startCreateGroupOperationNodeFragment()

            childFragmentManager.executePendingTransactions() // important line
            if (started) animEnterFromDepth.start()

            animFocusGrid.start()
            val transition = ChangeBounds()
            transition.interpolator = FastOutSlowInInterpolator()
            transition.duration = duration
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            constraintCreateGroupOperation.applyTo(fragment_layout)

        } else if (!hasSelectedTile && isCreateGroupOperationState) {
            animUnFocusGrid.apply { setTarget(ancestor_grid) }.start()
            returnToOriginalState(duration)
        }

        isCreateGroupOperationState = hasSelectedTile
    }

    fun updateNodeCreationMode(hasSelectedNode: Boolean) = fragment.apply {
        val duration = resources.getInteger(R.integer.animation_ms_long).toLong()

        if (hasSelectedNode && !isCreateOperationState) {

            val transition = ChangeBounds()
            transition.interpolator = FastOutSlowInInterpolator()
            transition.duration = duration
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            constraintCreateConfigNode.applyTo(fragment_layout)

        } else if (!hasSelectedNode && isCreateOperationState) {
            returnToOriginalState(duration)
        }

        isCreateOperationState = hasSelectedNode
    }

    private fun CoreConfigFragment.returnToOriginalState(duration: Long) {
        val transition = ChangeBounds()
        transition.duration = duration
        transition.interpolator = FastOutSlowInInterpolator()
        TransitionManager.endTransitions(inline_create_operator_controls)
        TransitionManager.beginDelayedTransition(fragment_layout, transition)
        constraintOriginalState.applyTo(fragment_layout)
    }

    fun showDim() = fragment.apply { fadeComponent(dimming_overlay, true) }
    fun hideDim() = fragment.apply { fadeComponent(dimming_overlay, false) }

    fun onViewCreated() = fragment.apply {
        val gridBackground = grid_background.background as AnimationDrawable
        view?.startBackgroundAnimation(gridBackground)
        val inlineNodeCreationBackground = inline_create_operator_controls.background as AnimationDrawable
        view?.startBackgroundAnimation(inlineNodeCreationBackground)

        list_empty_switcher.inAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        list_empty_switcher.outAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    }

    private fun fadeComponent(view: View, visible: Boolean) {

        if (visible) {
            view.visibility = true.viewVisibility
            view.animate().alpha(1f).start()
        } else {
            ObjectAnimator.ofFloat(view, "alpha", 0.0f).apply {
                interpolator = AccelerateDecelerateInterpolator()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        view.visibility = false.viewVisibility
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        view.visibility = false.viewVisibility
                    }

                    override fun onAnimationStart(animation: Animator?) {}
                })
            }.start()
        }
    }
}