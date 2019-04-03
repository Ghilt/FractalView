package se.admdev.fractalviewer.ancestorconfig

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_create_conditional_node.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.setTextIfNotNull

private const val REQUEST_CODE_CONDITIONAL_OPERAND = 0
private const val REQUEST_CODE_TRUTH_OPERAND = 1
private const val REQUEST_CODE_FALSE_OPERAND = 2

class CreateConditionNodeFragment : Fragment() {

    private lateinit var model: ConfigViewModel
    private lateinit var creationData: CreateNodeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        } ?: throw Throwable("Invalid Activity")

        creationData = ViewModelProviders.of(this).get(CreateNodeViewModel::class.java)

        creationData.newCondition.observe(this, Observer<Operand> {
            select_conditional_operand_button.setTextIfNotNull(it?.name)
        })

        creationData.newConditionTrue.observe(this, Observer<Operand> {
            select_truth_path_operand_button.setTextIfNotNull(it?.name)
        })

        creationData.newConditionFalse.observe(this, Observer<Operand> {
            select_false_path_operand_button.setTextIfNotNull(it?.name)
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_conditional_node, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        select_conditional_operand_button.setOnClickListener {
            showPicker(REQUEST_CODE_CONDITIONAL_OPERAND, false)
        }

        select_truth_path_operand_button.setOnClickListener {
            showPicker(REQUEST_CODE_TRUTH_OPERAND)
        }

        select_false_path_operand_button.setOnClickListener {
            showPicker(REQUEST_CODE_FALSE_OPERAND)
        }

        view.findViewById<Button>(R.id.accept_selection_button).setOnClickListener {

            val success = model.saveNewConditionNode(
                creationData.newCondition.value,
                creationData.newConditionTrue.value,
                creationData.newConditionFalse.value
            )
            if (success) {
                fragmentManager?.popBackStack()
            } else {
                Toast.makeText(context, R.string.general_not_enough_input_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isAdded) {
            when (requestCode) {
                REQUEST_CODE_CONDITIONAL_OPERAND -> creationData.newCondition.value = data.getPickerChoice()
                REQUEST_CODE_TRUTH_OPERAND -> creationData.newConditionTrue.value = data.getPickerChoice()
                REQUEST_CODE_FALSE_OPERAND -> creationData.newConditionFalse.value = data.getPickerChoice()
            }
        }
    }

    private fun showPicker(requestCode: Int, allowFreeFormInput: Boolean = true) {
        val data = model.getAvailableOperandsArrayList()
        CompactPickerFragment.newInstance(this, data, allowFreeFormInput, requestCode)
            .show(fragmentManager, CompactPickerFragment.TAG)
    }

    companion object {

        const val TAG = "CreateConditionNodeFragment"

        @JvmStatic
        fun newInstance(): CreateConditionNodeFragment = CreateConditionNodeFragment()
    }
}

fun Intent?.getPickerChoice(): Operand? = this?.getParcelableExtra(CompactPickerFragment.EXTRA_SELECTED)
