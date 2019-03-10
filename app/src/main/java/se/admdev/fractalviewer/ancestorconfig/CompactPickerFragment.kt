package se.admdev.fractalviewer.ancestorconfig


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
import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.Fragment

private const val ARG_OPTIONS_DATA = "argOptionsData"
private const val ARG_RETURN_REQUEST_CODE = "argReturnRequestCode"

class CompactPickerFragment<K : Parcelable, T : CompactPickerItem<K>> : DialogFragment(), CompactPickerAdapter.OptionClickListener {

    private lateinit var data: List<T>
    private var returnRequestCode: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            returnRequestCode = getInt(ARG_RETURN_REQUEST_CODE)
            val optionsData = getParcelableArrayList<T>(ARG_OPTIONS_DATA)
            optionsData?.let {
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
        val intent = Intent()
        intent.putExtra(EXTRA_SELECTED, data[position].content)
        targetFragment?.onActivityResult(
            targetRequestCode, returnRequestCode, intent
        )
        dialog.dismiss()
    }

    companion object {

        const val TAG = "CompactPickerFragment"
        const val EXTRA_SELECTED = "extraSelectedItem"

        @JvmStatic
        fun <K : Parcelable, T : CompactPickerItem<K>> newInstance(parentFragment: Fragment, dataList: ArrayList<T>, returnRequestCode: Int): CompactPickerFragment<K, T> {
            return CompactPickerFragment<K, T>().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_OPTIONS_DATA, dataList)
                    putParcelableArrayList(ARG_RETURN_REQUEST_CODE, dataList)
                }
                setTargetFragment(parentFragment, returnRequestCode)
            }
        }
    }
}
