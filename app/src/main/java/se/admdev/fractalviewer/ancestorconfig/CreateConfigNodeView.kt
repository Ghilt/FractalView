package se.admdev.fractalviewer.ancestorconfig

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_inline_create_config_node.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.CompactPickerItem
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.showOperand

private const val REQUEST_CODE_OPERAND_1 = 0
private const val REQUEST_CODE_OPERAND_2 = 1
private const val REQUEST_CODE_OPERAND_3 = 2

class CreateConfigNodeView : ConstraintLayout {

    var parent: CoreConfigFragment? = null
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

        operandButtonMap = mapOf(
            REQUEST_CODE_OPERAND_1 to select_operand_1_button,
            REQUEST_CODE_OPERAND_2 to select_operand_2_button,
            REQUEST_CODE_OPERAND_3 to select_operand_3_button
        )

        operandButtonMap.forEach { code, button -> button.setOnClickListener { showPicker(code, true) } }
    }

    /* Return value represents first: Operand which no longer are selected, Second: Operand which now are selected*/
    fun updateOperand(op: Operand, add: Boolean): Pair<Operand?, Operand?> {
        val button = if (add) {
            operandButtonMap.values.firstOrNull { it.text.isEmpty() }
                ?: run { operandButtonMap.values.last() }
        } else {
            operandButtonMap.values.firstOrNull { it.text == op.name }
        }

        return updateOperandButton(button, if (add) op else null)
    }

    private fun updateOperandButton(button: Button?, op: Operand?): Pair<Operand?, Operand?> {
        if (button == null) throw IllegalArgumentException("CreateConfigNodeView.updateOperandButton(): Button or operand is null")
        val oldOp = availableOperands.findOperand(button.text.toString())
        button.showOperand(op)
        return onOperandButtonUpdated(oldOp, op)
    }

    private fun onOperandButtonUpdated(oldOp: Operand?, changedOp: Operand?): Pair<Operand?, Operand?> {
        val containsOldOp = operandButtonMap.any { (_, button) -> button.hasOperand(oldOp) }
        val containsChangeOp = operandButtonMap.any { (_, button) -> button.hasOperand(changedOp) }
        return Pair(if (containsOldOp) null else oldOp, if (containsChangeOp) changedOp else null)
    }

    private fun showPicker(requestCode: Int, allowFreeFormInput: Boolean = true) = parent?.apply {
        CompactPickerFragment.newInstance(this, availableOperands, allowFreeFormInput, requestCode)
            .show(fragmentManager, CompactPickerFragment.TAG)
    }

    fun onPickerCompleted(requestCode: Int, data: Intent?): Pair<Operand?, Operand?> {
        return updateOperandButton(operandButtonMap[requestCode], data.getPickerChoice())
    }

    fun setOnCloseClickListener(function: () -> Unit) {
        clear_button.setOnClickListener {
            operandButtonMap.values.forEach { it.clearText() }
            function.invoke()
        }
    }
}

private fun Button.hasOperand(op: Operand?) = text.isNotEmpty() && text.toString() == op?.name

private fun ArrayList<CompactPickerItem<Operand>>.findOperand(text: String?): Operand? {
    return this.firstOrNull { it.content.name == text }?.content
}

private fun Button.clearText() {
    text = ""
    backgroundTintList = null
}
