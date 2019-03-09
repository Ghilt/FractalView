package se.admdev.fractalviewer.ancestorconfig

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.compact_picker_item.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.CompactPickerItem

class CompactPickerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: CompactPickerItem) {
        itemView.contentTextView.text = item.content
    }

    companion object {

        private const val LAYOUT = R.layout.compact_picker_item

        @JvmStatic
        fun create(
            parent: ViewGroup,
            listener: CompactPickerAdapter.OptionClickListener
        ): CompactPickerItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(LAYOUT, parent, false)
            return CompactPickerItemViewHolder(view).apply {
                itemView.setOnClickListener {
                    listener.onItemClicked(adapterPosition)
                }
            }
        }
    }
}