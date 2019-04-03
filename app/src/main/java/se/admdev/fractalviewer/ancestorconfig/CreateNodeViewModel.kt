package se.admdev.fractalviewer.ancestorconfig

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.admdev.fractalviewer.ancestorconfig.model.GroupOperator
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.ancestorconfig.model.Operator

class CreateNodeViewModel : ViewModel() {

    val newGroupOperator = MutableLiveData<GroupOperator>(GroupOperator.SUM)
    val newNodeOperator = MutableLiveData<Operator>()
    val newNodeOperand = MutableLiveData<Operand>()

    val newCondition = MutableLiveData<Operand>()
    val newConditionTrue = MutableLiveData<Operand>()
    val newConditionFalse = MutableLiveData<Operand>()

    val newOperationFirstNodeOperand = MutableLiveData<Operand>()
    val newOperationNodeOperator = MutableLiveData<Operator>(Operator.ADDITION)
    val newOperationSecondNodeOperand = MutableLiveData<Operand>()
}

