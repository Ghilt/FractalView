package se.admdev.fractalviewer

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_tutorial.*
import se.admdev.fractalviewer.ancestorconfig.adapter.AncestorCoreMiniature
import se.admdev.fractalviewer.ancestorconfig.model.*

class TutorialFragment : Fragment() {

    private var exampleCoreListItem: AncestorCoreMiniature? = null
    private var demoCoreListItem: AncestorCoreMiniature? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        example_1.iterations = ITERATIONS_OF_THUMBNAIL //Pre set size to prevent thumbnail fractal from blinking in
        val exampleCore = createExample1Fractal()
        exampleCoreListItem =
            AncestorCoreMiniature(ITERATIONS_OF_THUMBNAIL, exampleCore, ::onFinishLoadingExampleFractal)


        demo_grid_0.setOnClickListener { it.isSelected = !it.isSelected }
        demo_grid_1.setOnClickListener { it.isSelected = !it.isSelected }
        demo_grid_2.setOnClickListener { it.isSelected = !it.isSelected }
        demo_operand_increase.setOnClickListener { demo_operand.text = "${(demo_operand.text.toString().toInt() + 1)}" }
        demo_operand_decrease.setOnClickListener {
            //TODO trigger redraw everywhere; not just decrease
            //TODO prevent too small or too large numbers
            demo_operand.text = "${(demo_operand.text.toString().toInt() - 1)}"
            createDemoFractal()
        }

        val background = view.background as AnimationDrawable
        view.startBackgroundAnimation(background)
    }

    private fun createDemoFractal() {
        val getDemoSumValue: (Int, Int) -> Boolean = { x: Int, y: Int ->
            if (y == 2) {
                when (x) {
                    0 -> demo_grid_0.isSelected
                    1 -> demo_grid_1.isSelected
                    2 -> demo_grid_2.isSelected
                    else -> false
                }
            } else {
                false
            }
        }
        val demoConfigNode = GroupOperationConfigNode(
            'A',
            GroupOperator.SUM,
            (0..2).map { x -> (0..2).map { y -> AncestorTile(x, y, getDemoSumValue(x, y)) } },
            Operator.MODULO,
            Operand(demo_operand.text.toString())
        )

        val core = AncestorCore(listOf(demoConfigNode))

        demoCoreListItem = AncestorCoreMiniature(ITERATIONS_OF_THUMBNAIL, core, ::onFinishLoadingDemoFractal)
    }

    private fun createExample1Fractal(): AncestorCore {
        val exampleConfigNode = GroupOperationConfigNode(
            'A',
            GroupOperator.SUM,
            (0..2).map { x -> (0..2).map { y -> AncestorTile(x, y, true) } },
            Operator.MODULO,
            Operand("2")
        )

        return AncestorCore(listOf(exampleConfigNode))
    }

    private fun onFinishLoadingExampleFractal() {
        example_1.setFractalData(exampleCoreListItem?.miniatureData)
    }

    private fun onFinishLoadingDemoFractal() {
        demo_result.setFractalData(demoCoreListItem?.miniatureData)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TutorialFragment()

        const val ITERATIONS_OF_THUMBNAIL = 42

    }
}
