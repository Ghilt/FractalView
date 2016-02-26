package se.pvajscp.se.dragoncurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by ani on 2016-02-25.
 */
public class DragonCurveView extends View {
    private final Paint paint;
    private final RectF rect;
    private int currentIteration;

    private int posX = 100;
    private int posY = 100;

    private int segmentLength = 16;
    Path path;

    private DragonCurve curve = new DragonCurve();

    public DragonCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(Color.parseColor("#33FF445F"));

        final int strokeWidth = 1;
        final int dimension = 200;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLACK);

        path = new Path();
        path.moveTo(200, 600);

        //size 200x200 example
        rect = new RectF(strokeWidth, strokeWidth, dimension + strokeWidth, dimension + strokeWidth);

        final DragonCurveAnimation animation = new DragonCurveAnimation(this,4);
        animation.setDuration(50000);
        startAnimation(animation);
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("spx", " start anima");
//                animation.start();
//            }
//        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawLine(posX, posY, posX + segmentLength, posY + segmentLength, paint);
//        posX += segmentLength;
//        posY += segmentLength;
        Point newEnd = curve.getDirectionAt(currentIteration);

//        Log.d("spx",currentIteration+ " M: " + newEnd.x + " " +newEnd.y);
        path.rLineTo(newEnd.x * segmentLength, newEnd.y * segmentLength);

        canvas.drawPath(path, paint);
//        Log.d("spx",currentIteration + " start anima");
//        canvas.drawArc(rect, 0, 180, false, paint);
    }

    public void setCurrentIteration(int currentIteration) {
        this.currentIteration = currentIteration;
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    public class DragonCurveAnimation extends Animation {

        private DragonCurveView dragonCurveView;

        private int maxIterations;

        public DragonCurveAnimation(DragonCurveView dragonCurveView, int maxIterations) {
            this.dragonCurveView = dragonCurveView;
            this.maxIterations = maxIterations;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation transformation) {
            int iteration = (int) (interpolatedTime * maxIterations);
            dragonCurveView.setCurrentIteration(dragonCurveView.getCurrentIteration()+1);
            dragonCurveView.requestLayout();
        }
    }
}
