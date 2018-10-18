package engine;

import android.util.Log;

import java.util.Vector;

import chesspresso.*;
import chesspresso.move.*;
import chesspresso.position.*;


public class KobiChessEngine implements ChessEngine {

	private Position position;
	private int ply;
	private short[] allVailableMoves;
	private final int INFINITY = 100000;
	public final int SEARCH_DEPTH = 10;
	public final int ABWIN = 30;
	private int num_pieces;
	
	public static final int MATE = 50000;
	public static final int CHECK = 50000;
	
	private static final int frame = 0;
	private static final int npiece = 13;
	private static final int wpawn = 1;
	private static final int bpawn = 2;
	private static final int wrook = 7;
	private static final int brook = 8;
	private static final int wbishop = 11;
	private static final int bbishop = 12;
	private static final int wking = 5;
	private static final int bking = 6;
	private static final int wqueen = 9;
	private static final int bqueen = 10;
	private static final int wknight = 3;
	private static final int bknight = 4;
	private int[] board;
	
	int[] rev_rank = {0,8,7,6,5,4,3,2,1};
	
	private int[] test = { 	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
							0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
							0, 0, 0, 2, 0, 0, 0, 0,	0, 2, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	private int[] moved = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	private int[] pieces = new int[33];
	private int[] squares = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	
	
	private int[] init_board = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	private int[] bishop = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, -5, -5, -5, -5, -5, -5, -5, -5, 0, 0, 0,
			0, -5, 10, 5, 10, 10, 5, 10, -5, 0, 0, 0, 0, -5, 5, 3, 12, 12, 3,
			5, -5, 0, 0, 0, 0, -5, 3, 12, 3, 3, 12, 3, -5, 0, 0, 0, 0, -5, 3,
			12, 3, 3, 12, 3, -5, 0, 0, 0, 0, -5, 5, 3, 12, 12, 3, 5, -5, 0, 0,
			0, 0, -5, 10, 5, 10, 10, 5, 10, -5, 0, 0, 0, 0, -5, -5, -5, -5, -5,
			-5, -5, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0 };

	private int knight[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, -10, -5, -5, -5, -5, -5, -5, -10, 0, 0,
			0, 0, -5, 0, 0, 3, 3, 0, 0, -5, 0, 0, 0, 0, -5, 0, 5, 5, 5, 5, 0,
			-5, 0, 0, 0, 0, -5, 0, 5, 10, 10, 5, 0, -5, 0, 0, 0, 0, -5, 0, 5,
			10, 10, 5, 0, -5, 0, 0, 0, 0, -5, 0, 5, 5, 5, 5, 0, -5, 0, 0, 0, 0,
			-5, 0, 0, 3, 3, 0, 0, -5, 0, 0, 0, 0, -10, -5, -5, -5, -5, -5, -5,
			-10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0 };

