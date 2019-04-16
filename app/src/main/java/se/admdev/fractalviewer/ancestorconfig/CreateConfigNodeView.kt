package se.admdev.fractalviewer.ancestorconfig

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.layout_inline_create_config_node.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.CompactPickerItem
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.ancestorconfig.model.Operator
import se.admdev.fractalviewer.isNotEmpty
import se.admdev.fractalviewer.setTextIfNotNull
import se.admdev.fractalviewer.showOperand

private const val REQUEST_CODE_OPERAND_1 = 0
private const val REQUEST_CODE_OPERAND_2 = 1
private const val REQUEST_CODE_OPERAND_3 = 2
private const val REQUEST_CODE_OPERATOR = 100

class CreateConfigNodeView : ConstraintLayout {

    private lateinit var constraintOriginalState: ConstraintSet
    private lateinit var constraintConditional: ConstraintSet

    var parent: CoreConfigFragment? = null
    private val animationDuration = context.resources.getInteger(R.integer.animation_ms_medium).toLong()
    private lateinit var operandButtonMap: Map<Int, Button>

    var availableOperands: ArrayList<CompactPickerItem<Operand>> = ArrayList()

    constructor(context: Context) : super(context) {
        loadLayout()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        loadLayout()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        loadLayout()
    }

    private fun loadLayout() {
        inflate(context, R.layout.layout_inline_create_config_node, this)

        constraintOriginalState = ConstraintSet().apply {
            clone(this@CreateConfigNodeView)
        }

        constraintConditional = ConstraintSet().apply {
            clone(this@CreateConfigNodeView)

            connect(
                R.id.select_operand_2_button, ConstraintSet.END,
                R.id.select_operand_3_button, ConstraintSet.START
            )
            connect(
                R.id.select_operand_3_button, ConstraintSet.START,
                R.id.select_operand_2_button, ConstraintSet.END
            )
            connect(
                R.id.select_operand_3_button, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        }

        operandButtonMap = mapOf(
            REQUEST_CODE_OPERAND_1 to select_operand_1_button,
            REQUEST_CODE_OPERAND_2 to select_operand_2_button,
            REQUEST_CODE_OPERAND_3 to select_operand_3_button
        )

        select_operand_3_button.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (select_operand_3_button.text.isNullOrEmpty()) {
                    animateToOperationState()
                } else {
                    animateToConditionalState()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        operandButtonMap.forEach { code, button -> button.setOnClickListener { showOperandPicker(code, true) } }

        select_operator_button.setOnClickListener { showOperatorPicker(REQUEST_CODE_OPERATOR, true) }
    }

    /* Return value represents first: Operand which no longer are selected, Second: Operand which now are selected*/
    fun updateOperand(op: Operand, add: Boolean): Pair<Operand?, Operand?> {
        return if (add) {
            val button = operandButtonMap.values.firstOrNull { it.text.isEmpty() }
                ?: run { operandButtonMap.values.last() }
            updateOperandButton(button, op)
        } else {
            operandButtonMap.values.filter { it.text.toString() == op.name }.forEach { updateOperandButton(it, null) }
            Pair(op, null)
        }
    }

    private fun updateOperandButton(button: Button?, op: Operand?): Pair<Operand?, Operand?> {
        val oldOp = availableOperands.findOperand(button?.text.toString())
        button?.showOperand(op)
        return onOperandButtonUpdated(oldOp, op)
    }

    private fun onOperandButtonUpdated(oldOp: Operand?, changedOp: Operand?): Pair<Operand?, Operand?> {
        val containsOldOp = operandButtonMap.any { (_, button) -> button.hasOperand(oldOp) }
        val containsChangeOp = operandButtonMap.any { (_, button) -> button.hasOperand(changedOp) }
        return Pair(if (containsOldOp) null else oldOp, if (containsChangeOp) changedOp else null)
    }

    private fun showOperandPicker(requestCode: Int, allowFreeFormInput: Boolean = true) = parent?.apply {
        CompactPickerFragment.newInstance(this, availableOperands, allowFreeFormInput, requestCode)
            .show(fragmentManager, CompactPickerFragment.TAG)
    }

    private fun updateOperatorButton(op: Operator?) {
        select_operator_button.setTextIfNotNull(op?.symbol)
    }

    private fun showOperatorPicker(requestCode: Int, allowFreeFormInput: Boolean = true) = parent?.apply {
        val data = ArrayList(Operator.values().map { CompactPickerItem(it, it.symbol) })
        CompactPickerFragment.newInstance(this, data, allowFreeFormInput, requestCode)
            .show(fragmentManager, CompactPickerFragment.TAG)
    }

    fun onPickerCompleted(requestCode: Int, data: Intent?): Pair<Operand?, Operand?> {
        return when (requestCode) {
            REQUEST_CODE_OPERAND_1,
            REQUEST_CODE_OPERAND_2,
            REQUEST_CODE_OPERAND_3 -> updateOperandButton(operandButtonMap[requestCode], data.getPickerChoiceOperand())
            REQUEST_CODE_OPERATOR -> {
                updateOperatorButton(data.getPickerChoiceOperator())
                Pair(null, null) // A little bit ugly, fix some time
            }
            else -> Pair(null, null)
        }
    }

    fun setOnCloseClickListener(function: () -> Unit) {
        clear_button.setOnClickListener {
            operandButtonMap.values.forEach { it.clearText() }
            function.invoke()
        }
    }

    fun setOnSaveNodeClickListener(function: (Operand?, Operator, Operand?, Operand?) -> Unit) {
        create_button.setOnClickListener {
            val op1 = operandButtonMap[REQUEST_CODE_OPERAND_1]?.createOperand()
            val operator = select_operator_button.createOperator()
            val op2 = operandButtonMap[REQUEST_CODE_OPERAND_2]?.createOperand()
            val op3 = operandButtonMap[REQUEST_CODE_OPERAND_3]?.createOperand()

            val enoughDataToCreateNode = op1 != null && op2 != null
            if (enoughDataToCreateNode) {
                operandButtonMap.values.forEach { it.clearText() }
                function.invoke(op1, operator, op2, op3)
            } else {
                Toast.makeText(context, R.string.general_not_enough_input_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun animateToConditionalState() {
        val transition = ChangeBounds()
        transition.interpolator = FastOutSlowInInterpolator()
        transition.duration = animationDuration
        TransitionManager.beginDelayedTransition(this, transition)
        constraintConditional.applyTo(this)
    }

    private fun animateToOperationState() {
        val transition = ChangeBounds()
        transition.interpolator = FastOutSlowInInterpolator()
        transition.duration = animationDuration
        TransitionManager.beginDelayedTransition(this, transition)
        constraintOriginalState.applyTo(this)
    }
}

private fun Button?.hasOperand(op: Operand?): Boolean = this?.text?.let {
    (it.isNotEmpty() && it.toString() == op?.name)
} ?: false

private fun Button.createOperand(): Operand? = if (isNotEmpty()) Operand(this.text.toString()) else null
private fun Button.createOperator(): Operator = Operator.values().first { this.text.toString() == it.symbol }
private fun ArrayList<CompactPickerItem<Operand>>.findOperand(text: String?) =
    firstOrNull { it.content.name == text }?.content


private fun Button.clearText() {
    text = ""
    backgroundTintList = null
}

fun Intent?.getPickerChoiceOperand(): Operand? = this?.getParcelableExtra(CompactPickerFragment.EXTRA_SELECTED)
fun Intent?.getPickerChoiceOperator(): Operator? = this?.getParcelableExtra(CompactPickerFragment.EXTRA_SELECTED)
