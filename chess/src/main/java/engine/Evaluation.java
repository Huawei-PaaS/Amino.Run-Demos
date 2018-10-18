package engine;

import java.util.Hashtable;

/**
 * This is the class which evaluates every specific position of a chess game.
 * @author user
 *
 */
public class Evaluation {
	
	private static final int PAWN = 100;
	private static final int KNIGHT = 320;
	private static final int BISHOP = 325;
	private static final int ROOK = 500;
	private static final int QUEEN = 975;
	private static final int KING = 32767;
	
	private long[] bitBoardArray = new long[12];
	private Hashtable<Character, Integer> pieces = new Hashtable<Character, Integer>();
	private boolean endGame = false;
	
	/**
	 * constructor class
	 */
	public Evaluation()
	{
		/*pieces.put(Character.valueOf('R'), Integer.valueOf(0));
		pieces.put(Character.valueOf('N'), Integer.valueOf(1));
		pieces.put(Character.valueOf('B'), Integer.valueOf(2));
		pieces.put(Character.valueOf('Q'), Integer.valueOf(3));
		pieces.put(Character.valueOf('K'), Integer.valueOf(4));
		pieces.put(Character.valueOf('P'), Integer.valueOf(5));
		pieces.put(Character.valueOf('r'), Integer.valueOf(6));
		pieces.put(Character.valueOf('n'), Integer.valueOf(7));
		pieces.put(Character.valueOf('b'), Integer.valueOf(8));
		pieces.put(Character.valueOf('q'), Integer.valueOf(9));
		pieces.put(Character.valueOf('k'), Integer.valueOf(10));
		pieces.put(Character.valueOf('p'), Integer.valueOf(11));*/
	}
	
	private short[] pawnTable = new short[] {
			 0,  0,  0,  0,  0,  0,  0,  0,
			 50, 50, 50, 50, 50, 50, 50, 50,
			 10, 10, 20, 30, 30, 20, 10, 10,
			  5,  5, 10, 27, 27, 10,  5,  5,
			  0,  0,  0, 25, 25,  0,  0,  0,
			  5, -5,-10,  0,  0,-10, -5,  5,
			  5, 10, 10,-25,-25, 10, 10,  5,
			  0,  0,  0,  0,  0,  0,  0,  0	
	};
	
	private short[] KnightTable = new short[] {
			-50,-40,-30,-30,-30,-30,-40,-50,
			 -40,-20,  0,  0,  0,  0,-20,-40,
			 -30,  0, 10, 15, 15, 10,  0,-30,
			 -30,  5, 15, 20, 20, 15,  5,-30,
			 -30,  0, 15, 20, 20, 15,  0,-30,
			 -30,  5, 10, 15, 15, 10,  5,-30,
			 -40,-20,  0,  5,  5,  0,-20,-40,
			 -50,-40,-20,-30,-30,-20,-40,-50
	};
	
	private short[] BishopTable = new short[] {
			-20,-10,-10,-10,-10,-10,-10,-20,
			 -10,  0,  0,  0,  0,  0,  0,-10,
			 -10,  0,  5, 10, 10,  5,  0,-10,
			 -10,  5,  5, 10, 10,  5,  5,-10,
			 -10,  0, 10, 10, 10, 10,  0,-10,
			 -10, 10, 10, 10, 10, 10, 10,-10,
			 -10,  5,  0,  0,  0,  0,  5,-10,
			 -20,-10,-40,-10,-10,-40,-10,-20
	};

	private short[] KingTable = new short[] {
			 -30, -40, -40, -50, -50, -40, -40, -30,
			  -30, -40, -40, -50, -50, -40, -40, -30,
			  -30, -40, -40, -50, -50, -40, -40, -30,
			  -30, -40, -40, -50, -50, -40, -40, -30,
			  -20, -30, -30, -40, -40, -30, -30, -20,
			  -10, -20, -20, -20, -20, -20, -20, -10, 
			   20,  20,   0,   0,   0,   0,  20,  20,
			   20,  30,  10,   0,   0,  10,  30,  20
	};

	private short[] KingTableEndGame = new short[] {
			-50,-40,-30,-20,-20,-30,-40,-50,
			 -30,-20,-10,  0,  0,-10,-20,-30,
			 -30,-10, 20, 30, 30, 20,-10,-30,
			 -30,-10, 30, 40, 40, 30,-10,-30,
			 -30,-10, 30, 40, 40, 30,-10,-30,
			 -30,-10, 20, 30, 30, 20,-10,-30,
			 -30,-30,  0,  0,  0,  0,-30,-30,
			 -50,-30,-30,-30,-30,-30,-30,-50
	};
	
