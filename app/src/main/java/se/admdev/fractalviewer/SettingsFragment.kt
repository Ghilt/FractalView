package se.admdev.fractalviewer

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tutorial_button.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_tutorialFragment)
        }

        val background = view.background as AnimationDrawable
        view.startBackgroundAnimation(background)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}
