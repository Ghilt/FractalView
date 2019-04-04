package se.admdev.fractalviewer.ancestorconfig

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_core_config.*
import kotlinx.android.synthetic.main.layout_add_buttons.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.viewVisibility

class ConfigUiState(val fragment: CoreConfigFragment) {

    private var isFabMenuExpanded = false
    private var isAddOperationState = false

    private val constraintOriginalState = ConstraintSet().apply {
        clone(fragment.context, R.layout.fragment_core_config)
    }

    private val constraintEditOperation = ConstraintSet().apply {
        clone(fragment.context, R.layout.fragment_core_config)
        connect(
            R.id.grid_background, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM
        )
    }

    private val constraintFabCollapsed = ConstraintSet().apply {
        clone(fragment.context, R.layout.layout_add_buttons)
    }

    private val constraintFabExpanded = ConstraintSet().apply {
        clone(fragment.context, R.layout.layout_add_buttons)
        connect(
            R.id.add_conditional_config_node_fab, ConstraintSet.BOTTOM,
            R.id.add_config_node_menu_fab, ConstraintSet.TOP,
            fragment.resources.getDimension(R.dimen.fab_spacing).toInt()
        )
        connect(
            R.id.add_all_config_node_fab, ConstraintSet.BOTTOM,
            R.id.add_conditional_config_node_fab, ConstraintSet.TOP,
            fragment.resources.getDimension(R.dimen.fab_spacing).toInt()
        )
        connect(
            R.id.add_operation_config_node_fab, ConstraintSet.BOTTOM,
            R.id.add_all_config_node_fab, ConstraintSet.TOP,
            fragment.resources.getDimension(R.dimen.fab_spacing).toInt()
        )

        connect(
            R.id.add_conditional_config_node_fab, ConstraintSet.START,
            R.id.add_config_node_menu_fab, ConstraintSet.START,
            0
        )
        connect(
            R.id.add_all_config_node_fab, ConstraintSet.START,
            R.id.add_conditional_config_node_fab, ConstraintSet.START,
            0
        )
        connect(
            R.id.add_operation_config_node_fab, ConstraintSet.START,
            R.id.add_all_config_node_fab, ConstraintSet.START,
            0
        )
    }

    fun updateNodeCreationMode(hasSelectedTile: Boolean) = fragment.apply {

        val duration = resources.getInteger(R.integer.animation_ms_long).toLong()

        if (hasSelectedTile && !isAddOperationState) {
            val translateAnim = AnimationUtils.loadAnimation(context, R.anim.fab_to_dialog_translate)
            val started = startCreateGroupOperationNodeFragment()
            if (started) animateCreateNodeDialog(create_node_frame, translateAnim) // TODO, fix

            AnimatorInflater.loadAnimator(context, R.animator.grid_focus).apply { setTarget(ancestor_grid) }.start()
            val transition = ChangeBounds()
            transition.interpolator = FastOutSlowInInterpolator()
            transition.duration = duration
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            constraintEditOperation.applyTo(fragment_layout)

        } else if (!hasSelectedTile && isAddOperationState) {

            AnimatorInflater.loadAnimator(context, R.animator.grid_unfocus).apply { setTarget(ancestor_grid) }.start()
            val transition = ChangeBounds()
            transition.duration = duration
            transition.interpolator = FastOutSlowInInterpolator()
            TransitionManager.beginDelayedTransition(fragment_layout, transition)
            constraintOriginalState.applyTo(fragment_layout)
        }

        isAddOperationState = hasSelectedTile
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

    fun showRevealAnimationCreationFragment() = fragment.apply {
        val translateAnim = AnimationUtils.loadAnimation(context, R.anim.fab_to_dialog_translate)
        animateCreateNodeDialog(create_node_frame, translateAnim)
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
        // TransitionManager.beginDelayedTransition(v) //Really do not get along with the TransitionManager

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