	/**
	 * this is the basic evaluation function
	 * @return
	 */
	public int evaluation(char[][] piecesArray)
	{
		int res = 0;
		
		// convert to bitboard array
		long temp = 0x1L;
		for (int y = 0; y<8; y++)
		{
			for (int x = 0; x<8; x++)
			{
				if (piecesArray[x][y] == 'R' || piecesArray[x][y] == 'N' || piecesArray[x][y] == 'B' || piecesArray[x][y] == 'Q' || piecesArray[x][y] == 'K' || piecesArray[x][y] == 'P' || piecesArray[x][y] == 'r' || piecesArray[x][y] == 'n' || piecesArray[x][y] == 'b' || piecesArray[x][y] == 'q' || piecesArray[x][y] == 'k' || piecesArray[x][y] == 'p')
				{
					//bitBoardArray[pieces.get(piecesArray[x][y])] = bitBoardArray[pieces.get(piecesArray[x][y])] | temp;
					bitBoardArray[getValueFromLetter(piecesArray[x][y])] = bitBoardArray[getValueFromLetter(piecesArray[x][y])] | temp;
				}
				
				if (piecesArray[x][y] == 'R')
					res += ROOK;
				else if (piecesArray[x][y] == 'r')
					res -= ROOK;
				else if (piecesArray[x][y] == 'N')
					res += KNIGHT;
				else if (piecesArray[x][y] == 'n')
					res -= KNIGHT;
				else if (piecesArray[x][y] == 'B')
					res += BISHOP;
				else if (piecesArray[x][y] == 'b')
					res -= BISHOP;
				else if (piecesArray[x][y] == 'Q')
					res += QUEEN;
				else if (piecesArray[x][y] == 'q')
					res -= QUEEN;
				else if (piecesArray[x][y] == 'K')
					res += KING;
				else if (piecesArray[x][y] == 'k')
					res -= KING;
				else if (piecesArray[x][y] == 'P')
					res += PAWN;
				else if (piecesArray[x][y] == 'p')
					res -= PAWN;
				
				temp = temp >>> 1;
			}
		}
		
		
		
		// position calculation
		temp = 0x1L;
		for (int i = 0; i < 64; i++)
		{
			if ((temp & bitBoardArray[getValueFromLetter('P')]) != 0)
				res += pawnTable[i];
			temp = temp >>> 1;
		}
		
		temp = 0x1L;
		for (int i = 0; i < 64; i++)
		{
			if ((temp & bitBoardArray[getValueFromLetter('p')]) != 0)
				res += -pawnTable[63 -i];
			temp = temp >>> 1;
		}
		
		temp = 0x1L;
		for (int i = 0; i < 64; i++)
		{
			if ((temp & bitBoardArray[getValueFromLetter('N')]) != 0)
				res += KnightTable[i];
			temp = temp >>> 1;
		}
		
		temp = 0x1L;
		for (int i = 0; i < 64; i++)
		{
			if ((temp & bitBoardArray[getValueFromLetter('n')]) != 0)
				res += -KnightTable[i];
			temp = temp >>> 1;
		}
		
		temp = 0x1L;
		for (int i = 0; i < 64; i++)
		{
			if ((temp & bitBoardArray[getValueFromLetter('B')]) != 0)
				res += BishopTable[i];
			temp = temp >>> 1;
		}
		
		temp = 0x1L;
		for (int i = 0; i < 64; i++)
		{
			if ((temp & bitBoardArray[getValueFromLetter('b')]) != 0)
				res += -BishopTable[i];
			temp = temp >>> 1;
		}
		
		if (!endGame)
		{
			temp = 0x1L;
			for (int i = 0; i < 64; i++)
			{
				if ((temp & bitBoardArray[getValueFromLetter('K')]) != 0)
					res += KingTable[i];
				temp = temp >>> 1;
			}
			
			temp = 0x1L;
			for (int i = 0; i < 64; i++)
			{
				if ((temp & bitBoardArray[getValueFromLetter('k')]) != 0)
					res += -KingTable[i];
				temp = temp >>> 1;
			}
		}
		else
		{
			temp = 0x1L;
			for (int i = 0; i < 64; i++)
			{
				if ((temp & bitBoardArray[getValueFromLetter('K')]) != 0)
					res += KingTableEndGame[i];
				temp = temp >>> 1;
			}
			
			temp = 0x1L;
			for (int i = 0; i < 64; i++)
			{	
				if ((temp & bitBoardArray[getValueFromLetter('k')]) != 0)
					res += -KingTableEndGame[i];
				temp = temp >>> 1;
			}
		}
		
		return res;
	}
	
	/**
	 * This function replaces the hashtable which does not function well in threads
	 * @param letter
	 * @return
	 */
	private short getValueFromLetter(char letter)
	{
		switch (letter)
		{
		case 'R':
			return 0;
		case 'N':
			return 1;
		case 'B':
			return 2;
		case 'Q':
			return 3;
		case 'K':
			return 4;
		case 'P':
			return 5;
		case 'r':
			return 6;
		case 'n':
			return 7;
		case 'b':
			return 8;
		case 'q':
			return 9;
		case 'k':
			return 10;
		case 'p':
			return 11;
		default:
			return -1;
		}
	}
	
	private int convertToNumber(char letter)
	{
		switch (letter)
		{
		case 'a':
			return 0;
		case 'b':
			return 1;
		case 'c':
			return 2;
		case 'd':
			return 3;
		case 'e':
			return 4;
		case 'f':
			return 5;
		case 'g':
			return 6;
		case 'h':
			return 7;
		}
		return 0;
	}
}
