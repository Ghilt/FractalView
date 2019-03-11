package se.admdev.fractalviewer.ancestorconfig

import android.content.Intent
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
import se.admdev.fractalviewer.ancestorconfig.CompactPickerFragment.Companion.EXTRA_SELECTED
import se.admdev.fractalviewer.ancestorconfig.model.*

private const val REQUEST_CODE_OPERATOR_PICKER = 0

class CoreConfigFragment : Fragment(), AncestorTileAdapter.AncestorGridClickListener {

    private lateinit var model: ConfigViewModel
    private val adapter = AncestorTileAdapter()
    private val creationGridAdapter = AncestorTileAdapter()
    private val listAdapter = ConfigurationListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(ConfigViewModel::class.java)
        } ?: throw Throwable("Invalid Activity")

        model.configNodes.observe(this, Observer<List<ConfigNode>> { items ->
            listAdapter.setDataSet(items)
            listAdapter.notifyDataSetChanged()
        })

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> { items ->
            (ancestor_grid.layoutManager as GridLayoutManager).spanCount = model.ancestorTileDimension
            adapter.setDataSet(items)
            adapter.notifyDataSetChanged()
            updateNodeCreationMode()
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

        select_operator_button.setOnClickListener {
            val data = ArrayList(Operator.values()
                .map { CompactPickerItem(it) {symbol} })
            CompactPickerFragment.newInstance(this, data, REQUEST_CODE_OPERATOR_PICKER)
                .show(fragmentManager, "PickOperatorDialog")
        }

        creationGridAdapter.containerSize = resources.getDimension(R.dimen.grid_size_miniature)
        ancestor_grid_edit_node_creation.adapter = creationGridAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_OPERATOR_PICKER -> onOperatorSelected(data?.getParcelableExtra(EXTRA_SELECTED))
        }
    }

    override fun onTileClicked(position: Int) {
        // going via viewModel into the tile observer works but you lose recycler animation magic
        // model.ancestorTiles.triggerObserver()
        updateNodeCreationMode()
        adapter.notifyItemChanged(position)
    }

    private fun onOperatorSelected(op: Operator?) {
        select_operator_button.text = op?.symbol
    }

    private fun updateNodeCreationMode() {
        val editMode = if (model.hasSelectedTile()) View.VISIBLE else View.GONE
        node_creation_controls.visibility = editMode

        val snap = model.getTileSnapshot()
        (ancestor_grid_edit_node_creation.layoutManager as GridLayoutManager).spanCount = snap.size
        creationGridAdapter.setDataSet(snap)
        creationGridAdapter.notifyDataSetChanged()
    }

    companion object {

        @JvmStatic
        fun createInstance(): Fragment {
            return CoreConfigFragment()
        }
    }
}