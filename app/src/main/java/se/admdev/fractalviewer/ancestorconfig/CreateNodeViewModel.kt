package se.admdev.fractalviewer.ancestorconfig

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.admdev.fractalviewer.ancestorconfig.model.*

class CreateNodeViewModel : ViewModel() {

    val newGroupOperator = MutableLiveData<GroupOperator>(GroupOperator.SUM)
    val newNodeOperator = MutableLiveData<Operator>()
    val newNodeOperand = MutableLiveData<Operand>()

    val newCondition = MutableLiveData<Operand>()
    val newConditionTrue = MutableLiveData<Operand>()
    val newConditionFalse = MutableLiveData<Operand>()
}

