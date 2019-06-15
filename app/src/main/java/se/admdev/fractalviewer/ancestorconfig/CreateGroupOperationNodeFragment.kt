package se.admdev.fractalviewer.ancestorconfig

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_create_group_operator_node.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorTileAdapter
import se.admdev.fractalviewer.ancestorconfig.model.*
import se.admdev.fractalviewer.gridLayoutManager
import se.admdev.fractalviewer.setTextIfNotNull


private const val REQUEST_CODE_GROUP_OPERATOR_PICKER = 0
private const val REQUEST_CODE_OPERATOR_PICKER = 1
private const val REQUEST_CODE_OPERAND_PICKER = 2

class CreateGroupOperationNodeFragment : Fragment() {

    private lateinit var model: ConfigViewModel
    private lateinit var creationData: CreateNodeViewModel
    private val creationGridAdapter = AncestorTileAdapter(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        } ?: throw Throwable("Invalid Activity")

        creationData = ViewModelProviders.of(this).get(CreateNodeViewModel::class.java)

        creationData.newGroupOperator.observe(this, Observer<GroupOperator> {
            select_group_operator_button.setTextIfNotNull(it?.symbol)
        })

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> {
            val snap = model.getTileSnapshot()
            ancestor_grid_edit_node_creation.gridLayoutManager.spanCount = snap.size
            creationGridAdapter.setDataSet(snap)
            creationGridAdapter.notifyDataSetChanged()

            if (!model.hasSelectedTile()) {
                fragmentManager?.popBackStack()
            }
        })

        creationData.newNodeOperator.observe(this, Observer<Operator> {
            select_operator_button.setTextIfNotNull(it?.symbol)
            select_operand_button.isEnabled = it != null
        })

        creationData.newNodeOperand.observe(this, Observer<Operand> {
            select_operand_button.setTextIfNotNull(it?.name)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_group_operator_node, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adjustForAncestorGridAnimation(view)

        select_group_operator_button.setOnClickListener {
            val data = ArrayList(GroupOperator.values().map { CompactPickerItem(it, it.symbol) })
            CompactPickerFragment.newOperatorInstance(this, data, REQUEST_CODE_GROUP_OPERATOR_PICKER)
                .show(fragmentManager, CompactPickerFragment.TAG)
        }

        creationGridAdapter.containerSize = resources.getDimension(R.dimen.grid_size_miniature)
        ancestor_grid_edit_node_creation.adapter = creationGridAdapter

        select_operator_button.setOnClickListener {
            val data = ArrayList(Operator.values().map { CompactPickerItem(it, it.symbol) })
            CompactPickerFragment.newOperatorInstance(this, data, REQUEST_CODE_OPERATOR_PICKER)
                .show(fragmentManager, CompactPickerFragment.TAG)
        }

        select_operand_button.setOnClickListener {
            val data = model.getAvailableOperandsArrayList()
            CompactPickerFragment.newOperandInstance(this, data, REQUEST_CODE_OPERAND_PICKER)
                .show(fragmentManager, CompactPickerFragment.TAG)
        }

        accept_selection_button.setOnClickListener {
            val success = model.saveNewGroupOperationNode(creationData.newGroupOperator.value, creationData.newNodeOperator.value, creationData.newNodeOperand.value)
            if (success) {
                fragmentManager?.popBackStack()
            } else {
                Toast.makeText(context, R.string.general_not_enough_input_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adjustForAncestorGridAnimation(root: View) {
        val typedValue = TypedValue()
        resources.getValue(R.dimen.ancestor_grid_focus_scaling_target, typedValue, true)
        val gridScale = typedValue.float
        val gridSize = resources.getDimension(R.dimen.grid_size)
        val gridTranslation = resources.getDimension(R.dimen.ancestor_grid_focus_translation_target)
        val extraMargin = gridSize * (gridScale - 1) + gridTranslation
        val params = root.layoutParams as FrameLayout.LayoutParams
        params.topMargin += extraMargin.toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isAdded) {
            when (requestCode) {
                REQUEST_CODE_GROUP_OPERATOR_PICKER -> onGroupOperator(data?.getParcelableExtra(CompactPickerFragment.EXTRA_SELECTED))
                REQUEST_CODE_OPERATOR_PICKER -> onOperatorSelected(data?.getParcelableExtra(CompactPickerFragment.EXTRA_SELECTED))
                REQUEST_CODE_OPERAND_PICKER -> onOperandSelected(data?.getParcelableExtra(CompactPickerFragment.EXTRA_SELECTED))
            }
        }
    }

    private fun onGroupOperator(operator: GroupOperator?) {
        creationData.newGroupOperator.value = operator
    }

    private fun onOperatorSelected(operator: Operator?) {
        creationData.newNodeOperator.value = operator
    }

    private fun onOperandSelected(operand: Operand?) {
        creationData.newNodeOperand.value = operand
    }

    companion object {

        const val TAG = "CreateGroupOperationNodeFragment"

        @JvmStatic
        fun newInstance() = CreateGroupOperationNodeFragment()
    }
}
