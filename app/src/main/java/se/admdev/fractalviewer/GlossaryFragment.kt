package se.admdev.fractalviewer

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_glossary.*
import se.admdev.fractalviewer.ancestorconfig.adapter.GlossaryListAdapter
import se.admdev.fractalviewer.ancestorconfig.model.GroupOperator
import se.admdev.fractalviewer.ancestorconfig.model.Operator

class GlossaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_glossary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterGroupOperator = GlossaryListAdapter()
        adapterGroupOperator.setDataSet(GroupOperator.values().toList())
        group_operator_recycler.adapter = adapterGroupOperator

        val adapterMathematicalOperator = GlossaryListAdapter()
        adapterMathematicalOperator.setDataSet(Operator.MATHEMATICAL_OPERATORS)
        mathematical_operator_recycler.adapter = adapterMathematicalOperator

        val adapterLogicalOperator = GlossaryListAdapter()
        adapterLogicalOperator.setDataSet(Operator.values().toList() - Operator.MATHEMATICAL_OPERATORS)
        logical_operator_recycler.adapter = adapterLogicalOperator

        val background = view.background as AnimationDrawable
        view.startBackgroundAnimation(background)
    }
}
