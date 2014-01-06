package com.asiacom.capturecamera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.asiacom.capturecamera.R;

/**
 * 进行框选的视图，可以用手指控制框选范围的大小
 * @author asiacom104
 */
public class CaptureView extends View {

	/**
	 * 触摸位置及动作 框体外部 1 框体左边缘 2 框体右边缘 4 框体上边缘 8 框体下边缘 16 框体移动 32
	 * */
	public static final int GROW_NONE = (1 << 0);
	public static final int GROW_LEFT_EDGE = (1 << 1);
	public static final int GROW_RIGHT_EDGE = (1 << 2);
	public static final int GROW_TOP_EDGE = (1 << 3);
	public static final int GROW_BOTTOM_EDGE = (1 << 4);
	public static final int GROW_MOVE = (1 << 5);

	// 捕获框体外部画笔
	private Paint outsideCapturePaint = new Paint();
	// 边框画笔
	private Paint lineCapturePaint = new Paint();
	// 可视范围
	private Rect viewRect;
	// 框体范围
	private RectF captureRect;
	// 触摸的边缘
	private int mMotionEdge;
	// 上次触摸的坐标
	private float mLastX, mLastY;

	// 水平拉伸箭头
	private Drawable horStretchArrows;
	// 垂直拉伸箭头
	private Drawable verStretchArrows;
	// 水平拉伸箭头的宽
	private int horStretchArrowsHalfWidth;
	// 水平拉伸箭头的高
	private int horStretchArrowsHalfHeigth;
	// 垂直拉伸箭头的宽
	private int verStretchArrowsHalfWidth;
	// 垂直拉伸箭头的高
	private int verStretchArrowsHalfHeigth;

	private CaptureView mCaptureView;

	private boolean rectMoveable;

	// 枚举动作类型：无、移动、拉伸
	private enum ActionMode {
		None, Move, Grow
	}

	private ActionMode mMode = ActionMode.None;
	
	
	
	public CaptureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CaptureView(Context context) {
		super(context);
	}

	public CaptureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 禁止硬件加速（不加报错运行不了）
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		// 捕获框边框画笔大小
		lineCapturePaint.setStrokeWidth(3F);
		lineCapturePaint.setStyle(Paint.Style.STROKE);
		// 画笔风格:空心
		lineCapturePaint.setAntiAlias(true);
		// 抗锯齿
		lineCapturePaint.setColor(Color.RED);
		// 画笔颜色
		Resources resources = context.getResources();

		/**
		 * 拉伸图片
		 * */
		horStretchArrows = resources.getDrawable(R.drawable.hor_stretch_arrows);
		verStretchArrows = resources.getDrawable(R.drawable.ver_stretch_arrows);

		/**
		 * 水平拉伸箭头的宽高
		 * */
		horStretchArrowsHalfWidth = horStretchArrows.getIntrinsicWidth() / 2;
		horStretchArrowsHalfHeigth = horStretchArrows.getIntrinsicHeight() / 2;

