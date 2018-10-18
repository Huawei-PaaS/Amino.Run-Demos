/*
 * Created on Apr 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package engine;

import android.util.Log;

import java.util.Vector;

import chesspresso.*;
import chesspresso.move.*;
import chesspresso.position.*;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RandomEngine2 implements ChessEngine {

	private Position position;
	private int ply;
	private short[] allVailableMoves;
	private final int INFINITY = 100000;
	private Short selectedMove;
	public final int SEARCH_DEPTH = 10;
	public final int ABWIN = 30;
	
	public String lastMove() {
		return Move.getString(position.getLastShortMove());
	}
	public RandomEngine2() {
		reset();
		
	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#reset()
	 */
	public void reset() {
		position = Position.createInitialPosition();
		ply = 0;
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#go()
	 */
	public String go() {
		System.out.println("Inside the go of Random Engine");
		/*if (isDraw()) {
			return "DRAW";
		} else if (isMate()) {
			return "CHECKMATE";
		} else {
			short moves[] = position.getAllMoves();
			if (moves.length < 1) {
				return "ERROR: NO MOVES";
			}
			int choice =
				new Long(
					Math.round(
						(Math.random() * 1000) + (Math.random() * 2) + 37)
						% moves.length)
					.intValue();
			try {
				position.doMove(moves[choice]);
			} catch (Exception e) {
				return "ERROR: " + e;
			}
			ply++;
			
			//TODO here activate the evaluation function
			
			
			return Move.getString(moves[choice]);
		}*/
		
		short myMove = bestMove();
		try {
			position.doMove(myMove);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ply++;
		return Move.getString(myMove);
		
	}
	
	/**
	 * returns the best move possible
	 * @return
	 */
	private short bestMove()
	{
		short best = Move.ILLEGAL_MOVE;
		
		short moves[];
		int alpha = -INFINITY;
		int beta = INFINITY;
		int score = -INFINITY - 10;
		moves = position.getAllMoves();
		
		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
			} catch (Exception e) {
				continue;
			}
			
			int val = -alphaBeta(SEARCH_DEPTH, -beta, -alpha, getBoard(ply));
			
			if (val >= beta) {
				beta = INFINITY;
				val = -alphaBeta(SEARCH_DEPTH, -beta, -alpha, getBoard(ply));
			}
			if (val > score) {
				score = val;
				best = moves[i];
				
			}
			alpha = score;
			beta = score + ABWIN;
			position.undoMove();
			
		}
		
		return best;
	}
	
	/**
	 * this is the alpha beta recursion
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @return
	 */
	private int alphaBeta(int depth, int alpha, int beta, char[][] pieceArray)
	{
		int value;
		short[] moves = position.getAllMoves();
		if (depth == 0)
			return evaluate(moves, pieceArray);
		else
		{
			for (int i=0; i<moves.length; i++)
			{
				try {
					position.doMove(moves[i]);
				} catch (IllegalMoveException e) {}
				
				value = -alphaBeta(depth - 1, -alpha, -beta, pieceArray);
				position.undoMove();
				if (value >= beta)
					return beta;
				if (value > alpha)
				{
					alpha = value;
					
				}
			}
		}
		Log.e("pointerSquare", String.valueOf(alpha));
		return alpha;
	}
	
	/**
	 * evaluation function. lazy method for this time...
	 * @return
	 */
	private int evaluate(short[] moves, char[][] pieceArray)
	{
		Evaluation eval = new Evaluation();
		
		int temp = eval.evaluation(pieceArray);
		
		return position.getMaterial();
		
		
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#makeMove(java.lang.String)
	 */
	public String makeMove(String move) {
		short m = parseMove(move);
		try {

			position.doMove(m);

		} catch (Exception e) {
			return "ERROR: " + e;
		}
		ply++;
		return Move.getString(m);
	}

	public short parseMove(String move) {
		move = move.trim();
		short moves[] = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			if (Move.getString(moves[i]).equals(move)) {
				return moves[i];
			}
		}
		return Move.ILLEGAL_MOVE;
	}
	
	/**
	 * returns all possible moves
	 * @return
	 */
	public String[] getAllMoves()
	{
		Vector<String> vector = new Vector<String>();
		short[] moves = position.getAllMoves();
		
		for (int i = 0; i < moves.length; i++)
		{
			vector.add(Move.getString(moves[i]));
		}
		
		return (String[]) vector.toArray();
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isWhiteTurn()
	 */
	public boolean isWhiteTurn() {
		return Position.isWhiteToPlay(position.hashCode());
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isDraw()
	 */
	public boolean isDraw() {
		return position.isStaleMate();
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isMate()
	 */
	public boolean isMate() {
		return position.isMate();
	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#getBoard(int)
	 */
	public char[][] getBoard(int ply) {

		char[][] retval = new char[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int piece = position.getStone((i * 8) + j);
				switch (piece) {
					case Chess.BLACK_BISHOP :
						retval[i][j] = 'b';
						break;
					case Chess.WHITE_BISHOP :
						retval[i][j] = 'B';
						break;
					case Chess.BLACK_KNIGHT :
						retval[i][j] = 'n';
						break;
					case Chess.WHITE_KNIGHT :
						retval[i][j] = 'N';
						break;
					case Chess.BLACK_ROOK :
						retval[i][j] = 'r';
						break;
					case Chess.WHITE_ROOK :
						retval[i][j] = 'R';
						break;
					case Chess.BLACK_QUEEN :
						retval[i][j] = 'q';
						break;
					case Chess.WHITE_QUEEN :
						retval[i][j] = 'Q';
						break;
					case Chess.BLACK_KING :
						retval[i][j] = 'k';
						break;
					case Chess.WHITE_KING :
						retval[i][j] = 'K';
						break;
					case Chess.BLACK_PAWN :
						retval[i][j] = 'p';
						break;
					case Chess.WHITE_PAWN :
						retval[i][j] = 'P';
						break;
					default:
						retval[i][j] = ' ';
				}
			}
		}
		return retval;
	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#currentPly()
	 */
	public int currentPly() {
		return ply;
	}
	
	/**
	 * indicate weather it is check mode.
	 */
	public boolean isCheck() {
		return position.isCheck();
	}
	
	public int[] getBoard(char[][] ply)
	{
		int[] res = new int[9];
		return res;
	}
}
