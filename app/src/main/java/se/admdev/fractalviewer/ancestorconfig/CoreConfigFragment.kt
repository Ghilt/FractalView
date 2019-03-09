package se.admdev.fractalviewer.ancestorconfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.create_node_overlay.*
import kotlinx.android.synthetic.main.fragment_core_config.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile
import se.admdev.fractalviewer.ancestorconfig.model.CompactPickerItem
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode
import kotlin.collections.ArrayList

class CoreConfigFragment : Fragment(), AncestorTileAdapter.AncestorGridClickListener {

    private lateinit var model: ConfigViewModel
    private val adapter = AncestorTileAdapter()
    private val listAdapter = ConfigurationListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        model.configNodes.observe(this, Observer<List<ConfigNode>> { items ->
            listAdapter.setDataSet(items)
            listAdapter.notifyDataSetChanged()
        })

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> { items ->
            (ancestor_grid.layoutManager as GridLayoutManager).spanCount = model.ancestorTileDimension
            adapter.setDataSet(items)
            adapter.notifyDataSetChanged()
            toggleNodeCreationMode()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_core_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.containerSize = resources.getDimension(R.dimen.grid_size)
        adapter.listener = this

        ancestor_grid.adapter = adapter
        node_list.adapter = listAdapter

        ancestor_grid.layoutManager = object : GridLayoutManager(this.context, model.ancestorTileDimension) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }

        accept_selection_button.setOnClickListener {
            model.configNodes.addItem(ConfigNode(model.getTileSnapshot()))
            model.clearAncestorSelection()
        }

        plus_grid_size_button.setOnClickListener {
            model.increaseAncestorTiles()
        }

        minus_grid_size_button.setOnClickListener {
            model.decreaseAncestorTiles()
        }

        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.operand_list,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                operator_selection_spinner.adapter = adapter
            }

            ArrayAdapter.createFromResource(
                it,
                R.array.operand_list,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                operand_selection_spinner.adapter = adapter
            }
        }

        select_operand_button.setOnClickListener {
            val resource: Array<CharSequence> = resources.getTextArray(R.array.operand_list)
            val data: ArrayList<CompactPickerItem> = ArrayList(resource.toList().map { CompactPickerItem(it) })
            CompactPickerFragment.newInstance(data).show(fragmentManager, "PickOperandDialog")
        }

    }

    override fun onTileClicked(position: Int) {
        // going via viewModel into the tile observer works but you lose recycler animation magic
        //        model.ancestorTiles.triggerObserver()

        toggleNodeCreationMode()
        adapter.notifyItemChanged(position)
    }

    private fun toggleNodeCreationMode() {
        val editMode = if (model.hasSelectedTile()) View.VISIBLE else View.GONE
        node_creation_controls.visibility = editMode
    }

    companion object {
        @JvmStatic
        fun createInstance(): Fragment {
            return CoreConfigFragment()
        }
    }
}