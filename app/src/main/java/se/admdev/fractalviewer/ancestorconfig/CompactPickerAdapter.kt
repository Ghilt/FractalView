package se.admdev.fractalviewer.ancestorconfig

import android.os.Parcelable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.admdev.fractalviewer.ancestorconfig.model.CompactPickerItem

class CompactPickerAdapter<T : Parcelable>(
    private val data: List<CompactPickerItem<T>>,
    private val listener: OptionClickListener
) : RecyclerView.Adapter<CompactPickerItemViewHolder>() {

    interface OptionClickListener {
        fun onItemClicked(position: Int)
    }

    override fun onCreateViewHolder(view: ViewGroup, type: Int) =
        CompactPickerItemViewHolder.create(view, listener)

    override fun getItemCount() = data.size
    override fun onBindViewHolder(view: CompactPickerItemViewHolder, pos: Int) =
        view.bind(data[pos])

}