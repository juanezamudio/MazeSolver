package sp3;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

import javax.swing.JFrame;

//import sp3.*;
import sp3.Maze.*;



public class MazeSolver {
	
	public static double COUNT_BFS = 0.0;
	public static double COUNT_DFS = 0.0;
	public static double COUNT_ASTAR = 0.0;
	public static double COUNT_BIBFS = 0.0;
	
	public HashMap<Position, Position> mapStart = new HashMap<Position, Position>();
	public HashMap<Position, Position> mapGoal = new HashMap<Position, Position>();
	public static Position CURR_GOAL;
	
	/**
	 * Method that places the correct positions in a list (i.e. finds solution path)
	 * @param start - The starting position
	 * @param goal - The target position
	 * @param parent - The parent map
	 * @param path - The list where the solution positions are placed
	 * @return - The list of solution positions (i.e. solution path)
	 */
	private List<Position> correctPath(Position start, 
									 Position goal, 
									 HashMap<Position, Position> parent, 
									 List<Position> path) {
		
		
		Position pos = goal;
		
		while (!pos.equals(start)) {
			path.add(0, pos);
			pos = parent.get(pos);
		}
		
		path.add(0, start);
		
		return path;
	}
	
	private List<Position> correctPathBi(Position start, 
									   Position currGoal, 
									   HashMap<Position, Position> parent,
									   List<Position> path,
									   int val,
									   Position goal) {
		
		switch (val) {
			
			case 1: {
				Position pos = currGoal;
				
				while (!pos.equals(start)) {
					//System.out.print(path);
					path.add(0, pos);
					pos = parent.get(pos);
				}
				
				path.add(0, start);
				break;
			}
			
			case 2: {
				Position finalGoal = goal;
				Position pos = currGoal;
				
				while (!pos.equals(finalGoal)) {
					path.add(pos);
					pos = parent.get(pos);
					
					if (pos == null) {
						break;
					}
				}
				
				path.add(finalGoal);
				break;
			}
			
		}
		
		return path;
	}
	
