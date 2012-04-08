/*
 * TouchImageView.java
 * By: Michael Ortiz
 * Updated By: Patrick Lackemacher
 * -------------------
 * Extends Android ImageView to include pinch zooming and panning.
 */

package pt.up.beta.mobile.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	Matrix matrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 3f;
	float[] m;

	float redundantXSpace, redundantYSpace;

	float width, height;
	static final int CLICK = 3;
	float saveScale = 1f;
	float right, bottom, origWidth, origHeight, bmWidth, bmHeight;

	ScaleGestureDetector mScaleDetector;
	GestureDetector mDoubleTapDetector;
	OnTouchListener touchListener;
	Context context;

	public TouchImageView(Context context) {
		super(context);
		sharedConstructing(context);
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context) {
		super.setClickable(true);
		this.context = context;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
			mScaleDetector = new ScaleGestureDetector(context,
					new ScaleListener());
		mDoubleTapDetector = new GestureDetector(context, new GestureListener());
		matrix.setTranslate(1f, 1f);
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);

		setOnTouchListener(touchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mScaleDetector != null)
					mScaleDetector.onTouchEvent(event);
				mDoubleTapDetector.onTouchEvent(event);

				matrix.getValues(m);
				float x = m[Matrix.MTRANS_X];
				float y = m[Matrix.MTRANS_Y];
				PointF curr = new PointF(event.getX(), event.getY());

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					last.set(event.getX(), event.getY());
					start.set(last);
					mode = DRAG;
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						float deltaX = curr.x - last.x;
						float deltaY = curr.y - last.y;
						float scaleWidth = Math.round(origWidth * saveScale);
						float scaleHeight = Math.round(origHeight * saveScale);
						if (scaleWidth < width) {
							deltaX = 0;
							if (y + deltaY > 0)
								deltaY = -y;
							else if (y + deltaY < -bottom)
								deltaY = -(y + bottom);
						} else if (scaleHeight < height) {
							deltaY = 0;
							if (x + deltaX > 0)
								deltaX = -x;
							else if (x + deltaX < -right)
								deltaX = -(x + right);
						} else {
							if (x + deltaX > 0)
								deltaX = -x;
							else if (x + deltaX < -right)
								deltaX = -(x + right);

							if (y + deltaY > 0)
								deltaY = -y;
							else if (y + deltaY < -bottom)
								deltaY = -(y + bottom);
						}
						matrix.postTranslate(deltaX, deltaY);
						last.set(curr.x, curr.y);
					}
					break;

				case MotionEvent.ACTION_UP:
					mode = NONE;
					int xDiff = (int) Math.abs(curr.x - start.x);
					int yDiff = (int) Math.abs(curr.y - start.y);
					if (xDiff < CLICK && yDiff < CLICK)
						performClick();
					break;

				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				}
				setImageMatrix(matrix);
				invalidate();
				return true; // indicate event was handled
			}

		});
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		if (bm != null) {
			bmWidth = bm.getWidth();
			bmHeight = bm.getHeight();
		}
	}

	public void setMaxZoom(float x) {
		maxScale = x;
	}

	public boolean needsExternalZoom() {
		return mScaleDetector == null;
	}

	public void zoomIn() {
		zoom(1.5f, width / 2, height / 2);
		touchListener.onTouch(this,
				MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, width, height,
						0, 0, 0, 0, 0, 0, 0));
	}

	public void zoomOut() {
		zoom(0.67f, width / 2, height / 2);
		touchListener.onTouch(this,
				MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, width, height,
						0, 0, 0, 0, 0, 0, 0));
	}

	private void zoom(float mScaleFactor, float xOrig, float yOrig) {
		float origScale = saveScale;
		saveScale *= mScaleFactor;
		if (saveScale > maxScale) {
			saveScale = maxScale;
			mScaleFactor = maxScale / origScale;
		} else if (saveScale < minScale) {
			saveScale = minScale;
			mScaleFactor = minScale / origScale;
		}
		right = width * saveScale - width - (2 * redundantXSpace * saveScale);
		bottom = height * saveScale - height
				- (2 * redundantYSpace * saveScale);
		if (origWidth * saveScale <= width || origHeight * saveScale <= height) {
			matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2);
			if (mScaleFactor < 1) {
				matrix.getValues(m);
				float x = m[Matrix.MTRANS_X];
				float y = m[Matrix.MTRANS_Y];
				if (mScaleFactor < 1) {
					if (Math.round(origWidth * saveScale) < width) {
						if (y < -bottom)
							matrix.postTranslate(0, -(y + bottom));
						else if (y > 0)
							matrix.postTranslate(0, -y);
					} else {
						if (x < -right)
							matrix.postTranslate(-(x + right), 0);
						else if (x > 0)
							matrix.postTranslate(-x, 0);
					}
				}
			}
		} else {
			matrix.postScale(mScaleFactor, mScaleFactor, xOrig, yOrig);
			matrix.getValues(m);
			float x = m[Matrix.MTRANS_X];
			float y = m[Matrix.MTRANS_Y];
			if (mScaleFactor < 1) {
				if (x < -right)
					matrix.postTranslate(-(x + right), 0);
				else if (x > 0)
					matrix.postTranslate(-x, 0);
				if (y < -bottom)
					matrix.postTranslate(0, -(y + bottom));
				else if (y > 0)
					matrix.postTranslate(0, -y);
			}
		}
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mode = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = (float) Math.min(
					Math.max(.95f, detector.getScaleFactor()), 1.05);
			zoom(mScaleFactor, detector.getFocusX(), detector.getFocusY());
			return true;

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		// Fit to screen.
		float scale;
		float scaleX = (float) width / (float) bmWidth;
		float scaleY = (float) height / (float) bmHeight;
		scale = Math.min(scaleX, scaleY);
		matrix.setScale(scale, scale);
		setImageMatrix(matrix);
		saveScale = 1f;

		// Center the image
		redundantYSpace = (float) height - (scale * (float) bmHeight);
		redundantXSpace = (float) width - (scale * (float) bmWidth);
		redundantYSpace /= (float) 2;
		redundantXSpace /= (float) 2;

		matrix.postTranslate(redundantXSpace, redundantYSpace);

		origWidth = width - 2 * redundantXSpace;
		origHeight = height - 2 * redundantYSpace;
		right = width * saveScale - width - (2 * redundantXSpace * saveScale);
		bottom = height * saveScale - height
				- (2 * redundantYSpace * saveScale);
		setImageMatrix(matrix);
	}

	private OnTapListener listener;

	public void setOnTapListener(OnTapListener listener) {
		this.listener = listener;
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		public boolean onSingleTapUp(MotionEvent e) {

			float x = e.getX();
			float y = e.getY();
			// calculate inverse matrix
			Matrix inverse = new Matrix();
			getImageMatrix().invert(inverse);

			// map touch point from ImageView to image
			float[] touchPoint = new float[] { x, y };
			inverse.mapPoints(touchPoint);
			// touchPoint now contains x and y in image's coordinate system
			if (touchPoint[0] < 0 || touchPoint[0] > bmWidth)
				return false;
			if (touchPoint[1] < 0 || touchPoint[1] > bmHeight)
				return false;
			e.setLocation(touchPoint[0], touchPoint[1]);
			Log.d("Single Tap", "Tapped at: (" + touchPoint[0] + ","
					+ touchPoint[1] + ")");
			if (listener != null)
				listener.onSingleTapUp(e);
			return true;

		}

		// event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			// calculate inverse matrix
			Matrix inverse = new Matrix();
			getImageMatrix().invert(inverse);

			// map touch point from ImageView to image
			float[] touchPoint = new float[] { x, y };
			inverse.mapPoints(touchPoint);
			// touchPoint now contains x and y in image's coordinate system
			if (touchPoint[0] < 0 || touchPoint[0] > bmWidth)
				return false;
			if (touchPoint[1] < 0 || touchPoint[1] > bmHeight)
				return false;
			e.setLocation(touchPoint[0], touchPoint[1]);
			e.setAction(MotionEvent.ACTION_UP);
			Log.d("Double Tap", "Tapped at: (" + touchPoint[0] + ","
					+ touchPoint[1] + ")");
			if (listener != null)
				listener.onDoubleTap(e);
			return true;
		}
	}

	public interface OnTapListener {

		public boolean onDoubleTap(MotionEvent e);

		public boolean onSingleTapUp(MotionEvent e);
	}

}
