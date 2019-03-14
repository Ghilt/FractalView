package se.admdev.fractalviewer.ancestorconfig

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.compact_picker_item.view.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.CompactPickerItem

class CompactPickerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun <T : Parcelable> bind(item: CompactPickerItem<T>) {
        itemView.content_text_view.text = item.name
        item.decorator?.invoke(itemView.content_text_view)
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