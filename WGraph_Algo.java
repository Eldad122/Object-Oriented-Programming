package ex1;

import java.io.*;
import java.util.*;

//WGraph_Algo represent algorithms on graph
public class WGraph_Algo implements weighted_graph_algorithms, Serializable {
	private weighted_graph wg;

	public WGraph_Algo() {// constructor
	}

	/**
	 * this method get a graph g and init wg to be equal to g
	 * 
	 * @param g
	 */
	public void init(weighted_graph g) {
		this.wg = g;
	}

	/**
	 * get Graph
	 * 
	 * @return wg
	 */
	@Override
	public weighted_graph getGraph() {
		return this.wg;
	}

	/**
	 * this method create in deep copy a copy of this.wg and return it's
	 * 
	 * @return copy
	 */
	@Override
	public weighted_graph copy() {
		weighted_graph copy = new WGraph_DS(this.wg);
		return copy;
	}

	/**
	 * the method initialize all the info fields to be null
	 */
	private void initializeInfo() {
		Iterator<node_info> it = this.wg.getV().iterator();
		while (it.hasNext()) {
			it.next().setInfo(null);
		}
	}

	/**
	 * the method initialize all the node_tag to infinity
	 */
	private void initializeTag() {
		Iterator<node_info> it = this.wg.getV().iterator();
		while (it.hasNext()) {
			it.next().setTag(Double.POSITIVE_INFINITY);
		}
	}

	/**
	 * the method returns true iff there is a valid path from EVREY node to each
	 * other node.
	 * 
	 * @return true if the graph is linked else return false
	 */
	@Override
	public boolean isConnected() {
		if (this.wg.edgeSize() < this.wg.nodeSize() - 1) {
			return false;
		}
		initializeInfo();// initialize all the info fields
		if (this.wg.nodeSize() == 0 || this.wg.nodeSize() == 1) {
			return true;// if there is no node or one its connected
		}
		WGraph_DS copy = (WGraph_DS) (copy());// create a copy graph that the algorithm will be implemented on
		LinkedList<node_info> qValues = new LinkedList<>();// create linked list that will storage the nodes we haven't visited
		int firstNodeKey = copy.getV().iterator().next().getKey();
		node_info first = copy.getNode(firstNodeKey);
		qValues.add(first);
		int countVisited = 0;// count the times we change the info of a node to visited
		while (qValues.size() != 0) {
			node_info current = qValues.removeFirst();
			if (current.getInfo() != null) {
				continue;// if its not a null we've already marked it so we can skip to the next loop
			}
			current.setInfo("visited");// remark
			countVisited++;

			Collection<node_info> listNeighbors = copy.getV(current.getKey());
			LinkedList<node_info> Neighbors = new LinkedList<>(listNeighbors);
			if (Neighbors == null) {
				continue;
			}
			for (node_info n : Neighbors) {
				if (n.getInfo() == null) {// if there is a node we haven't visited yet, we will insert it to the linkedList
					qValues.add(n);
				}
			}
		}
		if (this.wg.nodeSize() != countVisited)
			return false;

		return true;
	}

	/**
	 * the method compute the distance of the shortest path from src to dest in the
	 * graph
	 * 
	 * @param src  - start node
	 * @param dest - end (target) node
	 * @return distance of shortest path src-->dest
	 */
	@Override
	public double shortestPathDist(int src, int dest) {
		double sum = 0;
		if (shortestPath(src, dest) == null)
			return -1;
		return shortestPath(src, dest).get(shortestPath(src, dest).size() - 1).getTag();
	}

