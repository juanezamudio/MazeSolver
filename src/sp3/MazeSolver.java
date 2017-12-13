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
	public static double AVERAGE_DFS = 0.0;
	public static double COUNT_ASTAR = 0.0;
	public static double COUNT_BIBFS = 0.0;
	
	public Map<Position, Position> mapStart = new HashMap<Position, Position>();
	public Map<Position, Position> mapGoal = new HashMap<Position, Position>();
	public Position currGoal;
	
	/**
	 * Method that places the correct positions in a list (i.e. finds solution path)
	 * @param start - The starting position
	 * @param goal - The target position
	 * @param parent - The parent map
	 * @param path - The list where the solution positions are placed
	 * @return - The list of solution positions (i.e. solution path)
	 */
	public List<Position> correctPath(Position start, 
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
	
	public static List<Position> correctPathBi (Position start, 
											   Position goal,
											   HashMap<Position, Position> parent, 
											   List<Position> path, 
											   int bool, 
											   Position goalPos) {
	        // construct output list
			if(bool == 2){
				start = goalPos;
				Position currNode = goal;
		        while(!currNode.equals(start)){
		            path.add(currNode);
		            currNode = parentMap.get(currNode);
		        }
		        path.add(start);
			}else if(bool ==1){
			
	        Position currNode = goal;
	        while(!currNode.equals(start)){
	            path.add(0, currNode);
	            currNode = parentMap.get(currNode);
	        }
	        path.add(0 , start);
	        
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
		double average = 0;
		
		visited.add(start);
		parent.put(start, null);
		queue.add(start);
		
		while (!queue.isEmpty()) {
			Position v = queue.remove();
			average += 1;
			
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
		COUNT_BFS = COUNT_BFS/average;
		
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
		AVERAGE_DFS += 1;
		
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
		COUNT_DFS = COUNT_DFS/AVERAGE_DFS;
	}
	
	public Integer hfunc(Position a, Position b){
		int h = (int)Math.sqrt(Math.pow((a.x-b.x), 2) + Math.pow((a.y-b.y), 2));
		return h;
	}
	
	public String aStar (Maze m, Position start, Position goal, List<Position> path) {
		Queue<Entry> pQueue = new PriorityQueue<Entry>();
		Map<Position, Integer> dist = new HashMap<Position, Integer>();
		HashMap<Position, Position> parent = new HashMap<Position, Position>();
		String result = "Not Found";
		double average = 0.0;
		
		Entry startPos = new Entry(start, hfunc(start, new Position(0,0)));
		
		pQueue.add(startPos);
		dist.put(start, 0);
		parent.put(start, null);
		
		while (!pQueue.isEmpty()) {
			Entry n = pQueue.poll();
			Position nPos = n.getKey();
			average += 1;
			
			if (nPos.equals(goal)) {
				this.correctPath(start, goal, parent, path);
				result = "Target Found";
			}
			
			for (Position u : m.getNeighboringSpaces(nPos)) {
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
		
		COUNT_ASTAR = COUNT_ASTAR/average;
		return result;
	}
	
	public String bibfs (Maze maze, Position start, Position goal, List<Position> path) {
		Queue<Position> queueStart = new LinkedList<Position>();
		Queue<Position> queueGoal = new LinkedList<Position>();
		List<Position> pathStart = new ArrayList<Position>();
		List<Position> pathGoal = new ArrayList<Position>();
		
		List<Position> listStart;
		List<Position> listGoal;
		
		pathStart.add(start);
		pathGoal.add(goal);
		queueStart.add(start);
		queueGoal.add(goal);
		
		mapStart.put(start, null);
		mapGoal.put(goal, null);
		
		while (!queueStart.isEmpty() && !queueGoal.isEmpty()) {
			
			if (this.start_bibfs(maze, start, goal, path, queueStart, queueGoal, pathStart, mapStart)) {
				listStart = 
			}
		}
		
	}
	
	public boolean start_bibfs(Maze maze,
							  Position start, 
							  Position goal,
							  List<Position> path,
							  Queue<Position> queueStart, 
							  Queue<Position> queueGoal, 
							  List<Position> visited,
							  Map<Position, Position> map){
		
		Position n = queueStart.remove();
		
		for(Position u: maze.getNeighboringSpaces(n)){
			
			if(queueGoal.contains(u)){
				map.put(u, n);
				currGoal = u;
				return true;
			} else if (!visited.contains(u)) {
				map.put(u, n);
				queueStart.add(u);
				visited.add(u);
			}
		}

		return false;
	}
	
	public static void main(String[] args) {	
		int dim = 20;
		Maze m = new Maze(dim);

		Position startPos = new Position(0,0);
		Position goalPos = new Position(dim-1,dim-1);
		
		List<Position> path = new ArrayList<Position>();
//		path.add(startPos);
		
		// TODO: Implement a search algorithm to find a path from start to goal
		MazeSolver myMaze = new MazeSolver();
		
		//--------------------BFS Algorithm Call-------------------------------
//		myMaze.bfs(m, startPos, goalPos, path);
		
		//--------------------DFS Algorithm Call-------------------------------
//		myMaze.start_dfs(m, startPos, goalPos, path);
		
		//--------------------A* Algorithm Call--------------------------------
//		myMaze.aStar(m, startPos, goalPos, path);
		
		
		// TODO: Count m.getNeighboringSpaces(p) calls
//		System.out.println("BFS makes: " + Double.toString(COUNT_BFS) + " calls on average");
//		System.out.println("DFS makes: " + Double.toString(COUNT_DFS) + " calls on average");
//		System.out.println("A* makes: " + Double.toString(COUNT_ASTAR) + " calls on average");
		
		
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
