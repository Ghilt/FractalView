package se.admdev.fractalviewer.ancestorconfig.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreViewHolder.AncestorCoreAction
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore

class AncestorCoreAdapter(private val miniatureSize: Int, private val listener: ((Int, AncestorCore, AncestorCoreAction) -> Unit)) :
    RecyclerView.Adapter<AncestorCoreViewHolder>() {

//    init {
//        setHasStableIds(true)
//    }

    private var data = listOf<AncestorCoreMiniature>()

    override fun onCreateViewHolder(view: ViewGroup, type: Int): AncestorCoreViewHolder =
        AncestorCoreViewHolder.create(view, listener)

    override fun getItemCount() = data.size
    override fun onBindViewHolder(view: AncestorCoreViewHolder, pos: Int) {
        view.bind(data[pos])
    }

//    override fun getItemId(position: Int): Long {
//        return data[position].name.toLong()
//    }

    fun setDataSet(items: List<AncestorCore>) {
        data = items.mapIndexed { i, core -> AncestorCoreMiniature(miniatureSize, core) { notifyItemChanged(i) } }
    }

    fun removeItem(position: Int, core: AncestorCore) {
        val sizeBefore = data.size
        data = data.filter { it.core.name != core.name }
        val nbrRemoved = sizeBefore - data.size
        if (nbrRemoved == 1){
            notifyItemRemoved(position)
        } else {
            // As deletion only looks at name it is possible to remove several items at once
            // Todo make deletion look either at an id or implement equals
            notifyDataSetChanged()
        }
    }
}