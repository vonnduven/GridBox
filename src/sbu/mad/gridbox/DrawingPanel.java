package sbu.mad.gridbox;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback {

	private PanelThread _thread;
	GameGridManager gridMgr;
	int screenWidth, screenHeight;
	private Paint p;

	public DrawingPanel(Context context) {
		super(context); // Call super constructor
		getHolder().addCallback(this);

		// This bit gets us the resolution of our device screen
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics size = new DisplayMetrics();
		// display.getSize(size);
		display.getMetrics(size);

		screenWidth = size.widthPixels;
		screenHeight = size.heightPixels;

		int offset = 15;
		int squareArea = 4;

		// initialize the grid and the gridmanager
		SquareGrid grid = new SquareGrid(offset, offset+100, screenWidth - offset
				* 2, squareArea);
		gridMgr = new GameGridManager(grid);
		gridMgr.grid = grid;
		
		// setup the paint
		p = new Paint();
	}

	@Override
	public void onDraw(Canvas canvas) {
		// Clear the background to white
		p.setColor(Color.WHITE);
		canvas.drawRect(0, 0, screenWidth, screenHeight, p);

		// Render the grid and info
		gridMgr.grid.render(canvas);
		
		// Here is where you should render other objects (buttons, etc)
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		setWillNotDraw(false); // Allows us to use invalidate() to call onDraw()

		_thread = new PanelThread(getHolder(), this); // Start the thread that
		_thread.setRunning(true); // will make calls to
		_thread.start(); // onDraw()
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			_thread.setRunning(false); // Tells thread to stop
			_thread.join(); // Removes thread from mem.
		} catch (InterruptedException e) {
		}
	}

	class PanelThread extends Thread {
		private SurfaceHolder _surfaceHolder;
		//private DrawingPanel _panel;
		private boolean _run = false;

		public PanelThread(SurfaceHolder surfaceHolder, DrawingPanel panel) {
			_surfaceHolder = surfaceHolder;
			//_panel = panel;
		}

		public void setRunning(boolean run) { // Allow us to stop the thread
			_run = run;
		}

		@Override
		public void run() {
			Canvas c;
			while (_run) { // When setRunning(false) occurs, _run is
				c = null; // set to false and loop ends, stopping thread

				try {

					c = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {

						// Insert methods to modify positions of items in
						// onDraw()
						postInvalidate();

					}
				} finally {
					if (c != null) {
						_surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
}
