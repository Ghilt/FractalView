package se.admdev.fractalviewer

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_core_config.*
import se.admdev.fractalviewer.model.ConfigurationNode

class CoreConfigFragment : Fragment(), ConfigurationAdapter.AncestorGridClickListener {

    private lateinit var model: ConfigurationViewModel
    private val adapter = ConfigurationAdapter()
    private val listAdapter = ConfigurationListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(ConfigurationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        model.ancestorGridSize.observe(this, Observer<Int> { size ->
            (ancestor_grid.layoutManager as GridLayoutManager).spanCount = size
            adapter.size = size
            adapter.notifyDataSetChanged()
            Log.d("spx", "Span: " + adapter.size)
        })

        model.configNodes.observe(this, Observer<List<ConfigurationNode>> { items ->
            listAdapter.setDataSet(items)
            listAdapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_core_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.containerSize = resources.getDimension(R.dimen.grid_size) // measured width or common resource(then put in constructor).... 200 dp now
        adapter.listener = this

        ancestor_grid.adapter = adapter
        node_list.adapter = listAdapter

        ancestor_grid.layoutManager = object : GridLayoutManager(this.context, adapter.size) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }

        accept_selection_button.setOnClickListener {
            dim.visibility = View.GONE
            accept_selection_button.visibility = View.GONE
            model.configNodes.addItem(ConfigurationNode())

        }

        plus.setOnClickListener {
            model.ancestorGridSize.value = model.ancestorGridSize.value?.plus(2)
        }

        minus.setOnClickListener {
            model.ancestorGridSize.value = model.ancestorGridSize.value?.minus(2)
        }
    }

    override fun onTileClicked(position: Int) {
        dim.visibility = View.VISIBLE
        accept_selection_button.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic fun createInstance(): Fragment {
            return CoreConfigFragment()
        }
    }
}