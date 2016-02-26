package se.pvajscp.se.dragoncurve;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    private static Handler delayHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View relativeLayoutBase = findViewById(R.id.relativeLayoutBase);

//        ImageView view = (ImageView )findViewById(R.id.shape_view);
//
//        ShapeDrawable shape = new ShapeDrawable(new ArcShape(270, 360));
//        shape.setIntrinsicHeight(400);
//        shape.setIntrinsicWidth(400);
//        shape.getPaint().setColor(Color.RED);
//        view.setImageDrawable(shape);
        DragonCurve curve = new DragonCurve();



        String stringCurve = "";
        String stringCurve2 = "";
        for(int i = 0; i< 5;i++){
//            stringCurve += curve.nextSegment();
            stringCurve += curve.getSegmentAbsolutePosition(i).x + ":" + curve.getSegmentAbsolutePosition(i).y+ " - " + curve.getDirectionAt(i) + " - " + curve.getTurnAt(i) +  "\n";
        }
        Log.d("spx", "go: " + stringCurve.equals(stringCurve2) + " " + stringCurve);
//        delayHandler.postDelayed(dragonCurve, 1000);

    }

}
