
import java.util.ArrayList;

/**
 * This is a template for the class corresponding to your original
 * advanced heuristic.  This class is an implementation of the
 * <tt>Heuristic</tt> interface.  After thinking of an original
 * heuristic, you should implement it here, filling in the constructor
 * and the <tt>getValue</tt> method.
 */
public class AdvancedHeuristic implements Heuristic {

	private Puzzle puzzle;
	private State state;
	private int numCars;
	ArrayList<Integer> visited;
	
	/**
	 * This is the required constructor, which must be of the given form.
	 */
	public AdvancedHeuristic(Puzzle puzzle) {
		this.puzzle = puzzle;
		this.numCars = this.puzzle.getNumCars();
		this.visited = new ArrayList<Integer>();
	}

	/**
	 * This method returns the value of the heuristic function at the given
	 * state.
	 */
	public int getValue(State state) {
		this.state = state;
		this.visited.clear();
		
		if (state.isGoal()) {
			return 0;
		}
		
		return this.getMinimumRequiredMoves(state);
	}
	
	private int getMinimumRequiredMoves(State state) {
		visited.add(0);
		
		int value = 1;
		
		for (Integer car : getInitialBlockingCars()) {
			int needsSpaceFront = needsSpace(0, car, 0, true);
			int needsSpaceBack = needsSpace(0, car, 0, false);
			
			value += getBlockingValue(car, needsSpaceFront, needsSpaceBack);
		}
		
		return value;
	}
	
	private ArrayList<Integer> getInitialBlockingCars() {		
		ArrayList<Integer> blocking = new ArrayList<Integer>();
		
		boolean carOrient = puzzle.getCarOrient(0);
		int carSize = puzzle.getCarSize(0);
		int carPos = state.getVariablePosition(0);
		int carFixed = puzzle.getFixedPosition(0);
		
		for (int i = 1; i < this.numCars; i++) {
			
			if (carOrient == puzzle.getCarOrient(i)) {
				continue;
			}
			
			int iFixed = puzzle.getFixedPosition(i);
			
			if (iFixed < carPos + carSize) {
			 	continue;
			}
			
			int iPos = state.getVariablePosition(i);
			int iPosFront = iPos + puzzle.getCarSize(i);
			
			if (carFixed >= iPos && carFixed < iPosFront) {
				blocking.add(i);
			}
			
		}
		
		return blocking;	
	}
	
	private int getBlockingValue(int car, int needsSpaceFront, int needsSpaceBack) {
		visited.add(car);
		
		int value = 1;
		
		for (int next = 0; next < numCars; next++) {		
			if (next == car) {
				continue;
			}
			
			if (visited.contains(next)) {
				continue;
			}
			
			if (!isIntersecting(car, next)) {
				continue;
			}
			
			int valueFwd = 0, valueBwd = 0;
			
			boolean fwdMoveable = canMove(car, next, needsSpaceFront, true);
			boolean bwdMoveable = canMove(car, next, needsSpaceBack, false);
			
			int needsSpaceFwd = needsSpace(car, next, needsSpaceFront, true);
			int needsSpaceBwd = needsSpace(car, next, needsSpaceBack, false);
			
			if (!fwdMoveable) {
				valueFwd = getBlockingValue(next, needsSpaceFwd, needsSpaceBwd);
			} else if (isWallBlocking(car, needsSpaceFront, true)) {
				valueFwd = Integer.MAX_VALUE;
			}
			
			if (!bwdMoveable) {
				valueBwd = getBlockingValue(next, needsSpaceFwd, needsSpaceBwd);
			} else if (isWallBlocking(car, needsSpaceBack, false)) {
				valueBwd = Integer.MAX_VALUE;
			}
			
			value += Math.min(valueFwd, valueBwd);
		}
		
		return value;
	}
	
	private boolean canMove(int car, int next, int needsSpace, boolean direction) {
		boolean isBehind = isBehind(car, next);
		
		if (isBehind && direction || !isBehind && !direction) {
			return true;
		}
		
		int hasSpace = hasSpace(car, next, direction);
		return hasSpace >= needsSpace;
	}
	
	private int needsSpace(int car, int next, int needsSpace, boolean direction) {
		if (puzzle.getCarOrient(car) == puzzle.getCarOrient(next)) {
			int hasSpace = hasSpace(car, next, direction);
			return needsSpace - hasSpace;
		}
		
		int carFixed = puzzle.getFixedPosition(car);
		int nextPos = state.getVariablePosition(next);
		
		if (direction) {
			return Math.abs(carFixed - nextPos) + 1;
		}
		
		int nextPosFront = nextPos + puzzle.getCarSize(next);
		
		return Math.abs(carFixed - nextPosFront);
	}
	
	private int hasSpace(int car, int next, boolean direction) {
		int carPos = state.getVariablePosition(car);
		int carPosFront = carPos + puzzle.getCarSize(car);
		int nextFixed = puzzle.getFixedPosition(next);
		
		if (puzzle.getCarOrient(car) != puzzle.getCarOrient(next)) {
			if (direction) {
				return Math.abs(carPosFront - nextFixed);
			}
			
			return Math.abs(carPos - nextFixed) + 1;
		}
		
		int nextPos = state.getVariablePosition(next);
		
		if (direction) {
			return Math.abs(carPosFront - nextPos);
		}
		
		int nextPosFront = nextPos + puzzle.getCarSize(next);
		
		return Math.abs(carPos - nextPosFront);
	}
	
	private boolean isWallBlocking(int car, int needsSpace, boolean direction) {
		int carPos = state.getVariablePosition(car);
		int carPosFront = carPos + puzzle.getCarSize(car);
		
		if (direction && (carPosFront + needsSpace > puzzle.getGridSize())) {
			return true;
		}
		
		if (!direction && (carPos - needsSpace < 0)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isIntersecting(int car, int next) {
		int carFixed = puzzle.getFixedPosition(car);
		int nextFixed = puzzle.getFixedPosition(next);
		
		if (puzzle.getCarOrient(car) == puzzle.getCarOrient(next)) {
			return carFixed == nextFixed;
		}
		
		int nextPos = state.getVariablePosition(next);
		int nextPosFront = nextPos + puzzle.getCarSize(next);
		
		return carFixed >= nextPos && carFixed < nextPosFront;
	}
	
	private boolean isBehind(int car, int next) {
		int carSize = puzzle.getCarSize(car);
		int carPos = state.getVariablePosition(car);
		int nextPos = state.getVariablePosition(next);
		int nextSize = puzzle.getCarSize(next);
		
		if (this.puzzle.getCarOrient(car) == this.puzzle.getCarOrient(next)) {
			return nextPos + nextSize <= carPos;
		}
		
		int nextFixed = puzzle.getFixedPosition(next);
		
		return nextFixed < carPos + carSize;
	}

}