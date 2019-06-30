package se.admdev.fractalviewer.ancestorconfig

import android.app.Activity
import android.graphics.drawable.AnimationDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.appcompat.app.AlertDialog
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
import se.admdev.fractalviewer.startBackgroundAnimation
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
        listAdapter = AncestorCoreAdapter(ITERATIONS_OF_THUMBNAIL, this::onAncestorCoreClicked)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sync.clear()
        super.onViewCreated(view, savedInstanceState)
        val switcher: ViewSwitcher? = list_empty_switcher
        val list: RecyclerView? = core_list
        val emptyText: TextView? = empty_view

        switcher?.showList(false)
        (list?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        sync.action = { coreList ->
            list.post {
                // WORKAROUND: This is needed for the animation show up properly on some devices
                switcher?.showList(coreList.isNotEmpty())
                emptyText?.setText(R.string.start_load_configuration_no_saved)
                listAdapter.setDataSet(coreList)
                list.adapter = listAdapter
                listAdapter.notifyDataSetChanged()
                list.scheduleLayoutAnimation()
            }
        }

        activity?.let {
            val task = LoadCoreFromPrefsTask(it) { coreList ->
                sync.data = coreList
                sync.onFinishedTask(AWAIT_PREFS_LOADING)
            }
            task.execute()
        }

        val gridBackground = container.background as AnimationDrawable
        view.startBackgroundAnimation(gridBackground)
    }

    private fun onAncestorCoreClicked(adapterPos: Int, core: AncestorCore, action: AncestorCoreAction) {

        view?.let {
            when (action) {
                EDIT -> startConfigFragment(it, core)
                SHOW -> startFractalFragment(it, core)
                DELETE -> deleteFractal(adapterPos, core)
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

    private fun deleteFractal(adapterPos: Int, core: AncestorCore) {
        context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(it.getString(R.string.dialog_delete_fractal_title, core.name))

            builder.setPositiveButton(R.string.general_yes) { _, _ ->
                activity.deleteAncestorCore(core)
                listAdapter.removeItem(adapterPos, core)
                list_empty_switcher?.showList(listAdapter.itemCount != 0)
            }
            builder.setNegativeButton(R.string.general_cancel) { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }

    private class LoadCoreFromPrefsTask internal constructor(
        activity: Activity,
        val listener: (List<AncestorCore>) -> Unit
    ) : AsyncTask<Void, Void, List<AncestorCore>>() {
        val weakRefActivity = WeakReference(activity)
        override fun doInBackground(vararg params: Void) = weakRefActivity.get()?.loadAncestorCores()
        override fun onPostExecute(result: List<AncestorCore>?) {
            listener.invoke(result.orEmpty())
        }
    }

    companion object {
        const val AWAIT_ENTER_ANIMATION = "awaitAnimation"
        const val AWAIT_PREFS_LOADING = "awaitPrefsLoading"
        const val ITERATIONS_OF_THUMBNAIL = 11
    }
}
