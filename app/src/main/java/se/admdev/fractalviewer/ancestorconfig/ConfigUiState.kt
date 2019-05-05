package se.admdev.fractalviewer.ancestorconfig

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.core.content.res.ResourcesCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_core_config.*
import kotlinx.android.synthetic.main.layout_add_buttons.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.viewVisibility

class ConfigUiState(
    val fragment: CoreConfigFragment,
    private var isCreateGroupOperationState: Boolean = false,
    private var isCreateOperationState: Boolean = false
) {

    private var isFabMenuExpanded = false

    private val constraintOriginalState = ConstraintSet().apply {
        clone(fragment.fragment_layout)
    }

    private val constraintCreateGroupOperation = ConstraintSet().apply {
        clone(fragment.fragment_layout)
        connect(R.id.grid_background, BOTTOM, PARENT_ID, BOTTOM)
    }

    // TODO Cloning from layout inflates the layout, seems suboptimal,
    // TODO should prefer to clone from already inflated layout instead
    private val constraintCreateConfigNode = ConstraintSet().apply {
        clone(fragment.fragment_layout)
        connect(R.id.grid_background, BOTTOM, PARENT_ID, TOP)
        connect(R.id.list_empty_switcher, TOP, PARENT_ID, TOP)
        connect(R.id.list_empty_switcher, BOTTOM, R.id.inline_create_operator_controls, TOP)
        connect(R.id.ancestor_grid, BOTTOM, PARENT_ID, TOP, 10) // Otherwise slightly visible
        clear(R.id.ancestor_grid, TOP)
        connect(R.id.inline_create_operator_controls, BOTTOM, PARENT_ID, BOTTOM)
        clear(R.id.inline_create_operator_controls, TOP)
    }

    private val constraintFabCollapsed = ConstraintSet().apply {
        clone(fragment.context, R.layout.layout_add_buttons)
    }

    private val constraintFabExpanded = ConstraintSet().apply {
        clone(fragment.context, R.layout.layout_add_buttons)
        val pad = fragment.resources.getDimension(R.dimen.fab_spacing).toInt()
        connect(R.id.add_conditional_config_node_fab, BOTTOM, R.id.add_config_node_menu_fab, TOP, pad)
        connect(R.id.add_all_config_node_fab, BOTTOM, R.id.add_conditional_config_node_fab, TOP, pad)
        connect(R.id.add_operation_config_node_fab, BOTTOM, R.id.add_all_config_node_fab, TOP, pad)
        connect(R.id.add_conditional_config_node_fab, START, R.id.add_config_node_menu_fab, START, 0)
        connect(R.id.add_all_config_node_fab, START, R.id.add_conditional_config_node_fab, START, 0)
        connect(R.id.add_operation_config_node_fab, START, R.id.add_all_config_node_fab, START, 0)
    }

    fun updateGroupNodeCreationMode(hasSelectedTile: Boolean) = fragment.apply {

        val duration = resources.getInteger(R.integer.animation_ms_long).toLong()

        if (hasSelectedTile && !isCreateGroupOperationState) {

            val translateAnim = AnimationUtils.loadAnimation(context, R.anim.group_operator_dialog_reveal)
            translateAnim.interpolator = FastOutSlowInInterpolator()
            val started = startCreateGroupOperationNodeFragment()

            childFragmentManager.executePendingTransactions() // without this line the fragment is not animated with the line bellow
            if (started) animateGroupNodeDialog(create_node_frame, translateAnim)

            AnimatorInflater.loadAnimator(context, R.animator.grid_focus).apply { setTarget(ancestor_grid) }.start()
            val transition = ChangeBounds()
            transition.interpolator = FastOutSlowInInterpolator()
            transition.duration = duration
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            constraintCreateGroupOperation.applyTo(fragment_layout)

        } else if (!hasSelectedTile && isCreateGroupOperationState) {
            AnimatorInflater.loadAnimator(context, R.animator.grid_unfocus).apply { setTarget(ancestor_grid) }.start()
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
    fun showFab(visible: Boolean) = fragment.apply { fadeComponent(fab_space, visible) }

    fun toggleFabMenu() = fragment.apply {
        val transition = ChangeBounds()
        transition.interpolator = FastOutSlowInInterpolator()
        TransitionManager.beginDelayedTransition(fab_space, transition)
        val constraint = if (isFabMenuExpanded) constraintFabCollapsed else constraintFabExpanded
        constraint.applyTo(fab_space)
        isFabMenuExpanded = !isFabMenuExpanded

        if (isFabMenuExpanded) {
            val d =
                ResourcesCompat.getDrawable(resources, R.drawable.plus_to_cross_anim, null) as AnimatedVectorDrawable
            add_config_node_menu_fab.setImageDrawable(d)
            d.start()
        } else {
            val d =
                ResourcesCompat.getDrawable(resources, R.drawable.cross_to_plus_anim, null) as AnimatedVectorDrawable
            add_config_node_menu_fab.setImageDrawable(d)
            d.start()
        }

    }

    fun onViewCreated() = fragment.apply {
        val gridBackground = grid_background.background as AnimationDrawable
        startBackgroundAnimation(gridBackground)
        val inlineNodeCreationBackground = inline_create_operator_controls.background as AnimationDrawable
        startBackgroundAnimation(inlineNodeCreationBackground)

        list_empty_switcher.inAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        list_empty_switcher.outAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    }

    private fun CoreConfigFragment.startBackgroundAnimation(animDrawable: AnimationDrawable) {
        animDrawable.setEnterFadeDuration(resources.getInteger(R.integer.animation_ms_gradient_enter_fade))
        animDrawable.setExitFadeDuration(resources.getInteger(R.integer.animation_ms_gradient_exit_fade))
        animDrawable.start()
    }

    fun showRevealAnimationCreationFragment() = fragment.apply {
        val translateAnim = AnimationUtils.loadAnimation(context, R.anim.fab_to_dialog_translate)
        translateAnim.interpolator = FastOutSlowInInterpolator()
        animateCreateNodeDialog(create_node_frame, translateAnim)
    }

    private fun animateGroupNodeDialog(frame: FrameLayout, translateAnim: Animation?) {
        frame.startAnimation(translateAnim)
        ViewAnimationUtils.createCircularReveal(
            frame, frame.width / 2, frame.height / 3, 60f,
            frame.height.toFloat()
        ).apply {
            duration = frame.context.resources.getInteger(R.integer.animation_ms_long).toLong()
        }.start()
    }

    private fun animateCreateNodeDialog(frame: FrameLayout, translateAnim: Animation?) {
        frame.startAnimation(translateAnim)
        ViewAnimationUtils.createCircularReveal(
            frame, frame.width / 2, frame.height / 2, 60f,
            frame.height.toFloat()
        ).apply {
            duration = frame.context.resources.getInteger(R.integer.animation_ms_long).toLong()
        }.start()
    }

    fun closeFabMenu() = fragment.apply {
        if (isFabMenuExpanded) {
            toggleFabMenu()
        }
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