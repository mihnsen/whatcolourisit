package com.oe.app.whatcolourisit;

/*
Copyright 2014 minhnt@ownego.com
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import java.util.Calendar;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class WhatColourIsItService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new MyWallpaperEngine();
	}

	private class MyWallpaperEngine extends Engine {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};
		private boolean visible = true;

		public MyWallpaperEngine() {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(WhatColourIsItService.this);
			handler.post(drawRunner);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					
					Calendar c = Calendar.getInstance();
					int hours = c.get(Calendar.HOUR);
					int minutes = c.get(Calendar.MINUTE);
					int seconds = c.get(Calendar.SECOND);
					
					drawColourWatch(canvas, hours, minutes, seconds);
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			handler.removeCallbacks(drawRunner);
			if (visible) {
				handler.postDelayed(drawRunner, 1000);
			}
		}

		// Surface view requires that all elements are drawn completely
		private void drawColourWatch(Canvas canvas, int hours, int minutes, int seconds) {
			
			String strHours = hours>=10?""+hours:"0"+hours;
			String strMinutes = minutes>=10?""+minutes:"0"+minutes;
			String strSeconds = seconds>=10?""+seconds:"0"+seconds;
			
			String hexaColor = "#";
			hexaColor+= strHours;
			hexaColor+= strMinutes;
			hexaColor+= strSeconds;
			
			Paint mPaint = new Paint();
			Rect mRect = new Rect();
			
			mPaint.setColor(Color.parseColor(hexaColor));
			mRect.set(0, 0, canvas.getWidth(), canvas.getHeight());
			
			canvas.drawRect(mRect, mPaint);

			String text = strHours+" : "+strMinutes+" : "+strSeconds;
			drawWatchText(canvas, text);
			drawColorText(canvas, hexaColor);
		}
		
		private void drawWatchText(Canvas canvas, String text) {
			Paint paint = new Paint();
			Rect bounds = new Rect();
			
		    float textSize = 128f;

		    paint.setAntiAlias(true);
		    paint.setColor(Color.WHITE);
		    paint.setTextSize(textSize);
		    paint.setTextAlign(Align.CENTER);
		    paint.setLinearText(true);
		    
		    int x = (canvas.getWidth() / 2) - (bounds.width() / 2);
		    int y = (canvas.getHeight() / 2) - (bounds.height() / 2);
		    canvas.drawText(text, x, y, paint);
		}
		
		private void drawColorText(Canvas canvas, String color) {
			Paint paint = new Paint();
			Rect bounds = new Rect();
			
		    float textSize = 32f;

		    paint.setAntiAlias(true);
		    paint.setColor(Color.WHITE);
		    paint.setTextSize(textSize);
		    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		    paint.setTextAlign(Align.CENTER);
		    paint.setLinearText(true);
		    
		    int x = (canvas.getWidth() / 2) - (bounds.width() / 2);
		    int y = (canvas.getHeight() / 2) * 5/4;
		    canvas.drawText(color, x, y, paint);
		}
	}

}