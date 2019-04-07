package se.admdev.fractalviewer.ancestorconfig

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_inline_create_config_node.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.setTextIfNotNull


private const val STATE_SUPER = "super"
private const val STATE_OP_1 = "op1"
private const val STATE_OP_2 = "op2"
private const val STATE_OP_3 = "op3"

class CreateConfigNodeView : ConstraintLayout {

    private var operand1: Operand? = null
    private var operand2: Operand? = null
    private var operand3: Operand? = null

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
    }

    fun addOperand(op: Operand) {
        when (null) {
            operand1 -> addFirstOperand(op)
            operand2 -> addSecondOperand(op)
            operand3 -> addThirdOperand(op)
        }
    }


    private fun addFirstOperand(op: Operand) {
        operand1 = op
        select_operand_1_button.setTextIfNotNull(op.name)
    }

    private fun addSecondOperand(op: Operand) {
        operand2 = op
        select_operand_2_button.setTextIfNotNull(op.name)
    }

    private fun addThirdOperand(op: Operand) {
        operand3 = op
        select_operand_3_button.setTextIfNotNull(op.name)

    }


    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        // TODO No idea why the buttons don't manage to restore their state on their own
        bundle.putParcelable(STATE_SUPER,super.onSaveInstanceState())
        bundle.putParcelable(STATE_OP_1, operand1)
        bundle.putParcelable(STATE_OP_2, operand2)
        bundle.putParcelable(STATE_OP_3, operand3)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(STATE_SUPER))
            val op1: Operand? = state.getParcelable(STATE_OP_1)
            val op2: Operand? = state.getParcelable(STATE_OP_2)
            val op3: Operand? = state.getParcelable(STATE_OP_3)
            op1?.let {addFirstOperand(it)}
            op2?.let {addSecondOperand(it)}
            op3?.let {addThirdOperand(it)}

        }
    }
}