	/**
	 * the method return a list represent the path from source to destination in the
	 * graph
	 * 
	 * @param src  - start node
	 * @param dest - end (target) node
	 * @return List<node_data>
	 */
	@Override
	public List<node_info> shortestPath(int src, int dest) {
		int counter = 0;// count the index of listPath
		List<node_info> listPath = new ArrayList<node_info>();// The reverse list that is returned
		List<node_info> listResult = new ArrayList<node_info>();// the returned list
		if (!DijkstraHelper(src, dest))
			return null;// if there is no path
		if (src == dest) {
			listPath.add(this.wg.getNode(src));
			return listPath;
		}
		node_info d = this.wg.getNode(dest);
		listPath.add(counter, d);
		weighted_graph gCopy = copy();
		Iterator<node_info> it = gCopy.getV().iterator();
		while (it.hasNext()) {
			if (listPath.get(counter).getKey() == src)
				break;//when we finish
			if (gCopy.getV(listPath.get(counter).getKey()).contains(it.next())) {// remove the nodes that we were already checked if they need to be inserted to listPath
				continue;
			}
			Iterator<node_info> numIt = gCopy.getV(listPath.get(counter).getKey()).iterator();
			if (numIt != null) {
				node_info min = null;
				while (numIt.hasNext()) {
					node_info currentLoop = numIt.next();
					if (currentLoop.getTag() + gCopy.getEdge(listPath.get(counter).getKey(),
							currentLoop.getKey()) == listPath.get(counter).getTag()) {
						min = currentLoop;
					}
				}
				listPath.add(min);// insert to listPath
				counter++;
			}
		}
		for (int i = listPath.size() - 1; i >= 0; i--) {
			listResult.add(listPath.size() - i - 1, listPath.get(i));// reversing the list
		}
		return listResult;
	}

	//////////////////////////////////////////////////////////////////////////////
	// class Entry to enable the priority queue to storage node info objects and sort by the minimum key
	private class Entry implements Comparable<Entry> {
		private node_info node;
		private double tag;

		public Entry(node_info node, double tag) {
			this.tag = tag;
			this.node = node;
		}

		/**
		 * compre to between tags for implementation Comparable
		 * 
		 * @param other
		 * @return
		 */
		@Override
		public int compareTo(Entry other) {
			if (this.tag > other.tag)
				return 1;
			if (this.tag == other.tag)
				return 0;
			return -1;
		}
	}

	/**
	 * this method change each tag to the distance between src to the mode
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	public boolean DijkstraHelper(int src, int dest) {//Dijkstra Algo
		PriorityQueue<Entry> queue = new PriorityQueue();// queue storages the nodes that we will visit by the minimum tag
		initializeTag();
		initializeInfo();
		node_info first = this.wg.getNode(src);
		first.setTag(0);//the distance from itself is 0
		queue.add(new Entry(first, first.getTag()));
		while (!queue.isEmpty()) {
			Entry pair = queue.poll();
			node_info current = pair.node;
			if (current.getInfo() != null)
				continue;// if we visit we can skip to the next loop because we've already marked
			current.setInfo("visited");// remark
			Collection<node_info> listNeighbors = this.wg.getV(current.getKey());
			LinkedList<node_info> Neighbors = new LinkedList<>(listNeighbors);
			if (Neighbors == null)
				continue;
			for (node_info n : Neighbors) {

				if (n.getTag() > wg.getEdge(n.getKey(), current.getKey()) + current.getTag()) {
					n.setTag(current.getTag() + wg.getEdge(n.getKey(), current.getKey()));
				}
				queue.add(new Entry(n, n.getTag()));
			}
		}
		Iterator<node_info> it = this.wg.getV().iterator();
		while (it.hasNext()) {
			if (it.next().getInfo() == null)
				return false;
		}
		return true;
	}

	/**
	 * this method Saves the graph to the given file name
	 * 
	 * @param file - the file name (may include a relative path).
	 * @return true if the file saved and false if not
	 */
	@Override
	public boolean save(String file) {
		boolean ans = false;
		ObjectOutputStream oos;
		try {
			FileOutputStream fileOut = new FileOutputStream(file, true);
			oos = new ObjectOutputStream(fileOut);
			oos.writeObject(this);
			ans = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * This method load a graph to this graph algorithms
	 * 
	 * @param file - file name
	 * @return true if the file changed else false
	 */
	@Override
	public boolean load(String file) {
		try {
			FileInputStream streamIn = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(streamIn);//ois = ObjectInputStream
			WGraph_Algo readCase = (WGraph_Algo) ois.readObject();
			this.wg = null;
			this.wg = readCase.wg;// take the graph from readCase to this.wg
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}