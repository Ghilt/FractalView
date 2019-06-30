package se.admdev.fractalviewer

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_start.*
import se.admdev.fractalviewer.ancestorconfig.isFirstTimeUser

class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configure_button.setOnClickListener { v ->
            if (activity.isFirstTimeUser()) {
                Navigation.findNavController(v).navigate(R.id.action_startFragment_to_tutorialFragment)
            } else {
                Navigation.findNavController(v).navigate(R.id.action_startFragment_to_coreConfigFragment)
            }
        }

        load_saved_config_button.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_startFragment_to_loadAncestorCoreFragment)
        }

        settings_button.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_startFragment_to_settingsFragment)
        }

        splash_image.playAnimatedDrawable(R.drawable.start_splash_anim)

        val gridBackground = container.background as AnimationDrawable
        view.startBackgroundAnimation(gridBackground)
    }

    companion object {
        @JvmStatic
        fun newInstance() = StartFragment()
    }
}