		/**
		 * 竖直拉伸箭头的宽高
		 * */
		verStretchArrowsHalfWidth = verStretchArrows.getIntrinsicWidth() / 2;
		verStretchArrowsHalfHeigth = verStretchArrows.getIntrinsicHeight() / 2;
		// 默认为全屏模式
		setFullScreen(true);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// 初始化可视范围及框体大小
		viewRect = new Rect(left, top, right, bottom);
		int viewWidth = right - left;
		int viewHeight = bottom - top;
		int captureWidth = Math.min(viewWidth, viewHeight) * 3 / 5;
		int captureHeight = viewHeight * 2 / 5;
		// 将框体绘制在可视范围中间位置
		int captureX = (viewWidth - captureWidth) / 2;
		int captureY = (viewHeight - captureHeight) / 2;
		captureRect = new RectF(captureX, captureY, captureX + captureWidth,
				captureY + captureHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		Path path = new Path();
		// 顺时针闭合框体
		path.addRect(new RectF(captureRect), Path.Direction.CW);
		canvas.clipPath(path, Region.Op.DIFFERENCE);
		// 绘制框体外围区域
		canvas.drawRect(viewRect, outsideCapturePaint);
		// 绘制框体
		canvas.drawPath(path, lineCapturePaint);
		canvas.restore();
		// 拉伸操作时，绘制框体箭头
		if (mMode == ActionMode.Grow) {
			// 框体中间X坐标
			float xMiddle = captureRect.left + captureRect.width() / 2;
			// 框体中间Y坐标
			float yMiddle = captureRect.top + captureRect.height() / 2;

			// 框体左边的箭头
			horStretchArrows.setBounds((int) captureRect.left
					- horStretchArrowsHalfWidth, (int) yMiddle
					- horStretchArrowsHalfHeigth, (int) captureRect.left
					+ horStretchArrowsHalfWidth, (int) yMiddle
					+ horStretchArrowsHalfHeigth);
			horStretchArrows.draw(canvas);

			// 框体右边的箭头
			horStretchArrows.setBounds((int) captureRect.right
					- horStretchArrowsHalfWidth, (int) yMiddle
					- horStretchArrowsHalfHeigth, (int) captureRect.right
					+ horStretchArrowsHalfWidth, (int) yMiddle
					+ horStretchArrowsHalfHeigth);
			horStretchArrows.draw(canvas);

			// 框体上方的箭头
			verStretchArrows.setBounds((int) xMiddle
					- verStretchArrowsHalfWidth, (int) captureRect.top
					- verStretchArrowsHalfHeigth, (int) xMiddle
					+ verStretchArrowsHalfWidth, (int) captureRect.top
					+ verStretchArrowsHalfHeigth);
			verStretchArrows.draw(canvas);

			// 框体下方的箭头
			verStretchArrows.setBounds((int) xMiddle
					- verStretchArrowsHalfWidth, (int) captureRect.bottom
					- verStretchArrowsHalfHeigth, (int) xMiddle
					+ verStretchArrowsHalfWidth, (int) captureRect.bottom
					+ verStretchArrowsHalfHeigth);
			verStretchArrows.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 锁定当前触摸事件的操作对象，直到ACTION_UP，如果没有锁定，有grow为前次操作的值。
			int grow = getGrow(event.getX(), event.getY());
			if (grow != GROW_NONE) {
				mCaptureView = CaptureView.this;
				mMotionEdge = grow;
				mLastX = event.getX();
				mLastY = event.getY();
				mCaptureView.setMode((grow == GROW_MOVE) ? ActionMode.Move
						: ActionMode.Grow);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mCaptureView != null) {
				setMode(ActionMode.None);
				// 释放当前锁定的操作对象
				mCaptureView = null;
			}

			break;
		// 框体移动
		case MotionEvent.ACTION_MOVE:
			if (mCaptureView != null) {
				handleMotion(mMotionEdge, event.getX() - mLastX, event.getY()
						- mLastY);
				mLastX = event.getX();
				mLastY = event.getY();
			}
			break;
		}
		return true;
	}
	
	public void setRectLineColor(int color) {
		lineCapturePaint.setColor(color);
	}
	
	/**
	 * 默认框体不可移动。如果设置为true,那么框体可以随手指移动。
	 * @param rectMoveable
	 */
	public void setRectMoveable(boolean rectMoveable) {
		this.rectMoveable = rectMoveable;
	}

	/**
	 * 设置全屏,默认全屏
	 * @param true为全屏，框体外透明，false框体外为黑色
	 */
	public void setFullScreen(boolean full) {
		// 全屏，则把外部框体颜色设为透明
		if (full) {
			outsideCapturePaint.setARGB(100, 50, 50, 50);
		} else {
			// 只显示框体区域，框体外部为全黑
			outsideCapturePaint.setARGB(255, 0, 0, 0);
		}
	}

	private void setMode(ActionMode mode) {
		if (mode != mMode) {
			mMode = mode;
			invalidate();
		}
	}

