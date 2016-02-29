package se.pvajscp.se.dragoncurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by ani on 2016-02-25.
 */
public class DragonCurveView extends View {
    private final Paint paint;
    private int currentIteration;

    private Point maxPos = new Point(1,1);
    private Point minPos = new Point(-1,-1);

    int width, height;

    Path path;

    private DragonCurve curve = new DragonCurve();

    public DragonCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(Color.parseColor("#330F445F"));

        final int strokeWidth = 2;
        final int dimension = 200;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.BLACK);

        path = new Path();

        post(new Runnable() {
            @Override
            public void run() {
                width = getWidth();
                height = getHeight();
                path.moveTo(width / 2, height / 2);
            }
        });

        final DragonCurveAnimation animation = new DragonCurveAnimation(this);
        animation.setDuration(9000000);
        startAnimation(animation);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Point newEnd = curve.getDirectionAt(currentIteration);
        Point newAbsPos = curve.getAbsolutePositionAt(currentIteration);
        maxPos.x = Math.max(newAbsPos.x, maxPos.x);
        maxPos.y = Math.max(newAbsPos.y, maxPos.y);
        minPos.x = Math.min(newAbsPos.x, minPos.x);
        minPos.y = Math.min(newAbsPos.y, minPos.y);

        path.rLineTo(newEnd.x, -newEnd.y);

        float xScale = (width/2)/(Math.max(maxPos.x, Math.abs(minPos.x)));
        float yScale = (height/2)/(Math.max(maxPos.y, Math.abs(minPos.y)));
        float scale = Math.min(xScale, yScale);
        Path pathToPaint = new Path(path);

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scale, scale,canvas.getWidth()/2,canvas.getHeight()/2);
        pathToPaint.transform(scaleMatrix);

        canvas.drawPath(pathToPaint, paint);
        currentIteration++;
    }



    public class DragonCurveAnimation extends Animation {

        private DragonCurveView dragonCurveView;

        public DragonCurveAnimation(DragonCurveView dragonCurveView) {
            this.dragonCurveView = dragonCurveView;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation transformation) {
            dragonCurveView.requestLayout();
        }
    }
}
