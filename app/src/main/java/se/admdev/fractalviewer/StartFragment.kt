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

        ViewModelProviders.of(requireActivity()).get(ConfigViewModel::class.java)

        configure_button.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_startFragment_to_coreConfigFragment)
        }

        load_saved_config_button.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_startFragment_to_loadAncestorCoreFragment)
        }

        start_tutorial_button.setOnClickListener { v ->
            // TODO Tutorial fragment
        }

        val gridBackground = view.flare_background.background as AnimationDrawable
        view.startBackgroundAnimation(gridBackground)

    }

    companion object {
        @JvmStatic
        fun newInstance() = StartFragment()
    }
}
