package se.admdev.fractalviewer

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_third_party_credits.*

class ThirdPartyCreditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_third_party_credits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val inputStream = resources.openRawResource(R.raw.license_gson) 
            // :D from inputStream.available() docs:
            // <p> Note that while some implementations of {@code InputStream } will return
            // the total number of bytes in the stream, many will not.  It is
            // never correct to use the return value of this method to allocate
            // a buffer intended to hold all data in this stream.
            val b = ByteArray(inputStream.available())
            inputStream.read(b)
            license_gson.text = String(b)
        } catch (e: Exception) {
            license_gson.text = ""
        }


        val background = view.background as AnimationDrawable
        view.startBackgroundAnimation(background)
    }
}
