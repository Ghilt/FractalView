package se.admdev.fractalviewer.ancestorconfig

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_core_config.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorTileAdapter
import se.admdev.fractalviewer.ancestorconfig.adapter.ConfigurationListAdapter
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.gridLayoutManager
import se.admdev.fractalviewer.showList

class CoreConfigFragment : Fragment(), AncestorTileAdapter.AncestorGridClickListener {

    private lateinit var uiState: ConfigUiState
    private lateinit var model: ConfigViewModel
    private lateinit var ancestorAdapter: AncestorTileAdapter
    private lateinit var listAdapter: ConfigurationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ancestorAdapter = AncestorTileAdapter(true)
        listAdapter = ConfigurationListAdapter(this::onConfigNodeClicked)

        model = ViewModelProviders.of(requireActivity()).get(ConfigViewModel::class.java)

        model.configNodes.observe(this, Observer<List<ConfigNode>> { items ->
            listAdapter.setDataSet(items)
            listAdapter.notifyDataSetChanged()
            list_empty_switcher.showList(items.isNotEmpty())
            minus_grid_size_button.isEnabled = model.isChangeGridSizeEnabled()
            plus_grid_size_button.isEnabled = model.isChangeGridSizeEnabled()
            uiState.updateNodeCreationMode(model.hasSelectedConfigNode())
            inline_create_operator_controls.availableOperands = model.getAvailableOperandsArrayList()
        })

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> { items ->
            val showChangeGridSizeButtons = model.isChangeGridSizeEnabled() && !model.hasSelectedTile()
            minus_grid_size_button.isEnabled = !model.isAncestorGridMinSize() && showChangeGridSizeButtons
            plus_grid_size_button.isEnabled = !model.isAncestorGridMaxSize() && showChangeGridSizeButtons
            ancestor_grid.gridLayoutManager.spanCount = model.ancestorTileDimension
            ancestorAdapter.setDataSet(items) // No longer get adapter animations for free, could calculate diff here and not reset
            ancestorAdapter.notifyDataSetChanged()
            uiState.updateGroupNodeCreationMode(model.hasSelectedTile())
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_core_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInterceptBackButton(view)

        arguments?.let { arg ->
            CoreConfigFragmentArgs.fromBundle(arg).ancestorCore?.let { model.loadFromAncestorCore(it) }
        }

        ancestorAdapter.containerSize = resources.getDimension(R.dimen.grid_size)
        ancestorAdapter.listener = this
        ancestor_grid.adapter = ancestorAdapter
        model.ancestorTiles.triggerObserver()

        node_list.adapter = listAdapter
        model.configNodes.triggerObserver()

        plus_grid_size_button.setOnClickListener {
            model.increaseAncestorTiles()
            AnimatorInflater.loadAnimator(context, R.animator.increase_bump_small).apply { setTarget(ancestor_grid) }
                .start()
        }

        minus_grid_size_button.setOnClickListener {
            model.decreaseAncestorTiles()
            AnimatorInflater.loadAnimator(context, R.animator.decrease_bump_small).apply { setTarget(ancestor_grid) }
                .start()
        }

        dimming_overlay.setOnClickListener { /*Prevent click through*/ }
        grid_background.setOnClickListener { /*Prevent click through*/ }

        inline_create_operator_controls.parent = this
        inline_create_operator_controls.setOnCloseClickListener {
            model.clearConfigNodeSelection()
        }

        inline_create_operator_controls.setOnSaveNodeClickListener { op1, operator, op2, op3 ->
            val success = model.createNode(op1, operator, op2, op3)
            if (success) {
                model.clearConfigNodeSelection()
            }
        }

        uiState = ConfigUiState(this)
        uiState.onViewCreated()
    }

    private fun setupInterceptBackButton(view: View) {
        view.isFocusableInTouchMode = true // Needed to onBack intercept in fragment
        view.requestFocus()
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                val isBackPress = keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP
                if (isBackPress) {
                    return onBack()
                }
                return false
            }
        })
    }

    fun onBack(): Boolean {
        return when {
            model.hasSelectedTile() -> {
                model.clearGroupNodeCreationData()
                uiState.hideDim()
                true
            }
            model.hasSelectedConfigNode() -> {
                model.clearConfigNodeSelection()
                true
            }
            else -> false
        }
    }

    override fun onTileClicked(position: Int) {
        model.ancestorTiles.triggerObserver()
    }

    private fun onConfigNodeClicked(node: ConfigNode, longClick: Boolean, selected: Boolean, buttonClick: Boolean) {
        when {
            buttonClick -> {
                val action = CoreConfigFragmentDirections.showFractal().apply {
                    ancestorCore =
                        AncestorCore(model.configNodes.value?.dropLastWhile { it.label != node.label } ?: listOf())
                }
                view?.let { Navigation.findNavController(it).navigate(action) }

            }
            longClick -> {
                val operandToToggle = inline_create_operator_controls.updateOperand(Operand(node), selected)
                model.changeConfigNodeSelection(operandToToggle)

            }
            else -> {
                val operandToToggle = inline_create_operator_controls.updateOperand(Operand(node), selected)
                model.changeConfigNodeSelection(operandToToggle)
            }
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

    private fun isCreateGroupNodeFragmentShown() =
        childFragmentManager.findFragmentByTag(CreateGroupOperationNodeFragment.TAG) != null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isAdded) {
            inline_create_operator_controls?.onPickerCompleted(requestCode, data)?.let {
                model.changeConfigNodeSelection(it)
            }
        }
    }
}