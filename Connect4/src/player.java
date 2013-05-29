import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class player {

	//root URL used for all API calls
	final static String link = "http://connect4.sparefoot.com/?action="; 

	//integer rep of SpareFoot's piece
	final static int SpareFoot = 1;

	//integer rep of our piece
	final static int ourPlayer = 2;

	//determines how many turns ahead the AI calculates/predicts, limits recursive calls
	final static int depth = 7;

	//store the game ID here
	public static String gameID = "";

	//number of moves that have been made
	public static int moves = 0;

	//state of the game
	public static String state = "";

	//board object used to store board attributes
	public static board boardSetUp = new board();

	//function which updates local variables to reflect new state of board
	public static void getState() throws IOException {
		//create game and parse JSON
		URL url = new URL(link+"state&game_id="+gameID);
		String output = new Scanner(url.openStream()).useDelimiter("\\A").next();
		JSONObject info = (JSONObject) JSONValue.parse(output);

		//update state
		state = (String) info.get("state");

		//update the prettyBoard
		boardSetUp.prettyUpdate((String) info.get("pretty_board"));

		//update moves
		long temp1 = (Long) info.get("moves");
		moves = (int) temp1;

		//update the numerical board by looping through JSONArray of values
		boardSetUp.jsonArrayUpdate((JSONArray) info.get("board"));
	}

	//set up the game/get game ID
	public static void initializeGame() throws IOException {
		//create game 
		URL url = new URL(link+"create");
		String output = new Scanner(url.openStream()).useDelimiter("\\A").next();

		//store game id
		JSONObject details = (JSONObject) JSONValue.parse(output);
		gameID = (String) details.get("game_id");

		//get state	
		getState();

	}

	//temporary method to debug program, currently allows us to pause after every move
	public static void testMethod() throws IOException {
		//breaks after every move for testing purposes
		String cont = "";
		while (!cont.equalsIgnoreCase("Go")) {
			Scanner scanner = new Scanner (System.in);
			System.out.println("Board: ");
			System.out.println(boardSetUp.prettyBoard);
			System.out.print("Type 'Go' to Continue");
			cont = scanner.next();
		}
	}

	//submit move
	public static void submitMove(int move) throws IOException {
		URL url = new URL(link+"move&column=" + move + "&game_id=" + gameID);
		String output = new Scanner(url.openStream()).useDelimiter("\\A").next();
	}

	//parent function to decide which is the best move
	public static int playGame() {
		int currentScore = -9999999;
		//set the default best index to the first column that isn't full
		int bestIndex = 0;
		for (int i = 0; i < 6; i++) {
			if (!boardSetUp.isFull(i)) {
				bestIndex = i;
				break;
			}
		}
		//7 possible options
		for (int i = 0; i < 7; i++) {
			int tempScore;

			//is this column full?
			if (!boardSetUp.isFull(i)) {
				board tempBoard = new board(boardSetUp.duplicate());
				tempBoard.simulateMove(i, ourPlayer);

				//check if this is the best move we have found so far
				tempScore = -simulate(SpareFoot,depth-1,tempBoard, Integer.MIN_VALUE);
				if (tempScore > currentScore) {
					currentScore = tempScore;
					bestIndex = i;
				}
			}
		}
		return bestIndex;
	}

	//function that returns the number of "points" a move is worth
	public static int getScore(board b, int player) {
		//determine opponent
		int otherPlayer;
		if (player == SpareFoot) 
			otherPlayer = ourPlayer;
		else 
			otherPlayer = SpareFoot;



		//connect 4? this player wins
		if (b.inARow(player, 4) > 0) {
			return 999999;
		}
		int tempScore = 0;
		//do we have a connect 3? add 200 points for each one
		tempScore += (b.inARow(player, 3))*200;

		//do we have a connect 2? add 10 points for each one
		tempScore += (b.inARow(player, 2))*10;

		//opponent has connect 4?
		if (b.inARow(otherPlayer, 4) >= 1)
			return -999999;
		else
			return tempScore;
	}

	//recursive function that finds the highest possible score for a move
	public static int simulate(int player, int depth, board b, int previous) {
		int otherPlayer;
		//determine opponent
		if (player == SpareFoot) 
			otherPlayer = ourPlayer;
		else 
			otherPlayer = SpareFoot;

		//Have we won?
		if (b.inARow(player, 4) >= 1){			
			return 999999;
		}
		//Has the opponent won?
		else if (b.inARow(otherPlayer, 4) >= 1){			
			return -999999;
		}
		//Is the game full?
		else if (b.isAllFull() || depth == 0)
			return getScore(b,player);

		//find the best move to make
		int bestScore = -999999;
		int tempScore = 0;

		//if the score of a move is getting worse with each recursive call, we should not put the piece in this column for sure
		int current = getScore(b,player);
		if (previous-current < previous)
			return Integer.MIN_VALUE;

		//simulate player actions
		for (int i = 0; i < 7; i++) {
			if (!b.isFull(i)) {
				board tempBoard  = new board(b.duplicate());
				tempBoard.simulateMove(i, player);
				int val = -simulate(otherPlayer,depth-1,tempBoard, current);
				tempScore = val;
				//is this the best so far?
				if (tempScore > bestScore) {
					bestScore = tempScore;
				}
			}
		}
		//return the best score recorded
		return bestScore; 
	}


	//keeps playing game until someone wins
	public static void runGame() throws IOException {
		while (state.equals("In progress")) {
			getState();
			submitMove(playGame());
		}
		System.out.println("Result: " + state);
		System.out.println("Game ID: " + gameID);
	}
	
	public static void main(String[] args) throws IOException {
		initializeGame();
		getState();
		runGame();
	}
}
