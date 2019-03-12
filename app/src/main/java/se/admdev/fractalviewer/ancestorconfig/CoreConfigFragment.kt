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
        } ?: throw Throwable("Invalid Activity")

        model.configNodes.observe(this, Observer<List<ConfigNode>> { items ->
            listAdapter.setDataSet(items)
            listAdapter.notifyDataSetChanged()
        })

        model.ancestorTiles.observe(this, Observer<List<List<AncestorTile>>> { items ->
            (ancestor_grid.layoutManager as GridLayoutManager).spanCount = model.ancestorTileDimension
            adapter.setDataSet(items) // No longer get adapter animations for free, could calculate diff here and not reset
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

        plus_grid_size_button.setOnClickListener {
            model.increaseAncestorTiles()
        }

        minus_grid_size_button.setOnClickListener {
            model.decreaseAncestorTiles()
        }
    }

    override fun onTileClicked(position: Int) {
        model.ancestorTiles.triggerObserver()
    }

    private fun updateNodeCreationMode() {
        val editMode = if (model.hasSelectedTile()) View.VISIBLE else View.GONE
        dimming_overlay.visibility = editMode

        if (model.hasSelectedTile() && !isCreateNodeFragmentShown()) {
            fragmentManager?.apply {
                beginTransaction()
                    .add(R.id.create_node_frame, CreateNodeFragment.newInstance(), CreateNodeFragment.TAG)
                    .addToBackStack(CreateNodeFragment.TAG)
                    .commit()
            }
        }
    }

    private fun isCreateNodeFragmentShown() = fragmentManager?.findFragmentByTag(CreateNodeFragment.TAG) != null

    companion object {

        @JvmStatic
        fun createInstance(): Fragment {
            return CoreConfigFragment()
        }
    }
}