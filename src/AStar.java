
import java.util.ArrayList;
import java.util.List;

/**
 * This is the template for a class that performs A* search on a given
 * rush hour puzzle with a given heuristic.  The main search
 * computation is carried out by the constructor for this class, which
 * must be filled in.  The solution (a path from the initial state to
 * a goal state) is returned as an array of <tt>State</tt>s called
 * <tt>path</tt> (where the first element <tt>path[0]</tt> is the
 * initial state).  If no solution is found, the <tt>path</tt> field
 * should be set to <tt>null</tt>.  You may also wish to return other
 * information by adding additional fields to the class.
 */
//test commit 2
public class AStar {

    /** The solution path is stored here */
    public State[] path;
    
    private List<HeuristicNode> open = new ArrayList<HeuristicNode>(); //List untuk menyimpan node open
    private List<HeuristicNode> closed = new ArrayList<HeuristicNode>(); //List untuk menyimpan node closed

    /**
     * This is the constructor that performs A* search to compute a
     * solution for the given puzzle using the given heuristic.
     */
    public AStar(Puzzle puzzle, Heuristic heuristic) {
    	
    	// Initialize root node w/ heuristics and path costs
    	int h = heuristic.getValue(puzzle.getInitNode().getState()); //mengambil nilai heuristic dari node
    	HeuristicNode root = new HeuristicNode(puzzle.getInitNode(), h); 
    	
    	open.add(root);	// Add the root node to the open list
    	
    	while(!open.isEmpty()) {
    		
    		// Only performs sort if list was changed
    		//open.sort();
    		
    		HeuristicNode current = open.remove(0);
    		
    		if (current.getState().isGoal()) {
    			
    			// Set the path array size to depth of goal state;
    			// The +1 should be necessary to also include root node.
    			path = new State[current.getDepth() + 1]; //membuat path baru 
    			
    			// Set the current node to pathNode
    			HeuristicNode pathNode = current;
    			
    			// Get state for every node and store it in the path array,
    			// then override current path node with its parent node until parent is null.
    			while (pathNode != null) {
    				path[pathNode.getDepth()] = pathNode.getState();
    				pathNode = (HeuristicNode) pathNode.getParent();
    			} 
    			
    			// We found a solution, stop.
    			return;
    		}
    		
    		closed.add(current);
    		
                //Expand node
    		for (Node successor : current.expand()) {

    			h = heuristic.getValue(successor.getState());
    			HeuristicNode hSuccessor = new HeuristicNode(successor, h);
    			
    			if (open.contains(hSuccessor)) {
    				keepBetterNodeOnOpenList(hSuccessor);
    			} else if (!closed.contains(hSuccessor)) {
    				open.add(hSuccessor);
    			}
    		}

    	}

    }
    
    // Idea from: http://web.mit.edu/eranki/www/tutorials/search/
      private void keepBetterNodeOnOpenList(HeuristicNode successor) {
          HeuristicNode existing = open.get(open.size()-1);
    	
    	if (existing != null) {
    		if (existing.compareTo(successor) > 0) { //Jika existing > dari successor maka ambil successornya
    			open.remove(existing);
    			open.add(successor);
    		}
    	}
    }

}