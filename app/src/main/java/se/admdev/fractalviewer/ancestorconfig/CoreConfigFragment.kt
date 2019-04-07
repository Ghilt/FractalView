package se.admdev.fractalviewer.ancestorconfig

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_core_config.*
import kotlinx.android.synthetic.main.layout_add_buttons.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorTileAdapter
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.gridLayoutManager

class CoreConfigFragment : Fragment(), AncestorTileAdapter.AncestorGridClickListener {

    private lateinit var uiState: ConfigUiState
    private lateinit var model: ConfigViewModel
    private lateinit var ancestorAdapter: AncestorTileAdapter
    private lateinit var listAdapter: ConfigurationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ancestorAdapter = AncestorTileAdapter()
        listAdapter = ConfigurationListAdapter(this::onConfigNodeClicked)

        model = activity?.run {
            ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        } ?: throw Throwable("Invalid Activity")

        model.configNodes.observe(this, Observer<List<ConfigNode>> { items ->
            listAdapter.setDataSet(items)
            listAdapter.notifyDataSetChanged()
            showList(items.isNotEmpty())
            minus_grid_size_button.isEnabled = model.isChangeGridSizeEnabled()
            plus_grid_size_button.isEnabled = model.isChangeGridSizeEnabled()
            uiState.updateNodeCreationMode(model.hasSelectedConfigNode())
        })

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> { items ->
            minus_grid_size_button.isEnabled = !model.isAncestorGridMinSize() && model.isChangeGridSizeEnabled()
            plus_grid_size_button.isEnabled = !model.isAncestorGridMaxSize() && model.isChangeGridSizeEnabled()
            ancestor_grid.gridLayoutManager.spanCount = model.ancestorTileDimension
            ancestorAdapter.setDataSet(items) // No longer get adapter animations for free, could calculate diff here and not reset
            ancestorAdapter.notifyDataSetChanged()
            uiState.updateGroupNodeCreationMode(model.hasSelectedTile())
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

        val animDrawable = grid_background.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(resources.getInteger(R.integer.animation_ms_gradient_enter_fade))
        animDrawable.setExitFadeDuration(resources.getInteger(R.integer.animation_ms_gradient_exit_fade))
        animDrawable.start()

        list_empty_switcher.inAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        list_empty_switcher.outAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        ancestorAdapter.containerSize = resources.getDimension(R.dimen.grid_size)
        ancestorAdapter.listener = this
        ancestor_grid.adapter = ancestorAdapter
        model.ancestorTiles.triggerObserver()

        node_list.adapter = listAdapter
        model.configNodes.triggerObserver()


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
                uiState.hideDim()
                uiState.showFab(true)
            } else {
                uiState.closeFabMenu()
                uiState.showFab(false)
            }
        }

        dimming_overlay.setOnClickListener { /*Prevent click through*/ }
        grid_background.setOnClickListener { /*Prevent click through*/ }

        setupFabButtons()

        uiState = ConfigUiState(this)

    }

    private fun setupFabButtons() {
        add_config_node_menu_fab.setOnClickListener {
            uiState.toggleFabMenu()
        }

        add_conditional_config_node_fab.setOnClickListener {
            uiState.showRevealAnimationCreationFragment()
            startCreateConditionNodeFragment()
        }

        add_all_config_node_fab.setOnClickListener {
            model.selectAll()
        }

        add_operation_config_node_fab.setOnClickListener {
            uiState.showRevealAnimationCreationFragment()
            startCreateOperationNodeFragment()
        }
    }

    override fun onTileClicked(position: Int) {
        model.ancestorTiles.triggerObserver()
    }

    private fun onConfigNodeClicked(node: ConfigNode, longClick: Boolean) {
        if (longClick) {
            model.configNodes.triggerObserver()
            inline_create_operator_controls.addOperand(Operand(node))
        } else {
            val action = CoreConfigFragmentDirections.showFractal().apply {
                ancestorCore =
                    AncestorCore(model.configNodes.value?.dropLastWhile { it.label != node.label } ?: listOf())
            }
            view?.let { Navigation.findNavController(it).navigate(action) }
        }
    }

    fun startCreateGroupOperationNodeFragment(): Boolean {

        if (!isCreateGroupNodeFragmentShown()) {
            childFragmentManager.beginTransaction()
                .add(
                    R.id.create_node_frame,
                    CreateGroupOperationNodeFragment.newInstance(),
                    CreateGroupOperationNodeFragment.TAG
                )
                .addToBackStack(CreateGroupOperationNodeFragment.TAG)
                .commit()
            return true
        }
        return false
    }

    private fun startCreateOperationNodeFragment(): Boolean {

        if (!isCreateNodeFragmentShown()) {
            childFragmentManager.beginTransaction()
                .add(
                    R.id.create_node_frame,
                    CreateOperationNodeFragment.newInstance(),
                    CreateOperationNodeFragment.TAG
                )
                .addToBackStack(CreateOperationNodeFragment.TAG)
                .commit()
            return true
        }
        return false
    }

    private fun startCreateConditionNodeFragment() {
        uiState.showDim()
        if (!isCreateConditionNodeFragmentShown()) {
            childFragmentManager.beginTransaction()
                .add(
                    R.id.create_node_frame,
                    CreateConditionNodeFragment.newInstance(),
                    CreateConditionNodeFragment.TAG
                )
                .addToBackStack(CreateGroupOperationNodeFragment.TAG)
                .commit()
        }
    }

    private fun isCreateNodeFragmentShown() =
        childFragmentManager.findFragmentByTag(CreateOperationNodeFragment.TAG) != null

    private fun isCreateGroupNodeFragmentShown() =
        childFragmentManager.findFragmentByTag(CreateGroupOperationNodeFragment.TAG) != null

    private fun isCreateConditionNodeFragmentShown() =
        childFragmentManager.findFragmentByTag(CreateConditionNodeFragment.TAG) != null

    companion object {

        @JvmStatic
        fun createInstance(): Fragment {
            return CoreConfigFragment()
        }
    }
}