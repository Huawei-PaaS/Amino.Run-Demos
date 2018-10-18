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

import java.util.Hashtable;

public class BoardView extends View {
	private Bitmap m_Board = null;
	private Bitmap[] m_ChessPieces;
	private Hashtable<String, Integer> pieces = new Hashtable<String, Integer>();
	private char[][] piecesArray;
	private MainActivity parent;
	private boolean isMoveNow = false;
	private Point startPoint, movePoint;
	
	public BoardView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		
		//Builds Hash Table
		pieces.put("R", 0);
		pieces.put("N", 1);
		pieces.put("B", 2);
		pieces.put("Q", 3);
		pieces.put("K", 4);
		pieces.put("P", 5);
		pieces.put("r", 6);
		pieces.put("n", 7);
		pieces.put("b", 8);
		pieces.put("q", 9);
		pieces.put("k", 10);
		pieces.put("p", 11);
		
		m_Board = BitmapFactory.decodeResource(getResources(), R.drawable.chessboard);
		
		m_ChessPieces = new Bitmap[12];
		m_ChessPieces[pieces.get("K")] = BitmapFactory.decodeResource(getResources(), R.drawable.king_white);
		m_ChessPieces[pieces.get("Q")] = BitmapFactory.decodeResource(getResources(), R.drawable.queen_white);
		m_ChessPieces[pieces.get("R")] = BitmapFactory.decodeResource(getResources(), R.drawable.rook_white);
		m_ChessPieces[pieces.get("B")] = BitmapFactory.decodeResource(getResources(), R.drawable.bishop_white);
		m_ChessPieces[pieces.get("N")] = BitmapFactory.decodeResource(getResources(), R.drawable.knight_white);
		m_ChessPieces[pieces.get("P")] = BitmapFactory.decodeResource(getResources(), R.drawable.pawn_white);
		
		m_ChessPieces[pieces.get("k")] = BitmapFactory.decodeResource(getResources(), R.drawable.king_black);
		m_ChessPieces[pieces.get("q")] = BitmapFactory.decodeResource(getResources(), R.drawable.queen_black);
		m_ChessPieces[pieces.get("r")] = BitmapFactory.decodeResource(getResources(), R.drawable.rook_black);
		m_ChessPieces[pieces.get("b")] = BitmapFactory.decodeResource(getResources(), R.drawable.bishop_black);
		m_ChessPieces[pieces.get("n")] = BitmapFactory.decodeResource(getResources(), R.drawable.knight_black);
		m_ChessPieces[pieces.get("p")] = BitmapFactory.decodeResource(getResources(), R.drawable.pawn_black);
		
		startPoint = new Point();
		movePoint = new Point();
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawBitmap(m_Board, 0, 0, null);
		
		int pixelScaleWidth = (this.getWidth()) / 8;
		int pixelScaleHeight = this.getHeight() / 8;
		
		for (int y=0; y<8; y++)
		{
			for (int x=0; x<8; x++)
			{

				if (isMoveNow)
				{
					if (!(startPoint.x == x && startPoint.y == 7 - y))
						actualDraw(canvas, pixelScaleWidth, pixelScaleHeight, y, x, 0, 0);
					else
					{
						int offsetX = (int) (movePoint.fx - startPoint.fx);
						int offsetY = (int) (movePoint.fy - startPoint.fy);
						actualDraw(canvas, pixelScaleWidth, pixelScaleHeight, y, x, offsetX, offsetY);
					}
				}
				else
					actualDraw(canvas, pixelScaleWidth, pixelScaleHeight, y, x, 0, 0);
			}
		}
		
		
	}
	
	/**
	 * Draws one piece at a time
	 * @param canvas graphical file of the piece
	 * @param pixelScaleWidth width of a square
	 * @param pixelScaleHeight height of a square
	 * @param y original y coordinate
	 * @param x original x coordinate
	 * @param offsetX original x coordinate offset while moving
	 * @param offsetY original y coordinate offset while moving
	 */
	private void actualDraw(Canvas canvas, int pixelScaleWidth, int pixelScaleHeight, int y, int x, int offsetX, int offsetY)
	{
		if (piecesArray[y][x] == 'R')
			canvas.drawBitmap(m_ChessPieces[pieces.get("R")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'B')
			canvas.drawBitmap(m_ChessPieces[pieces.get("B")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'N')
			canvas.drawBitmap(m_ChessPieces[pieces.get("N")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'Q')
			canvas.drawBitmap(m_ChessPieces[pieces.get("Q")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'K')
			canvas.drawBitmap(m_ChessPieces[pieces.get("K")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'P')
			canvas.drawBitmap(m_ChessPieces[pieces.get("P")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'r')
			canvas.drawBitmap(m_ChessPieces[pieces.get("r")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'b')
			canvas.drawBitmap(m_ChessPieces[pieces.get("b")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'n')
			canvas.drawBitmap(m_ChessPieces[pieces.get("n")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'q')
			canvas.drawBitmap(m_ChessPieces[pieces.get("q")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'k')
			canvas.drawBitmap(m_ChessPieces[pieces.get("k")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
		if (piecesArray[y][x] == 'p')
			canvas.drawBitmap(m_ChessPieces[pieces.get("p")], x * pixelScaleWidth + offsetX, (7 - y) * pixelScaleHeight + offsetY, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}
	
	private int measureWidth(int widthMeasureSpec)
	{
		int preferred = m_Board.getWidth();
		return getMeasurement(widthMeasureSpec, preferred);
	}
	
	private int measureHeight(int heightMeasureSpec)
	{
		int preferred = m_Board.getHeight();
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
	
	/**
	 * Displays and updates pieces on the board and then redraw
	 * @param piecesArray
	 */
	public void displayPieces(char[][] piecesArray)
	{
		this.piecesArray = new char[8][8];
		this.piecesArray = piecesArray;
		this.invalidate();
	}
	
	public void syncParent(MainActivity parent)
	{
		this.parent = parent;
	}
	
	/**
	 * getsParameters While moving fingers
	 * @param startPoint
	 * @param endPoint
	 */
	public void getMovePoint(Point startPoint, Point movePoint)
	{
		this.startPoint = startPoint;
		this.movePoint = movePoint;
		isMoveNow = true;
		this.invalidate();
	}
	
	public void getUpPoint()
	{
		isMoveNow = false;
		this.invalidate();
	}
}
