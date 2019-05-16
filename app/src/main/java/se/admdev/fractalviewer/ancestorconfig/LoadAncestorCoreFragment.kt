package se.admdev.fractalviewer.ancestorconfig

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.fragment_load_ancestor_core.*
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreAdapter
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreViewHolder.AncestorCoreAction
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreViewHolder.AncestorCoreAction.*
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.showList
import java.lang.ref.WeakReference

class LoadAncestorCoreFragment : Fragment() {

    private lateinit var listAdapter: AncestorCoreAdapter
    private val sync: Synchronizer<List<AncestorCore>> = Synchronizer(AWAIT_ENTER_ANIMATION, AWAIT_PREFS_LOADING)

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

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (nextAnim != 0) {
            val anim = loadAnimation(activity, nextAnim)
            anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    if (enter) {
                        sync.onFinishedTask(AWAIT_ENTER_ANIMATION)
                    }
                }
            })

            anim
        } else {
            if (enter) {
                sync.onFinishedTask(AWAIT_ENTER_ANIMATION)
            }
            super.onCreateAnimation(transit, enter, nextAnim)
        }
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
        val switcher: ViewSwitcher? = list_empty_switcher
        val list: RecyclerView? = core_list
        switcher?.showList(false)
        (list?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        sync.action = { coreList ->
            switcher?.showList(coreList.isNotEmpty())
            listAdapter.setDataSet(coreList)
            list.adapter = listAdapter
        }

        activity?.let {
            val task = LoadCoreFromPrefsTask(it) { coreList ->
                sync.data = coreList
                sync.onFinishedTask(AWAIT_PREFS_LOADING)
            }
            task.execute()
        }
    }

    private class LoadCoreFromPrefsTask internal constructor(
        activity: Activity,
        val listener: (List<AncestorCore>) -> Unit
    ) : AsyncTask<Void, Void, List<AncestorCore>>() {
        val weakRefActivity = WeakReference<Activity>(activity)
        override fun doInBackground(vararg params: Void) = weakRefActivity.get()?.loadAncestorCores()
        override fun onPostExecute(result: List<AncestorCore>?) {
            listener.invoke(result.orEmpty())
        }
    }

    companion object {
        const val AWAIT_ENTER_ANIMATION = "awaitAnimation"
        const val AWAIT_PREFS_LOADING = "awaitPrefsLoading"
    }
}
