package se.admdev.fractalviewer.ancestorconfig.model

import androidx.lifecycle.MutableLiveData

class ConfigNode(
    val tileSnapshot: List<List<AncestorTile>>,
    val operator: MutableLiveData<Operator>,
    val operand: MutableLiveData<Operand>
)