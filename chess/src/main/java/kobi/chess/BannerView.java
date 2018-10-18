package kobi.chess;

/*
Copyright 2011 by Kobi Krasnoff

This file is part of Pocket Chess.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class BannerView extends View {
	
	

	private Bitmap m_Banner = null;

	public BannerView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		
		m_Banner = BitmapFactory.decodeResource(getResources(), R.drawable.banner);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawBitmap(m_Banner, 0, 0, null);
		
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}
	
	private int measureWidth(int widthMeasureSpec)
	{
		int preferred = m_Banner.getWidth();
		return getMeasurement(widthMeasureSpec, preferred);
	}
	
	private int measureHeight(int heightMeasureSpec)
	{
		int preferred = m_Banner.getHeight();
		return getMeasurement(heightMeasureSpec, preferred);
	}
	
	private int getMeasurement(int measureSpec, int preferred)
	{
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement = 0;
		
		switch(MeasureSpec.getMode(measureSpec))
		{
		case MeasureSpec.EXACTLY:
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
		}
		
		return measurement;
	}
}
