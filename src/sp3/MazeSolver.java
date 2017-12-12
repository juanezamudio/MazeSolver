package sp3;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JFrame;

//import sp3.*;
import sp3.Maze.*;



public class MazeSolver {
	
	public static double COUNT_BFS = 0.0;
	public static double COUNT_DFS = 0.0;
	public static double AVERAGE_DFS = 0.0;
	public static double COUNT_ASTAR = 0.0;
	public static double COUNT_BIBFS = 0.0;
	
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
						result = "Found";
					}
				}
			}
		}
		COUNT_BFS = COUNT_BFS/average;
		
		return result;
	}
	
	/**
	 * 
	 * @param maze
	 * @param start
	 * @param goal
	 * @param stack
	 * @param visited
	 * @return
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
			result = "Found";	
		}
		
		for (Position u : maze.getNeighboringSpaces(start)) {
			COUNT_DFS += 1;
			
			if (!visited.contains(u) && (dfs(maze, u, goal, path, visited) == "Found")) {
				path.add(u);
				result = "Found";
			}
		}
		
		stack.pop();
		return result;
	}
	
	/**
	 * 
	 * @param maze
	 * @param start
	 * @param goal
	 * @return
	 */
	public void start_dfs (Maze maze, Position start, Position goal, List<Position> path) {
		Set<Position> visited = new HashSet<Position>();
		this.dfs(maze, start, goal, path, visited);
		path.add(start);
		COUNT_DFS = COUNT_DFS/AVERAGE_DFS;
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
		myMaze.start_dfs(m, startPos, goalPos, path);
		
		
		// TODO: Count m.getNeighboringSpaces(p) calls
//		System.out.println("BFS makes: " + Double.toString(COUNT_BFS) + " calls on average");
		System.out.println("DFS makes: " + Double.toString(COUNT_DFS) + " calls on average");
		
		
		
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
