package se.admdev.fractalviewer.ancestorconfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_load_ancestor_core.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreAdapter
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreViewHolder.AncestorCoreAction
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreViewHolder.AncestorCoreAction.*
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.showList

class LoadAncestorCoreFragment : Fragment() {

    private lateinit var listAdapter: AncestorCoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_load_ancestor_core, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listAdapter = AncestorCoreAdapter(this::onAncestorCoreClicked)
    }

    private fun onAncestorCoreClicked(core: AncestorCore, action: AncestorCoreAction) {

        view?.let {
            when (action) {
                EDIT -> startConfigFragment(it, core)
                SHOW -> startFractalFragment(it, core)
                DELETE -> Navigation.findNavController(it).navigate(R.id.editFractal)
            }
        }
    }

    private fun startConfigFragment(view: View, core: AncestorCore) {
        val action = LoadAncestorCoreFragmentDirections.editFractal().apply {
            ancestorCore = core
        }
        Navigation.findNavController(view).navigate(action)
    }

    private fun startFractalFragment(view: View, core: AncestorCore) {
        val action = LoadAncestorCoreFragmentDirections.loadFractal().apply {
            ancestorCore = core
        }
        Navigation.findNavController(view).navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val coreList = activity.loadAncestorCores()
        list_empty_switcher.showList(coreList.isNotEmpty())
        listAdapter.setDataSet(coreList)
        core_list.adapter = listAdapter
    }
}
