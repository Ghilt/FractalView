package se.admdev.fractalviewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.admdev.fractalviewer.model.ConfigurationNode

class ConfigurationViewModel : ViewModel() {
    val ancestorGridSize = MutableLiveData<Int>().apply { value = 3 }
    val configNodes = MutableLiveData<MutableList<ConfigurationNode>>().apply { value = mutableListOf() }
}

fun <T> MutableLiveData<MutableList<T>>.addItem(item: T) {
    val updatedItems = this.value
    updatedItems?.add(item)
    this.value = updatedItems //Needed to trigger observer,
}