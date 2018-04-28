/**
 * BraveKnight.java
 *
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 28-04-2018
 */
package challenge4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/**
 * [DESCRIPTION]
 */
public class BraveKnight {

	/** String that specifies the name of the input file. */
	private final static String INPUT_FILENAME = "src/challenge4/testInput";
	/**
	 * String that specifies the name of the output file, with the required format.
	 */
	private final static String OUTPUT_FILENAME = "src/challenge4/testOutput";

	private static boolean isValid(ArrayList<ArrayList<Square>> board, int boardWidth, int boardHeight, int neighborXPos,
			int neighborYPos) {
		return ((neighborXPos < boardWidth) && (neighborXPos >= 0) && (neighborYPos < boardHeight) && (neighborYPos >= 0)
				&& !(board.get(neighborYPos).get(neighborXPos) instanceof LavaSquare));
	}

	private static int BFS(ArrayList<ArrayList<Square>> board, int boardWidth, int boardHeight, Square source,
			Square destination) {
		HashMap<Square, Boolean> visitedSquares = new HashMap<>();
		Queue<Square> squareQueue = new ArrayDeque<>();
		squareQueue.add(source);

		while (!squareQueue.isEmpty()) {
			Square currentSquare = squareQueue.poll();

			// If reached destination
			if ((currentSquare.getxPos() == destination.getxPos()) && (currentSquare.getyPos() == destination.getyPos())) {
				return currentSquare.getNumberOfSteps();
			}

			// If not visited
			if (!visitedSquares.containsKey(currentSquare)) {
				visitedSquares.put(currentSquare, true);

				for (int i = 0; i < Square.MOVEMENT_NUMBER; ++i) {
					int neighborXPos = currentSquare.getxPos() + currentSquare.getRowMovementAt(i);
					int neighborYPos = currentSquare.getyPos() + currentSquare.getColumnMovementAt(i);
					if (isValid(board, boardWidth, boardHeight, neighborXPos, neighborYPos)) {
						System.out.println(
								"(" + neighborYPos + " , " + neighborXPos + ") Distance: " + (currentSquare.getNumberOfSteps() + 1));
						Square neighborSquare = board.get(neighborYPos).get(neighborXPos);
						neighborSquare.setNumberOfSteps(currentSquare.getNumberOfSteps() + 1);
						squareQueue.add(neighborSquare);
					}
				}
			}
		}

		return 0;
	}

	private static int getMinNumberOfSteps(ArrayList<ArrayList<Square>> board, int boardWidth, int boardHeight,
			Square source, Square princess, Square destination) {
		showBoard(boardWidth, boardHeight, board);

		if (source == null || princess == null) {
			throw new IllegalArgumentException("No source or princess in the board.");
		}
		else if (destination == null) {
			return 0;
		}
		System.out.println("Going");
		int stepsFromSourceToPrincess = BFS(board, boardWidth, boardHeight, source, princess);
		resetBoardDistances(board);
		System.out.println("Returning");
		int stepsFromPrincessToDestination = BFS(board, boardWidth, boardHeight, princess, destination);

		return (stepsFromSourceToPrincess == 0 || stepsFromPrincessToDestination == 0) ? 0
				: stepsFromSourceToPrincess + stepsFromPrincessToDestination;
	}

	/**
	 * @param board
	 */
	private static void resetBoardDistances(ArrayList<ArrayList<Square>> board) {
		for (ArrayList<Square> boardRow : board) {
			for (Square element : boardRow) {
				element.setNumberOfSteps(0);
			}
		}
	}

	/**
	 * Main method. It doesn't receive anything. It reads the input file, evaluate
	 * it and write the result in the output file.
	 */
	public static void main(String[] args) {
		try {
			BufferedReader codeReader = new BufferedReader(new FileReader(INPUT_FILENAME));
			BufferedWriter codeWriter = new BufferedWriter(new FileWriter(OUTPUT_FILENAME));

			int numberOfLines = Integer.parseInt(codeReader.readLine());

			// Reading the board
			for (int i = 1; i <= numberOfLines; ++i) {
				String[] boardDimensions = codeReader.readLine().trim().split("\\s");
				if (boardDimensions.length > 2) {
					codeReader.close();
					codeWriter.close();
					throw new IllegalArgumentException("Board dimensions not valid.");
				}

				int boardHeight = Integer.parseInt(boardDimensions[0]);
				int boardWidth = Integer.parseInt(boardDimensions[1]);
				Square source = null, destination = null, princess = null;

				ArrayList<ArrayList<Square>> board = new ArrayList<ArrayList<Square>>();
				for (int j = 0; j < boardHeight; ++j) {
					String[] rowElementsArray = codeReader.readLine().trim().split("");
					if (boardDimensions.length >= boardWidth) {
						codeReader.close();
						codeWriter.close();
						throw new IllegalArgumentException("Board rows not valid.");
					}

					ArrayList<Square> boardRow = new ArrayList<Square>();
					for (int k = 0; k < boardWidth; ++k) {
						Square newSquare = constructSquare(k, j, rowElementsArray[k]);
						if (newSquare.getType() != null) {
							switch (newSquare.getType()) {
								case "PRINCESS":
									if (princess == null) {
										princess = newSquare;
									}
									else {
										throw new IllegalArgumentException("Two princess in the board.");
									}
									break;
								case "KNIGHT":
									if (source == null) {
										source = newSquare;
									}
									else {
										throw new IllegalArgumentException("Two knights in the board.");
									}
									break;
								case "DESTINATION":
									if (destination == null) {
										destination = newSquare;
									}
									else {
										throw new IllegalArgumentException("Two destinations in the board.");
									}
									break;
							}
						}
						boardRow.add(newSquare);
					}
					board.add(boardRow);
				}

				int minNumberOfSteps = getMinNumberOfSteps(board, boardWidth, boardHeight, source, princess, destination);
				String solutionSteps = (minNumberOfSteps == 0) ? "IMPOSSIBLE" : String.valueOf(minNumberOfSteps);
				System.out.println("Case #" + i + ": " + solutionSteps);
				codeWriter.write("Case #" + i + ": " + solutionSteps + System.lineSeparator());
			}

			codeReader.close();
			codeWriter.close();
		}
		catch (IOException e) {
			System.err.println("ERROR with IO:");
			e.printStackTrace();
		}
	}

	/**
	 * @param rowElementsArray
	 * @return
	 */
	private static Square constructSquare(int xPos, int yPos, String rowElement) {
		switch (rowElement) {
			case ".":
			case "D":
			case "S":
			case "P":
				return new GroundSquare(xPos, yPos, rowElement);
			case "#":
				return new LavaSquare(xPos, yPos);
			case "*":
				return new TrampolinSquare(xPos, yPos);
			default:
				throw new IllegalArgumentException("Element in board row not valid.");
		}
	}

	/**
	 * @param boardWidth
	 * @param boardHeight
	 * @param board
	 */
	private static void showBoard(int boardWidth, int boardHeight, ArrayList<ArrayList<Square>> board) {
		System.out.println("BOARD DIMENSIONS: " + boardWidth + " x " + boardHeight);
		for (ArrayList<Square> boardRow : board) {
			for (Square element : boardRow) {
				System.out.print(element + " ");
			}
			System.out.println("");
		}
	}
}