	private long white_pawn[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0,
			0, 0, 0, 2, 4, 6, 8, 8, 6, 4, 2, 0, 0, 0, 0, 3, 6, 9, 12, 12, 9, 6,
			3, 0, 0, 0, 0, 4, 8, 12, 16, 16, 12, 8, 4, 0, 0, 0, 0, 5, 10, 15,
			20, 20, 15, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private int black_pawn[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 5, 10, 15, 20, 20, 15, 10, 5, 0, 0, 0, 0, 4, 8, 12, 16, 16, 12,
			8, 4, 0, 0, 0, 0, 3, 6, 9, 12, 12, 9, 6, 3, 0, 0, 0, 0, 2, 4, 6, 8,
			8, 6, 4, 2, 0, 0, 0, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0,
			0, -5, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	/* to be used during opening and middlegame for white king positioning: */
	private int white_king[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 10, 4, 0, 0, 7, 10, 2, 0, 0, 0,
			0, -3, -3, -5, -5, -5, -5, -3, -3, 0, 0, 0, 0, -5, -5, -8, -8, -8,
			-8, -5, -5, 0, 0, 0, 0, -8, -8, -13, -13, -13, -13, -8, -8, 0, 0,
			0, 0, -13, -13, -21, -21, -21, -21, -13, -13, 0, 0, 0, 0, -21, -21,
			-34, -34, -34, -34, -21, -21, 0, 0, 0, 0, -34, -34, -55, -55, -55,
			-55, -34, -34, 0, 0, 0, 0, -55, -55, -89, -89, -89, -89, -55, -55,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0 };

	/* to be used during opening and middlegame for black king positioning: */
	private int black_king[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -55, -55, -89, -89, -89, -89, -55,
			-55, 0, 0, 0, 0, -34, -34, -55, -55, -55, -55, -34, -34, 0, 0, 0,
			0, -21, -21, -34, -34, -34, -34, -21, -21, 0, 0, 0, 0, -13, -13,
			-21, -21, -21, -21, -13, -13, 0, 0, 0, 0, -8, -8, -13, -13, -13,
			-13, -8, -8, 0, 0, 0, 0, -5, -5, -8, -8, -8, -8, -5, -5, 0, 0, 0,
			0, -3, -3, -5, -5, -5, -5, -3, -3, 0, 0, 0, 0, 2, 10, 4, 0, 0, 7,
			10, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0 };

	/* to be used for positioning of both kings during the endgame: */
	private int end_king[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -5, -3, -1, 0, 0, -1, -3, -5, 0, 0,
			0, 0, -3, 5, 5, 5, 5, 5, 5, -3, 0, 0, 0, 0, -1, 5, 10, 10, 10, 10,
			5, -1, 0, 0, 0, 0, 0, 5, 10, 15, 15, 10, 5, 0, 0, 0, 0, 0, 0, 5,
			10, 15, 15, 10, 5, 0, 0, 0, 0, 0, -1, 5, 10, 10, 10, 10, 5, -1, 0,
			0, 0, 0, -3, 5, 5, 5, 5, 5, 5, -3, 0, 0, 0, 0, -5, -3, -1, 0, 0,
			-1, -3, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0 };
	
	public String lastMove() {
		return Move.getString(position.getLastShortMove());
	}
	public KobiChessEngine() {
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
		System.out.println("Inside the go from MainActivity for Engine Move..");
		short myMove = bestMove();
		try {
			position.doMove(myMove);
		} catch (IllegalMoveException e) {
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
		System.out.println("Inside the bestMove()");
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
		char[][] bArray = getBoard(ply);
		board = getBoard(bArray);
		for (int x=0; x<8; x++)
		{
			for (int y=0; y<8; y++)
			{
				if (bArray[x][y] != ' ')
					num_pieces++;
			}
		}
		
		reset_piece_square();
		
		if (num_pieces > 11) {
			return (opn_eval());
		} else if (num_pieces < 5) {
			//return (end_eval());
		} else {
			//return (mid_eval());
		}	
		
		return position.getMaterial();
		//return res;
		
	}
	
	// TODO open eval
	private int opn_eval()
	{
		int i, pawn_file = 0, rank = 0, wking_pawn_file, bking_pawn_file, j;
		long score = 0;
		boolean isolated, backwards;
		int[][] pawns = new int[2][11];
		int[] white_back_pawn = new int[11];
		int[] black_back_pawn = new int[11];
		
		
		
		//memset
		for (int x = 0; x<2; x++)
		{
			for (int y = 0; y<2; y++)
			{
				pawns[x][y] = 0;
			}
		}
		
		for (i = 0; i < 11; i++) {
			white_back_pawn[i] = 7;
			black_back_pawn[i] = 2;
		}
		
		for (j = 1; j <= num_pieces; j++) {
			i = pieces[j];
			if (i == 0)
				continue;
			pawn_file = file (i)+1;
			rank = rank (i);
		}
		
		if (board[i] == wpawn) {
			pawns[1][pawn_file]++;
			if (rank < white_back_pawn[pawn_file]) {
				white_back_pawn[pawn_file] = rank;
			}
		}
		else if (board[i] == bpawn) {
			pawns[0][pawn_file]++;
			if (rank > black_back_pawn[pawn_file]) {
				black_back_pawn[pawn_file] = rank;
			}
		}
		
		for (j = 1; j <= num_pieces; j++) {
			i = pieces[j];
			if (i == 0)
				continue;
			pawn_file = file (i)+1;
			rank = rank (i);
			switch (board[i]) {
			case (wpawn):
				isolated = false;
				backwards = false;
				score += 100;
				score += white_pawn[i];
				
				if (white_back_pawn[pawn_file+1] > rank && white_back_pawn[pawn_file-1] > rank) {
					if (rank != 2)
						score -= 3;
					backwards = true;
					
					backwards = true;
					if (pawns[1][pawn_file+1] == 0 && pawns[1][pawn_file-1] == 0) {
						score -= 2;
						isolated = true;
					}
				}
				
				if (pawns[0][pawn_file] == 0) {
					if (backwards)
						score -= 3;
					if (isolated)
						score -= 5;
				}
				
				if (pawns[1][pawn_file] > 1)
					  score -= 2*(pawns[1][pawn_file]-1);
				
				if (pawns[0][pawn_file] == 0
						&& rank >= black_back_pawn[pawn_file - 1]
						&& rank >= black_back_pawn[pawn_file + 1]) {
					score += white_pawn[i];
					/* give an extra bonus if a connected, passed pawn: */
					if (!isolated)
						score += 10;
				}
				break;
			case (bpawn):
				isolated = false;
				backwards = false;
				score -= 100;
				score -= black_pawn[i];
				
				if (black_back_pawn[pawn_file + 1] < rank
						&& black_back_pawn[pawn_file - 1] < rank) {
					/*
					 * no penalty in the opening for having a backwards pawn
					 * that hasn't moved yet!
					 */
					if (rank != 2)
						score += 3;
					backwards = true;
					/* check to see if it is furthermore isolated: */
					if (pawns[0][pawn_file + 1] == 0
							&& pawns[0][pawn_file - 1] == 0) {
						score += 2;
						isolated = true;
					}
				}
				
				if (pawns[1][pawn_file] == 0) {
					if (backwards)
						score += 3;
					if (isolated)
						score += 5;
				}
				
				if (pawns[0][pawn_file] > 1)
					score += 2 * (pawns[0][pawn_file] - 1);
				
				if (pawns[1][pawn_file] == 0
						&& rank <= white_back_pawn[pawn_file - 1]
						&& rank <= white_back_pawn[pawn_file + 1]) {
					score -= black_pawn[i];
					if (!isolated)
						score -= 10;
				}
				
				
				break;
			case (wrook):
				score += 500;
			
				if (rank == 7)
					  score += 8;
				
				if (pawns[1][pawn_file] == 0) {
					/* half open file */
					score += 5;
					if (pawns[0][pawn_file] == 0) {
						/* open file */
						score += 3;
					}
				}
				break;
			case (brook):
				score -= 500;
			
				if (rank == 2)
					score -= 8;

				if (pawns[0][pawn_file] == 0) {
					/* half open file */
					score -= 5;
					if (pawns[1][pawn_file] == 0) {
						/* open file */
						score -= 3;
					}
				}
				break;
			case (wbishop):
				score += 325;
				score += bishop[i];
				break;
			case (bbishop):
				score -= 325;
				score -= bishop[i];
				break;
			case (wknight):
				score += 310;
				score += knight[i];
				break;
			case (bknight):
				score -= 310;
				score -= knight[i];
				break;
			case (wqueen):
				score += 900;
			
				// TODO c line 937 find out what is the moved value in debug
				if (i != 29)
				  if (moved[28] == 0 || moved[27] == 0 || moved[31] == 0 || moved[32] == 0)
				    score -= 4;
				break;
			case (bqueen):
				score -= 900;
			
				if (i != 113)
				  if (moved[112] == 0 || moved[111] == 0 || moved[115] == 0 || moved[116] == 0)
				    score += 4;
				break;
			case (wking):
				score += white_king[i];
			
				//if (white_castled)
				//  score += 12;
				if (moved[30] != 0) {
				  score -= 4;
				  
				  if (pawns[1][pawn_file] == 0)
					    score -= 6;
				}
				
				if (rank < white_back_pawn[pawn_file]
						&& pawns[1][pawn_file] != 0)
					score -= 5 * (white_back_pawn[pawn_file] - rank - 1);
				else
					score -= 8;
				if (rank < white_back_pawn[pawn_file + 1]
						&& pawns[1][pawn_file + 1] != 0)
					score -= 4 * (white_back_pawn[pawn_file + 1] - rank - 1);
				else
					score -= 8;
				if (rank < white_back_pawn[pawn_file - 1]
						&& pawns[1][pawn_file - 1] != 0)
					score -= 4 * (white_back_pawn[pawn_file - 1] - rank - 1);
				else
					score -= 8;
				break;
			case (bking):
				score -= black_king[i];
			
				if (moved[114] != 0) {
				  score += 4;
				  if (pawns[0][pawn_file] == 0)
					    score += 6;
				}
				
				if (rank > black_back_pawn[pawn_file]
						&& pawns[0][pawn_file] != 0)
					score += 5 * (rank - black_back_pawn[pawn_file] - 1);
				else
					score += 8;
				if (rank > black_back_pawn[pawn_file + 1]
						&& pawns[0][pawn_file + 1] != 0)
					score += 4 * (rank - black_back_pawn[pawn_file + 1] - 1);
				else
					score += 8;
				if (rank > black_back_pawn[pawn_file - 1]
						&& pawns[0][pawn_file - 1] != 0)
					score += 4 * (rank - black_back_pawn[pawn_file - 1] - 1);
				else
					score += 8;
				break;
			}
		}
		
		// TODO c line 1031 handle castling and moved
		if (moved[41] == 0 && board[53] != npiece)
			score -= 7;
		if (moved[42] == 0 && board[54] != npiece)
			score -= 7;
		if (moved[101] == 0 && board[89] != npiece)
			score += 7;
		if (moved[102] == 0 && board[90] != npiece)
			score += 7;
		
		//wking_pawn_file = file (wking_loc)+1;
		//bking_pawn_file = file (bking_loc)+1;
		
		
		
		return 0;
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
	
	/**
	 * converts board array to faile format
	 * @param input
	 * @return
	 */
	public int[] getBoard(char[][] input)
	{
		int[] res = new int[144];
		for (int i = 0; i<144; i++)
		{
			res[i] = 0;
		}
		
		res[26] = convertToNumber(input[0][0]);
		res[27] = convertToNumber(input[0][1]);
		res[28] = convertToNumber(input[0][2]);
		res[29] = convertToNumber(input[0][3]);
		res[30] = convertToNumber(input[0][4]);
		res[31] = convertToNumber(input[0][5]);
		res[32] = convertToNumber(input[0][6]);
		res[33] = convertToNumber(input[0][7]);
		
		res[38] = convertToNumber(input[1][0]);
		res[39] = convertToNumber(input[1][1]);
		res[40] = convertToNumber(input[1][2]);
		res[41] = convertToNumber(input[1][3]);
		res[42] = convertToNumber(input[1][4]);
		res[43] = convertToNumber(input[1][5]);
		res[44] = convertToNumber(input[1][6]);
		res[45] = convertToNumber(input[1][7]);
		
		res[50] = convertToNumber(input[2][0]);
		res[51] = convertToNumber(input[2][1]);
		res[52] = convertToNumber(input[2][2]);
		res[53] = convertToNumber(input[2][3]);
		res[54] = convertToNumber(input[2][4]);
		res[55] = convertToNumber(input[2][5]);
		res[56] = convertToNumber(input[2][6]);
		res[57] = convertToNumber(input[2][7]);
		
		res[62] = convertToNumber(input[3][0]);
		res[63] = convertToNumber(input[3][1]);
		res[64] = convertToNumber(input[3][2]);
		res[65] = convertToNumber(input[3][3]);
		res[66] = convertToNumber(input[3][4]);
		res[67] = convertToNumber(input[3][5]);
		res[68] = convertToNumber(input[3][6]);
		res[69] = convertToNumber(input[3][7]);
		
		res[74] = convertToNumber(input[4][0]);
		res[75] = convertToNumber(input[4][1]);
		res[76] = convertToNumber(input[4][2]);
		res[77] = convertToNumber(input[4][3]);
		res[78] = convertToNumber(input[4][4]);
		res[79] = convertToNumber(input[4][5]);
		res[80] = convertToNumber(input[4][6]);
		res[81] = convertToNumber(input[4][7]);
		
		res[86] = convertToNumber(input[5][0]);
		res[87] = convertToNumber(input[5][1]);
		res[88] = convertToNumber(input[5][2]);
		res[89] = convertToNumber(input[5][3]);
		res[90] = convertToNumber(input[5][4]);
		res[91] = convertToNumber(input[5][5]);
		res[92] = convertToNumber(input[5][6]);
		res[93] = convertToNumber(input[5][7]);
		
		res[98] = convertToNumber(input[6][0]);
		res[99] = convertToNumber(input[6][1]);
		res[100] = convertToNumber(input[6][2]);
		res[101] = convertToNumber(input[6][3]);
		res[102] = convertToNumber(input[6][4]);
		res[103] = convertToNumber(input[6][5]);
		res[104] = convertToNumber(input[6][6]);
		res[105] = convertToNumber(input[6][7]);
		
		res[110] = convertToNumber(input[7][0]);
		res[111] = convertToNumber(input[7][1]);
		res[112] = convertToNumber(input[7][2]);
		res[113] = convertToNumber(input[7][3]);
		res[114] = convertToNumber(input[7][4]);
		res[115] = convertToNumber(input[7][5]);
		res[116] = convertToNumber(input[7][6]);
		res[117] = convertToNumber(input[7][7]);
		
		return res;
	}
	
	private int convertToNumber(char letter)
	{
		switch (letter)
		{
		case 'P': return wpawn;
		case 'p': return bpawn;
		case 'K': return wking;
		case 'k': return bking;
		case 'Q': return wqueen;
		case 'q': return bqueen;
		case 'R': return wrook;
		case 'r': return brook;
		case 'N': return wknight;
		case 'n': return bknight;
		case 'B': return wbishop;
		case 'b': return bbishop;
		case ' ': return npiece;
		default: return frame;
		}
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
	
	// from utils.c
	void reset_piece_square() {

		/* reset the pieces[] / squares[] arrays */

		int i;
		/*
		 * we use piece number 0 to show a piece taken off the board, so don't
		 * use that piece number for other things:
		 */

		pieces[0] = 0;
		num_pieces = 0;

		for (i = 26; i < 118; i++) {
			if (board[i] != frame && board[i] != npiece) {
				pieces[++num_pieces] = i;
				squares[i] = num_pieces;
			} else {
				squares[i] = 0;
			}
		}

	}
	
	private int file(int square) {
		return (((square) - 26) % 12) + 1;
	}
	
	private int rank(int square) {
		return (((square) - 26) / 12) + 1;
	}
	
}
