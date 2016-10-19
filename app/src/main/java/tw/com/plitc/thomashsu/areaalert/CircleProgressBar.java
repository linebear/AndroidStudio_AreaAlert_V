package tw.com.plitc.thomashsu.areaalert;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class CircleProgressBar extends View {

	private float maxProgress = 300;
	private float progress = 0;
	private int colorbottom = 0x00acdd78, colortop = 0xff5baf56;
	private int progressStrokeWidth = 1;
	private int marxArcStorkeWidth = 15;
	//
	RectF oval;
	Paint paint;
	Canvas canvas;

	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		oval = new RectF();
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();
		this.canvas = canvas;
		width = (width > height) ? height : width;
		height = (width > height) ? height : width;

		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		canvas.drawColor(Color.TRANSPARENT);
		paint.setStrokeWidth(progressStrokeWidth);
		paint.setStyle(Style.STROKE);

		oval.left = marxArcStorkeWidth / 2;
		oval.top = marxArcStorkeWidth / 2;
		oval.right = width - marxArcStorkeWidth / 2;
		oval.bottom = height - marxArcStorkeWidth / 2;

		paint.setColor(colorbottom);
		paint.setStrokeWidth(marxArcStorkeWidth);
		canvas.drawArc(oval, 90, 360, false, paint);
		paint.setColor(colortop);
		paint.setStrokeWidth(marxArcStorkeWidth);
		canvas.drawArc(oval, 90, ((float) progress / maxProgress) * 360,
				false, paint); //

		float start = 90f;
		float p = 15f;
//		p = (int) (progress * p);
		for (int i = 0; i < p; i++) {
			paint.setColor(0xffffffff);
			// 绘制间隔快
			canvas.drawArc(oval, start + 360/15 - 2 ,2, false,paint);
			start = (start + 360/15);
		}

		paint.setStrokeWidth(1);
		int textHeight = height / 10;
		paint.setTextSize(textHeight);
		paint.setStyle(Style.FILL);




	}

	private int getDpValue(int w) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w,
				getContext().getResources().getDisplayMetrics());
	}


	public float getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}


	public void setProgress(float progress, int colortop,float maxProgress) {
		this.progress = progress;
		this.colortop = colortop;
		this.maxProgress = maxProgress;
		this.invalidate();
	}

	public void setProgressNotInUiThread(int progress, View view) {
		this.progress = progress;
		view.setAnimation(pointRotationAnima(0,
				(int) (((float) 360 / maxProgress) * progress)));
		this.postInvalidate();
	}

	/**
	 * 
	 * @param fromDegrees
	 * @param toDegrees
	 * @return
	 */
	private Animation pointRotationAnima(float fromDegrees, float toDegrees) {
		int initDegress = 306;// )
		RotateAnimation animation = new RotateAnimation(fromDegrees,
				initDegress + toDegrees, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(1);
		animation.setRepeatCount(1);
		animation.setFillAfter(true);
		return animation;
	}

}