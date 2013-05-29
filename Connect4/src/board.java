import org.json.simple.JSONArray;

/*
 * This is a class that stores the board and its attributes.
 * It also contains methods that calculate properties (4 in a row, etc) and returns it to the player
 */
public class board {

	//create a board from a two dimensional array
	public board(int[][] pieces) {
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[0].length; j++) {
				board[i][j] = pieces[i][j];
			}
		}

	}

	//create a board with no parameters
	public board() {

	}

	//store the board here
	public int[][] board = new int[6][7];

	//visual display of board
	public String prettyBoard = "";

	//method that updates the string representation of the board
	public void prettyUpdate (String update) {
		prettyBoard = update;		
	}

	//method that updates the array representation of the board
	public void jsonArrayUpdate (JSONArray jBoard) {
		//loop through array object and copy the values
		for (int i = 0; i < jBoard.size(); i++) {
			JSONArray tempRow = (JSONArray) jBoard.get(i);
			for (int j = 0; j < tempRow.size(); j++) {
				long temp = (Long) tempRow.get(j);
				board[i][j] = (int) temp;
				
			}
		}
	}

	//method that updates the array representation of the board by accepting a 2D array, used for testing
	public void arrayUpdate(int[][] newboard) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = newboard[i][j];
			}
		}
	}

	//method that checks if a column is full
	public boolean isFull (int column) {
		if (board[0][column] != 0)
			return true;
		else
			return false;
	}

	//method that checks if a whole game is full
	public boolean isAllFull() {
		boolean full = true;
		for (int i = 0; i < 7; i++) {
			if (isFull(i) == false)
				full = false;
		}
		return full;
	}

	//method that checks if a player has n pieces in a row, will return the number of times n successive pieces are found
	public int inARow(int player, int lengthOfRow) {
		int count = 0;

		//check horizontal
		for (int i = 0; i < 6; i++) {
			for (int j = lengthOfRow - 1; j < 7; j++) {
				boolean found = true;
				for (int m = 0; m < lengthOfRow; m++) {
					if (board[i][j-m] != player)
						found = false;
				}
				//did we find a match? increment our count
				if (found == true)
					count++;
			}
		}

		//check vertical
		for (int i = lengthOfRow - 1; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				boolean found = true;
				for (int m = 0; m < lengthOfRow; m++) {
					if (board[i-m][j] != player)
						found = false;
				}
				//did we find a match? increment our count
				if (found == true)
					count++;
			}
		}

		//check left diagonal
		for (int i = lengthOfRow - 1; i < 6; i++) {
			for (int j = lengthOfRow - 1; j < 7; j++) {
				boolean found = true;
				for (int m = 0; m < lengthOfRow; m++) {
					if (board[i-m][j-m] != player)
						found = false;
				}
				//did we find a match? increment our count
				if (found == true)
					count++;
			}
		}

		//check right diagonal
		for (int i = lengthOfRow - 1; i < 6; i++) {
			for (int j = 7 - lengthOfRow; j >= 0; j--) {
				boolean found = true;
				for (int m = 0; m < lengthOfRow; m++) {
					if (board[i-m][j+m] != player)
						found = false;
				}
				//did we find a match? increment our count
				if (found == true)
					count++;
			}
		}

		return count;
	}

	//temporarily alters board to help simulate player/opponent moves
	public void simulateMove(int col, int player) {
		//change the first empty slot in the column
		for (int i = 5; i >= 0; i--) {
			if (board[i][col] == 0) {
				board[i][col] = player;
				break;
			}
		}
	}

	//removes most recent piece in a column, also to help us simulate player/opponent moves
	public void removeRecent(int col) {
		for (int i = 0; i < 6; i++) {
			if (board[i][col] != 0) {
				board[i][col] = 0;
				break;
			}
		}
	}

	//returns an array representation of the board
	public int[][] duplicate(){
		int [][] data = new int[6][7];
		data = board.clone();
		return data;
	}
	
	//returns string representation of array
	public String toString() {
		String array = "";
		for (int i = 0; i < board.length; i++) {
			String row = "\n";
			for (int j = 0; j < board[0].length; j++) {
				row += board[i][j] + " ";
			}
			array = array + row;
		}
		return array;
	}
}