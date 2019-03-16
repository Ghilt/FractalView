package se.admdev.fractalviewer.ancestorconfig

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_create_node.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.*
import se.admdev.fractalviewer.gridLayoutManager
import se.admdev.fractalviewer.setTextIfNotNull

private const val REQUEST_CODE_OPERATOR_PICKER = 0
private const val REQUEST_CODE_OPERAND_PICKER = 1

class CreateNodeFragment : Fragment() {

    private lateinit var model: ConfigViewModel
    private val creationGridAdapter = AncestorTileAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        } ?: throw Throwable("Invalid Activity")

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> {
            val snap = model.getTileSnapshot()
            ancestor_grid_edit_node_creation.gridLayoutManager.spanCount = snap.size
            creationGridAdapter.setDataSet(snap)
            creationGridAdapter.notifyDataSetChanged()

            if (!model.hasSelectedTile()) {
                fragmentManager?.popBackStack()
            }
        })

        model.newNodeOperator.observe(this, Observer<Operator> {
            select_operator_button.setTextIfNotNull(it?.symbol)
            select_operand_button.isEnabled = it != null && it != Operator.NONE

        })

        model.newNodeOperand.observe(this, Observer<Operand> {
            select_operand_button.setTextIfNotNull(it?.name)
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_node, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        creationGridAdapter.containerSize = resources.getDimension(R.dimen.grid_size_miniature)
        ancestor_grid_edit_node_creation.adapter = creationGridAdapter

        accept_selection_button.setOnClickListener {
            model.configNodes.addItem(
                ConfigNode(
                    model.getNextConfigNodeLabel(),
                    model.getTileSnapshot(),
                    model.newNodeOperator.value,
                    model.newNodeOperand.value
                )
            )
            model.onSaveNewNode()
        }

        select_operator_button.setOnClickListener {
            val data = ArrayList(Operator.values().map { CompactPickerItem(it, it.symbol) })
            CompactPickerFragment.newInstance(this, data, false, REQUEST_CODE_OPERATOR_PICKER)
                .show(fragmentManager, CompactPickerFragment.TAG)
        }

        select_operand_button.setOnClickListener {
            val data = model.getAvailableOperandsArrayList()
            CompactPickerFragment.newInstance(this, data, true, REQUEST_CODE_OPERAND_PICKER)
                .show(fragmentManager, CompactPickerFragment.TAG)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isAdded) {
            when (requestCode) {
                REQUEST_CODE_OPERATOR_PICKER -> onOperatorSelected(data?.getParcelableExtra(CompactPickerFragment.EXTRA_SELECTED))
                REQUEST_CODE_OPERAND_PICKER -> onOperandSelected(data?.getParcelableExtra(CompactPickerFragment.EXTRA_SELECTED))
            }
        }
    }

    private fun onOperatorSelected(operator: Operator?) {
        model.newNodeOperator.value = operator
    }

    private fun onOperandSelected(operand: Operand?) {
        model.newNodeOperand.value = operand
    }

    companion object {

        const val TAG = "CreateNodeFragment"

        @JvmStatic
        fun newInstance() = CreateNodeFragment()
    }
}