	/**
	 * BFS Algorithm to solve the Maze
	 * @param maze - The maze
	 * @param start - The starting position
	 * @param goal - The target position
	 * @param path - The list of position in the solution path
	 * @return - A string denoting whether or not the target was found
	 */
	public String bfs (Maze maze, Position start, Position goal, List<Position> path) {
		Set<Position> visited = new HashSet<Position>();
		HashMap<Position, Position> parent = new HashMap<Position, Position>();
		Queue<Position> queue = new LinkedList<Position>();
		String result = "Not Found";
		
		visited.add(start);
		parent.put(start, null);
		queue.add(start);
		
		while (!queue.isEmpty()) {
			Position v = queue.remove();
			
			for (Position u : maze.getNeighboringSpaces(v)) {
				COUNT_BFS += 1;
				
				if (!visited.contains(u)) {
					visited.add(u);
					parent.put(u, v);
					queue.add(u);
					
					if (u.equals(goal)) {
						this.correctPath(start, goal, parent, path);
						result = "Target Found";
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * DFS Algorithm to solve the Maze
	 * @param maze - The maze
	 * @param start - The starting position
	 * @param goal - The target position
	 * @param stack - The stack used to keep track of current position
	 * @param visited - The set of visited positions
	 * @return - String denoting whether the target was found or not
	 */
	public String dfs (Maze maze, 
					  Position start, 
					  Position goal, 
					  List<Position> path, 
					  Set<Position> visited) {
		
		String result = "Not Found";
		Stack<Position> stack = new Stack<Position>();
		
		stack.add(start);
		visited.add(start);
		
		if (start.equals(goal)) {
			result = "Target Found";	
		}
		
		for (Position u : maze.getNeighboringSpaces(start)) {
			COUNT_DFS += 1;
			
			if (!visited.contains(u) && (dfs(maze, u, goal, path, visited) == "Target Found")) {
				path.add(u);
				result = "Target Found";
			}
		}
		
		stack.pop();
		return result;
	}
	
	/**
	 * Method that calls the DFS function with the appropriate parameters, keeping track of count
	 * @param maze - The maze
	 * @param start - The starting position
	 * @param goal - The target goal
	 * @return - void
	 */
	public void start_dfs (Maze maze, Position start, Position goal, List<Position> path) {
		Set<Position> visited = new HashSet<Position>();
		this.dfs(maze, start, goal, path, visited);
		path.add(start);
	}
	
	private Integer hfunc(Position a, Position b){
		int h = (int)Math.sqrt(Math.pow((a.x-b.x), 2) + Math.pow((a.y-b.y), 2));
		return h;
	}
	
	public String aStar (Maze maze, Position start, Position goal, List<Position> path) {
		Queue<Entry> pQueue = new PriorityQueue<Entry>();
		Map<Position, Integer> dist = new HashMap<Position, Integer>();
		HashMap<Position, Position> parent = new HashMap<Position, Position>();
		String result = "Not Found";
		
		Entry startPos = new Entry(start, hfunc(start, new Position(0,0)));
		
		pQueue.add(startPos);
		dist.put(start, 0);
		parent.put(start, null);
		
		while (!pQueue.isEmpty()) {
			Entry n = pQueue.poll();
			Position nPos = n.getKey();
			
			if (nPos.equals(goal)) {
				this.correctPath(start, goal, parent, path);
				result = "Target Found";
			}
			
			for (Position u : maze.getNeighboringSpaces(nPos)) {
				COUNT_ASTAR += 1;
				
				if (dist.get(u) == null) {
					Entry uPos = new Entry(u, hfunc(u, goal));
					
					pQueue.add(uPos);
					dist.put(u, Integer.MAX_VALUE);
				}
				
				if (dist.get(n.getKey()) + hfunc(nPos, u) < dist.get(u)) {
					int uDist = dist.get(u);
					Entry oldPos = new Entry(u, uDist);
					
					uDist = dist.get(nPos) + hfunc(nPos, u);
					dist.put(u, uDist);
					
					Entry newPos = new Entry(u, dist.get(u) + hfunc(nPos, u));
					
					if (pQueue.contains(oldPos)) {
						pQueue.remove(oldPos);
						pQueue.add(newPos);
					} else {
						pQueue.add(newPos);
					}
					
					parent.put(u, nPos);
				}
				
			}
			
		}
		
		return result;
	}
	
	public String start_bibfs (Maze maze, Position start, Position goal, List<Position> path) {
		Queue<Position> queueStart = new LinkedList<Position>();
		Queue<Position> queueGoal = new LinkedList<Position>();
		ArrayList<Position> pathStart = new ArrayList<Position>();
		ArrayList<Position> pathGoal = new ArrayList<Position>();
		
		String result = "Not Found";
		
		pathStart.add(start);
		pathGoal.add(goal);
		queueStart.add(start);
		queueGoal.add(goal);
		
		mapStart.put(start, null);
		mapGoal.put(goal, null);
		
		while (!queueStart.isEmpty() && !queueGoal.isEmpty()) {
			
			if (this.bibfs(maze, queueStart, queueGoal, pathStart, mapStart)) {
				List<Position> listStart = this.correctPathBi(start, CURR_GOAL, mapStart, path, 1, goal);
				List<Position> listGoal = this.correctPathBi(start, CURR_GOAL, mapGoal, path, 2, goal);
				result = "Target Found";
			} else if (this.bibfs(maze, queueGoal, queueStart, pathGoal, mapGoal)) {
				List<Position> listStart = this.correctPathBi(start, CURR_GOAL, mapStart, path, 1, goal);
				List<Position> listGoal = this.correctPathBi(start, CURR_GOAL, mapGoal, path, 2, goal);
				result = "Target Found";
			}
		}
		
		return result;
	}
	
	public boolean bibfs(Maze maze, 
						Queue<Position> queueStart, 
						Queue<Position> queueGoal,
						ArrayList<Position> visited, 
						HashMap<Position, Position> map){
		
		Position n = queueStart.remove();
		
		for (Position u: maze.getNeighboringSpaces(n)) {
			COUNT_BIBFS += 1;
			
			if(queueGoal.contains(u)){
				map.put(u, n);
				this.matched(u);
				return true;
			} else if (!visited.contains(u)) {
				map.put(u, n);
				queueStart.add(u);
				visited.add(u);
			}
		}

		return false;
	}
	
	private Position matched (Position pos) {
		CURR_GOAL = pos;
		return CURR_GOAL;
	}
	
	public static void countNeighborCalls(MazeSolver object,
								  int dimension,
								  Position startPos,
								  Position goalPos,
								  List<Position> path) {
		
		double average = 10.0;
		
		Maze maze = new Maze(dimension);
		
		for (double i = 0.0; i <= average; i++) {
			
			object.bfs(maze, startPos, goalPos, path);
			path.clear();
			
			object.start_dfs(maze, startPos, goalPos, path);
			path.clear();
			
			object.aStar(maze, startPos, goalPos, path);
			path.clear();
			
			object.start_bibfs(maze, startPos, goalPos, path);
			path.clear();
		}
		
		COUNT_BFS /= average;
		COUNT_DFS /= average;
		COUNT_ASTAR /= average;
		COUNT_BIBFS /= average;
		
		System.out.println("For 10 algorithm calls with a maze dimension of: " + dimension + " by " + dimension);
		System.out.println("BFS makes: " + Double.toString(COUNT_BFS) + " getNeighboringSpace calls on average");
		System.out.println("DFS makes: " + Double.toString(COUNT_DFS) + " getNeighboringSpace calls on average");
		System.out.println("A* makes: " + Double.toString(COUNT_ASTAR) + " getNeighboringSpace calls on average");
		System.out.println("BiBFS makes: " + Double.toString(COUNT_BIBFS) + " getNeighboringSpace calls on average");
		
		System.out.println();
		System.out.println();
	}
	
	public static void main(String[] args) {	
		int dim = 20;
		Maze m = new Maze(dim);

		Position startPos = new Position(0,0);
		Position goalPos = new Position(dim-1,dim-1);
		
		List<Position> path = new ArrayList<Position>();
		List<Position> countPath = new ArrayList<Position>();
//		path.add(startPos);
		
		// TODO: Implement a search algorithm to find a path from start to goal
		MazeSolver myMaze = new MazeSolver();
		
		//--------------UNCOMMENT BELOW TO TEST ALGORITHMS---------------------
		
		
		//--------------------BFS Algorithm Call-------------------------------
//		myMaze.bfs(m, startPos, goalPos, path);
		
		//--------------------DFS Algorithm Call-------------------------------
//		myMaze.start_dfs(m, startPos, goalPos, path);
		
		//--------------------A* Algorithm Call--------------------------------
//		myMaze.aStar(m, startPos, goalPos, path);
		
		//--------------------Bidirectional BFS Call---------------------------
//		myMaze.start_bibfs(m, startPos, goalPos, path);
		
		// TODO: Count m.getNeighboringSpaces(p) calls
		// Needs about 4GB of Memory and will take about an hour and 40 minutes
		countNeighborCalls(myMaze, 25, startPos, goalPos, countPath);
		countNeighborCalls(myMaze, 50, startPos, goalPos, countPath);
		countNeighborCalls(myMaze, 100, startPos, goalPos, countPath);
		countNeighborCalls(myMaze, 200, startPos, goalPos, countPath);
		countNeighborCalls(myMaze, 400, startPos, goalPos, countPath);
		
		
		JFrame f = new JFrame();
		f.setTitle("SP3: Maze");
		f.setSize(1000, 1000);
		f.setLocation(50, 50);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		f.getContentPane().add(new MazePanel(m,path));
		f.setVisible(true);
		f.validate();
		f.repaint();
	}
}