	// 确定触摸位置及动作，分别为触摸框体外围和框体上、下、左、右边缘以及框体内部
	private int getGrow(float x, float y) {
		// 触摸的有效范围大小
		final float effectiveRange = 20F;
		int grow = GROW_NONE;
		float left = captureRect.left;
		float top = captureRect.top;
		float right = captureRect.right;
		float bottom = captureRect.bottom;

		/**
		 * 垂直检测
		 * */
		boolean verticalCheck = (y >= top - effectiveRange)
				&& (y < bottom + effectiveRange);

		/**
		 * 水平检测
		 * */
		boolean horizCheck = (x >= left - effectiveRange)
				&& (x < right + effectiveRange);

		// 触摸了框体左边缘
		if ((Math.abs(left - x) < effectiveRange) && verticalCheck) {
			grow |= GROW_LEFT_EDGE;
		}

		// 触摸了框体右边缘
		if ((Math.abs(right - x) < effectiveRange) && verticalCheck) {
			grow |= GROW_RIGHT_EDGE;
		}

		// 触摸了框体上边缘
		if ((Math.abs(top - y) < effectiveRange) && horizCheck) {
			grow |= GROW_TOP_EDGE;
		}

		// 触摸了框体下边缘
		if ((Math.abs(bottom - y) < effectiveRange) && horizCheck) {
			grow |= GROW_BOTTOM_EDGE;
		}

		// 触摸框体内部

		if (grow == GROW_NONE && captureRect.contains((int) x, (int) y)) {
			grow = GROW_MOVE;
		}

		return grow;
	}

	// 处理触摸事件，判断移动框体还是伸缩框体
	private void handleMotion(int grow, float dx, float dy) {
		if (grow == GROW_NONE) {
			return;
		} else if (grow == GROW_MOVE && rectMoveable) {
			// 移动框体
			moveBy(dx, dy);
		} else {
			if (((GROW_LEFT_EDGE | GROW_RIGHT_EDGE) & grow) == 0) {
				// 水平不伸缩
				dx = 0;
			}

			if (((GROW_TOP_EDGE | GROW_BOTTOM_EDGE) & grow) == 0) {
				// 垂直不伸缩
				dy = 0;
			}
			growBy((((grow & GROW_LEFT_EDGE) != 0) ? -1 : 1) * dx,
					(((grow & GROW_TOP_EDGE) != 0) ? -1 : 1) * dy);
		}
	}

	private void moveBy(float dx, float dy) {
		RectF invalRect = new RectF(captureRect);
		captureRect.offset(dx, dy);
		captureRect.offset(Math.max(0, viewRect.left - captureRect.left),
				Math.max(0, viewRect.top - captureRect.top));
		captureRect.offset(Math.min(0, viewRect.right - captureRect.right),
				Math.min(0, viewRect.bottom - captureRect.bottom));

		// 更新围绕本身区域和指定的区域,清除移动滞留的痕迹
		invalRect.union(captureRect);
		invalRect.inset(-100, -100);
		// 重绘指定区域
		invalidate((int)invalRect.left,(int)invalRect.top,(int)invalRect.right,(int)invalRect.bottom);
	}

	private void growBy(float dx, float dy) {
		// captureRect最小宽度
		float widthCap = 50F;
		// captureRect最小高度
		float heightCap = 50F;

		RectF r = new RectF(captureRect);

		// 当captureRect拉伸到宽度 = viewRect的宽度时，则调整dx的值为 0
		if (dx > 0F && r.width() + 2 * dx >= viewRect.width()) {
			dx = 0F;
		}
		// 同上
		if (dy > 0F && r.height() + 2 * dy >= viewRect.height()) {
			dy = 0F;
		}
		// 框体边缘外移
		r.inset(-dx, -dy);

		// 当captureRect缩小到宽度 = widthCap时
		if (r.width() <= widthCap) {
			r.inset(-(widthCap - r.width()) / 2F, 0F);
		}

		// 同上
		if (r.height() <= heightCap) {
			r.inset(0F, -(heightCap - r.height()) / 2F);
		}

		if (r.left < viewRect.left) {
			r.offset(viewRect.left - r.left, 0F);
		} else if (r.right > viewRect.right) {
			r.offset(-(r.right - viewRect.right), 0);
		}
		if (r.top < viewRect.top) {
			r.offset(0F, viewRect.top - r.top);
		} else if (r.bottom > viewRect.bottom) {
			r.offset(0F, -(r.bottom - viewRect.bottom));
		}

		captureRect.set(r.left, r.top, r.right, r.bottom);
		invalidate();
	}

	public RectF getCaptureRect() {
		return captureRect;
	}
}
