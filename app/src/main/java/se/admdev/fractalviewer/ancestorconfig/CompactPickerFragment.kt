package se.admdev.fractalviewer.ancestorconfig


import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.dialog_compact_picker.*
import se.admdev.fractalviewer.ancestorconfig.model.CompactPickerItem
import se.admdev.fractalviewer.ancestorconfig.model.Operand
import se.admdev.fractalviewer.viewVisibility

private const val ARG_OPTIONS_DATA = "argOptionsData"
private const val ARG_RETURN_REQUEST_CODE = "argReturnRequestCode"
private const val ARG_FREE_FORM_INPUT = "argFreeFormInput"

class CompactPickerFragment<T : Parcelable> : DialogFragment(), CompactPickerAdapter.OptionClickListener {

    private lateinit var data: List<CompactPickerItem<T>>
    private var freeFormInput = false
    private var returnRequestCode: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            returnRequestCode = getInt(ARG_RETURN_REQUEST_CODE)
            freeFormInput = getBoolean(ARG_FREE_FORM_INPUT)
            val optionsData = getParcelableArrayList<CompactPickerItem<T>>(ARG_OPTIONS_DATA)
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

        val inputText = free_form_edit_text

        inputText.visibility = if (freeFormInput) View.VISIBLE else View.GONE

        inputText.addTextChangedListener( object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val editMode = (!inputText.text.isEmpty()).viewVisibility
                confirm_pick_button.visibility = editMode
                dimming_overlay.visibility = editMode
            }
        })

        confirm_pick_button.setOnClickListener {
            val intent = Intent()
            intent.putExtra(EXTRA_SELECTED, Operand(inputText.text.toString()))
            targetFragment?.onActivityResult(
                targetRequestCode, returnRequestCode, intent
            )
            dialog.dismiss()
        }
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
        fun <T : Parcelable> newInstance(
            parentFragment: Fragment,
            dataList: ArrayList<CompactPickerItem<T>>,
            freeFormInput: Boolean,
            returnRequestCode: Int
        ): CompactPickerFragment<T> {
            return CompactPickerFragment<T>().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_OPTIONS_DATA, dataList)
                    putInt(ARG_RETURN_REQUEST_CODE, returnRequestCode)
                    putBoolean(ARG_FREE_FORM_INPUT, freeFormInput)
                }
                setTargetFragment(parentFragment, returnRequestCode)
            }
        }
    }
}
