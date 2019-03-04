package se.admdev.fractalviewer.ancestorconfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_core_config.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.model.AncestorTile
import se.admdev.fractalviewer.ancestorconfig.model.ConfigNode

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
            val editMode = if (model.hasSelectedTile()) View.VISIBLE else View.GONE
            dim.visibility = editMode
            accept_selection_button.visibility = editMode
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

        plus.setOnClickListener {
            model.increaseAncestorTiles()
        }

        minus.setOnClickListener {
            model.decreaseAncestorTiles()
        }
    }

    override fun onTileClicked(position: Int) {
        // going via viewModel into the tile observer works but you lose recycler animation magic
        //        model.ancestorTiles.triggerObserver()

        val editMode = if (model.hasSelectedTile()) View.VISIBLE else View.GONE
        dim.visibility = editMode
        accept_selection_button.visibility = editMode
        adapter.notifyItemChanged(position)
    }

    companion object {
        @JvmStatic
        fun createInstance(): Fragment {
            return CoreConfigFragment()
        }
    }
}