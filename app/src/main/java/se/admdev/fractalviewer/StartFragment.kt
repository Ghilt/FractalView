package se.admdev.fractalviewer

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.view.*
import se.admdev.fractalviewer.ancestorconfig.ConfigViewModel

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
            Navigation.findNavController(v).navigate(R.id.action_startFragment_to_coreConfigFragment)
        }

        load_saved_config_button.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_startFragment_to_loadAncestorCoreFragment)
        }

        settings_button.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_startFragment_to_settingsFragment)
        }

        var background = view.flare_background_1.background as AnimationDrawable
        view.startBackgroundAnimation(background)

        background = view.flare_background_2.background as AnimationDrawable
        view.postDelayed({view.startBackgroundAnimation(background)}, 1800)

    }

    companion object {
        @JvmStatic
        fun newInstance() = StartFragment()
    }
}
