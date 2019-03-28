package se.admdev.fractalviewer.ancestorconfig

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_core_config.*
import kotlinx.android.synthetic.main.layout_add_buttons.*
import se.admdev.fractalviewer.*
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorTileAdapter
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode

class CoreConfigFragment : Fragment(), AncestorTileAdapter.AncestorGridClickListener {

    private lateinit var model: ConfigViewModel
    private val adapter = AncestorTileAdapter()
    private val listAdapter = ConfigurationListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        } ?: throw Throwable("Invalid Activity")

        model.configNodes.observe(this, Observer<List<ConfigNode>> { items ->
            listAdapter.setDataSet(items)
            listAdapter.notifyDataSetChanged()
            final_target_text.showLabel(items.lastOrNull()?.label)
            showList(items.isNotEmpty())
        })

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> { items ->
            ancestor_grid.gridLayoutManager.spanCount = model.ancestorTileDimension
            adapter.setDataSet(items) // No longer get adapter animations for free, could calculate diff here and not reset
            adapter.notifyDataSetChanged()
            updateNodeCreationMode()
        })
    }

    private fun showList(show: Boolean) {
        val currentlyShowingEmpty = list_empty_switcher.nextView is RecyclerView
        if (show == currentlyShowingEmpty) list_empty_switcher.showNext()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_core_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_empty_switcher.inAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        list_empty_switcher.outAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        adapter.containerSize = resources.getDimension(R.dimen.grid_size)
        adapter.listener = this
        ancestor_grid.adapter = adapter

        node_list.adapter = listAdapter

        plus_grid_size_button.setOnClickListener {
            model.increaseAncestorTiles()
        }

        minus_grid_size_button.setOnClickListener {
            model.decreaseAncestorTiles()
        }

        childFragmentManager.addOnBackStackChangedListener {
            // Not the best/temp solution. Need to clear selection when user backs out from node creation
            // and there is no good onBack intercept for Fragments. Alternative is to handle in activity
            if (childFragmentManager.backStackEntryCount == 0) {
                model.clearNodeCreationData()
                fab_space.setVisible()
            } else {
                fab_space.setGone()
            }
        }

        dimming_overlay.setOnClickListener { /*Prevent click through*/ }

        setupFabButtons()

        confirm_core_button.setOnClickListener {
            val action = CoreConfigFragmentDirections.showFractal().apply {
                ancestorCore = AncestorCore(model.configNodes.value ?: listOf())
            }
            Navigation.findNavController(view).navigate(action)
        }
    }

    private fun setupFabButtons() {
        var expanded = false
        val foldedConstraints = ConstraintSet()
        foldedConstraints.clone(fab_space)
        val expandedConstraints = ConstraintSet()
        expandedConstraints.clone(fab_space)
        expandedConstraints.connect(
            R.id.add_conditional_config_node_fab, ConstraintSet.BOTTOM,
            R.id.add_config_node_menu_fab, ConstraintSet.TOP,
            resources.getDimension(R.dimen.fab_spacing).toInt()
        )
        expandedConstraints.connect(
            R.id.add_all_config_node_fab, ConstraintSet.BOTTOM,
            R.id.add_conditional_config_node_fab, ConstraintSet.TOP,
            resources.getDimension(R.dimen.fab_spacing).toInt()
        )

        val transition = ChangeBounds()
        transition.interpolator = FastOutSlowInInterpolator()
        add_config_node_menu_fab.setOnClickListener {
            TransitionManager.beginDelayedTransition(fab_space, transition)
            val constraint = if (expanded) foldedConstraints else expandedConstraints
            constraint.applyTo(fab_space)
            expanded = !expanded

            if (expanded) {
                val d = getDrawable(resources, R.drawable.plus_to_cross_anim, null) as AnimatedVectorDrawable
                add_config_node_menu_fab.setImageDrawable(d)
                d.start()
            } else {
                val d = getDrawable(resources, R.drawable.cross_to_plus_anim, null) as AnimatedVectorDrawable
                add_config_node_menu_fab.setImageDrawable(d)
                d.start()
            }
        }

        val translateAnim = AnimationUtils.loadAnimation(context, R.anim.fab_to_dialog_translate)
        val frame = create_node_frame

        add_conditional_config_node_fab.setOnClickListener {
            frame.startAnimation(translateAnim)
            ViewAnimationUtils.createCircularReveal(
                frame,
                frame.width / 2,
                frame.height / 2,
                60f,
                frame.height.toFloat()
            ).apply {
                duration = context?.resources?.getInteger(R.integer.animation_ms_long)?.toLong() ?: 0
            }.start()
            startCreateConditionalNodeFragment()
        }

        add_all_config_node_fab.setOnClickListener {
            model.selectAll()
        }
    }

    override fun onTileClicked(position: Int) {
        model.ancestorTiles.triggerObserver()
    }

    private fun updateNodeCreationMode() {
        val selectedTiles = model.hasSelectedTile()
        dimming_overlay.visibility = selectedTiles.viewVisibility

        if (selectedTiles) {
            ancestor_grid.elevation = resources.getDimension(R.dimen.view_elevation_dialog)
            if (!isCreateNodeFragmentShown()) {
                childFragmentManager.beginTransaction()
                    .add(
                        R.id.create_node_frame,
                        CreateOperationNodeFragment.newInstance(),
                        CreateOperationNodeFragment.TAG
                    )
                    .addToBackStack(CreateOperationNodeFragment.TAG)
                    .commit()
            }
        } else {
            ancestor_grid.elevation = resources.getDimension(R.dimen.view_elevation_small)
        }
    }

    private fun startCreateConditionalNodeFragment() {
        dimming_overlay.visibility = true.viewVisibility

        if (!isCreateConditionalNodeFragmentShown()) {
            childFragmentManager.beginTransaction()
                .add(
                    R.id.create_node_frame,
                    CreateConditionalNodeFragment.newInstance(),
                    CreateConditionalNodeFragment.TAG
                )
                .addToBackStack(CreateOperationNodeFragment.TAG)
                .commit()
        }
    }

    private fun isCreateNodeFragmentShown() =
        childFragmentManager.findFragmentByTag(CreateOperationNodeFragment.TAG) != null

    private fun isCreateConditionalNodeFragmentShown() =
        childFragmentManager.findFragmentByTag(CreateConditionalNodeFragment.TAG) != null

    companion object {

        @JvmStatic
        fun createInstance(): Fragment {
            return CoreConfigFragment()
        }
    }
}