package se.admdev.fractalviewer.ancestorconfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.fragment_core_config.*
import kotlinx.android.synthetic.main.layout_add_buttons.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import se.admdev.fractalviewer.gridLayoutManager
import se.admdev.fractalviewer.toDp
import se.admdev.fractalviewer.viewVisibility

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
        })

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> { items ->
            ancestor_grid.gridLayoutManager.spanCount = model.ancestorTileDimension
            adapter.setDataSet(items) // No longer get adapter animations for free, could calculate diff here and not reset
            adapter.notifyDataSetChanged()
            updateNodeCreationMode()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_core_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            if (childFragmentManager.backStackEntryCount == 0) model.onSaveNewNode()
        }

        dimming_overlay.setOnClickListener {/*Prevent click through*/}

        setupFabButtons()
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
        expandedConstraints.setRotation(R.id.add_config_node_menu_fab, 45f)

        val transition = ChangeBounds()
        transition.interpolator = FastOutSlowInInterpolator()
        add_config_node_menu_fab.setOnClickListener {
            TransitionManager.beginDelayedTransition(fab_space, transition)
            val constraint = if (expanded) foldedConstraints else expandedConstraints
            constraint.applyTo(fab_space)
            expanded = !expanded
//            it.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate_45_degrees))
        }

        add_conditional_config_node_fab.setOnClickListener {
            startCreateConditionalNodeFragment()
        }

    }


    override fun onTileClicked(position: Int) {
        model.ancestorTiles.triggerObserver()
    }

    private fun updateNodeCreationMode() {
        dimming_overlay.visibility = model.hasSelectedTile().viewVisibility
        dimming_overlay.translationZ = resources.getDimension(R.dimen.view_elevation_none)

        if (model.hasSelectedTile() && !isCreateNodeFragmentShown()) {
            childFragmentManager.beginTransaction()
                .add(R.id.create_node_frame, CreateNodeFragment.newInstance(), CreateNodeFragment.TAG)
                .addToBackStack(CreateNodeFragment.TAG)
                .commit()
        }
    }

    private fun startCreateConditionalNodeFragment() {
        dimming_overlay.visibility = true.viewVisibility
        dimming_overlay.translationZ = resources.getDimension(R.dimen.view_elevation_large)

        if (!isCreateConditionalNodeFragmentShown()) {
            childFragmentManager.beginTransaction()
                .add(
                    R.id.create_node_frame,
                    CreateConditionalNodeFragment.newInstance(),
                    CreateConditionalNodeFragment.TAG
                )
                .addToBackStack(CreateNodeFragment.TAG)
                .commit()
        }
    }

    private fun isCreateNodeFragmentShown() = childFragmentManager.findFragmentByTag(CreateNodeFragment.TAG) != null
    private fun isCreateConditionalNodeFragmentShown() =
        childFragmentManager.findFragmentByTag(CreateConditionalNodeFragment.TAG) != null

    companion object {

        @JvmStatic
        fun createInstance(): Fragment {
            return CoreConfigFragment()
        }
    }
}