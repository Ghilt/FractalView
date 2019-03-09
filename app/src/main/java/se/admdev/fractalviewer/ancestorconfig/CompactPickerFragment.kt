package se.admdev.fractalviewer.ancestorconfig


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.dialog_compact_picker.*
import se.admdev.fractalviewer.ancestorconfig.model.CompactPickerItem
import java.lang.ClassCastException

private const val ARG_OPTIONS_DATA = "ArgOptionsData"

class CompactPickerFragment : DialogFragment(), CompactPickerAdapter.OptionClickListener {

    interface ItemSelectedListener {
        fun onItemClicked(item: CompactPickerItem)
    }

    private lateinit var data: List<CompactPickerItem>
    private var listener: ItemSelectedListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = activity as ItemSelectedListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: ClassCastException: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            val optionsData = getParcelableArrayList<CompactPickerItem>(ARG_OPTIONS_DATA)
            optionsData?.let{
                data = it
            }
        }

        if (!::data.isInitialized) {
            Log.e(TAG, "Attempt to start $TAG with no data")
            dialog.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(se.admdev.fractalviewer.R.layout.dialog_compact_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        options_grid.layoutManager = layoutManager

        options_grid.adapter = CompactPickerAdapter(data, this)
    }

    override fun onItemClicked(position: Int) {
        listener?.onItemClicked(data[position])
        dialog.dismiss()
    }

    companion object {

        const val TAG = "CompactPickerFragment"

        @JvmStatic
        fun newInstance(dataList: ArrayList<CompactPickerItem>) =
            CompactPickerFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_OPTIONS_DATA, dataList)
                }
            }
    }
}
