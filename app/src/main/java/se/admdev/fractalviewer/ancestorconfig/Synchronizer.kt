package se.admdev.fractalviewer.ancestorconfig

class Synchronizer<T>(vararg taskIds: String) {

    private val tasks = taskIds.associateBy({ it }, { false }).toMutableMap()
    var action: (T) -> Unit = {}
    var data: T? = null

    fun onFinishedTask(taskId: String) {
        tasks[taskId] = true

        data?.let { argument ->
            if (tasks.values.all { it }) {
                action.invoke(argument)
                data = null
            }
        }
    }
}