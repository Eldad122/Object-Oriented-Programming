package ex1;

import java.io.Serializable;
import java.util.*;

//this class represents an undirectional weighted graph

public class WGraph_DS implements weighted_graph, Serializable {

	private int MC;// mode counter to count the number of changes on the graph
	private int nodeSize;// number of nodes in the graph
	private int edges;// number of edges in the graph
	private HashMap<Integer, node_info> graph;// Hash-Map structure represents the graph

	/**
	 * constructor for WGraph_DS
	 */
	public WGraph_DS() {
		this.MC = 0;
		this.edges = 0;
		this.nodeSize = 0;
		this.graph = new HashMap<Integer, node_info>();
	}

	/**
	 * constructor for graph g in the parameter
	 * 
	 * @param g
	 */
	public WGraph_DS(weighted_graph g) {
		this.MC = 0;
		this.edges = g.edgeSize();
		this.nodeSize = g.nodeSize();
		this.graph = new HashMap<Integer, node_info>();
		Iterator<node_info> it = g.getV().iterator();
		while (it.hasNext()) {
			node_info current = it.next();
			int currentKey = current.getKey();
			this.graph.put(currentKey, current);
		}
	}

	/**
	 *
	 * @param key - the node_id
	 * @return the method return node by giving key if the key not found return null
	 */
	@Override
	public node_info getNode(int key) {
		if (this.graph.containsKey(key))
			return this.graph.get(key);
		return null;
	}

	/**
	 * the method return true if there is edge between node1 and node2 else return
	 * false
	 * 
	 * @param node1
	 * @param node2
	 * @return true if the edge exist else false
	 */
	@Override
	public boolean hasEdge(int node1, int node2) {
		NodeInfo n1 = (NodeInfo) getNode(node1);
		NodeInfo n2 = (NodeInfo) getNode(node2);
		if (n1 == null || n2 == null)
			return false;
		if (n1.ni == null)
			return false;
		if (n1.ni.containsKey(node2))
			return true;
		return false;
	}

	/**
	 * the method return the edge between node1 to node2
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	@Override
	public double getEdge(int node1, int node2) {
		double no = -1.0;
		if (hasEdge(node1, node2)) {
			NodeInfo n = (NodeInfo) getNode(node1);
			return (n.weights.get(node2)).doubleValue();
		}
		return no;
	}

	/**
	 * the method add node to this.graph
	 * 
	 * @param key
	 */
	@Override
	public void addNode(int key) {
		if (graph.containsKey(key))
			return;
		NodeInfo node = new NodeInfo(key);
		this.graph.put(key, node);
		this.MC++;
		this.nodeSize++;
	}

	/**
	 * the method create an edge between node1 and node2 and if there is an edge its
	 * update the edge
	 * 
	 * @param node1
	 * @param node2
	 */
	@Override
	public void connect(int node1, int node2, double w) {
		if (node1 == node2)
			return;
		NodeInfo n1 = (NodeInfo) getNode(node1);
		NodeInfo n2 = (NodeInfo) getNode(node2);
		if (hasEdge(node1, node2)) {
			n1.weights.put(node2, w);// update the weight
			n2.weights.put(node1, w);
			this.MC++;
			return;
		}
		n1.ni.put(node2, n2);
		n2.ni.put(node1, n1);
		n1.weights.put(node2, w);
		n2.weights.put(node1, w);
		this.MC++;
		this.edges++;
	}

	/**
	 * the method return a shallow copy of the graph
	 * 
	 * @return shallow copy
	 */
	@Override
	public Collection<node_info> getV() {
		Collection<node_info> shallowCopy = this.graph.values();
		return shallowCopy;
	}

	/**
	 * This method return a collection representing all the nodes connected to
	 * node_id
	 * 
	 * @param node_id
	 * @return collection representing all the nodes connected to node_id
	 */
	@Override
	public Collection<node_info> getV(int node_id) {
		NodeInfo n = (NodeInfo) getNode(node_id);
		if (n == null)
			return null;
		return n.ni.values();
	}

	/**
	 * the method remove a node from the graph
	 * 
	 * @param key
	 * @return removal node
	 */
	@Override
	public node_info removeNode(int key) {
		node_info removalNode = (getNode(key));
		if (removalNode != null) {
			Iterator<node_info> iterator = getV(key).iterator();
			while (iterator.hasNext()) {
				int keyCurrent = iterator.next().getKey();
				NodeInfo current = (NodeInfo) removalNode;
				current.weights.remove(key, getEdge(key, keyCurrent));// remove the edge
				current.ni.remove(key, removalNode);// remove the removal node from the neighbor list
			}
			this.edges -= getV(key).size();
			this.MC++;
			this.nodeSize--;
			this.graph.remove(key, removalNode);// delete the node from the graph
		}
		return removalNode;
	}

	/**
	 * the method remove the edge between node1 and node2
	 * 
	 * @param node1
	 * @param node2
	 */
	@Override
	public void removeEdge(int node1, int node2) {
		if (hasEdge(node1, node2)) {
			NodeInfo n1 = (NodeInfo) (getNode(node1));
			NodeInfo n2 = (NodeInfo) (getNode(node2));
			n1.weights.remove(node2, n2);// remove weight
			n2.weights.remove(node1, n1);
			n1.ni.remove(node2);// remove node1 from neighbor list of node2
			n2.ni.remove(node1);
			this.MC++;
			this.edges--;
		}
	}

	/**
	 * the method return the num of the nodes in the graph
	 * 
	 * @return graph size
	 */
	@Override
	public int nodeSize() {
		return this.nodeSize;
	}

	/**
	 * the method return num of edges in the graph
	 * 
	 * @return num of edges
	 */
	@Override
	public int edgeSize() {
		return this.edges;
	}

	/**
	 * the method return num of changing on the graph
	 * 
	 * @return MC
	 */
	@Override
	public int getMC() {
		return this.MC;
	}

	/**
	 * the method return the info about the graph without MC print for check the
	 * function because the copy method initialize the MC
	 * 
	 * @return result
	 */
	public String toString() {
		String result = "The Graph have: " + nodeSize + " nodes, " + edges + " edges";
		Iterator<node_info> it = getV().iterator();
		while (it.hasNext()) {
			node_info currentNode = it.next();
			result += "\n" + currentNode.toString() + "\n";
			Iterator<node_info> itNi = getV(currentNode.getKey()).iterator();
			while (itNi.hasNext()) {
				node_info current = itNi.next();
				result += "neighbor is: ";
				result += Integer.toString(current.getKey());
				result += ". the edge size between this node to neighbor node is: ";
				result += Double.toString(getEdge(currentNode.getKey(), current.getKey()));
				result += "\n";
			}

		}
		return result;
	}

	/**
	 * this method check if object ot is equal to this.ga
	 * 
	 * @param ot
	 * @return true if equal else return false
	 */
	public boolean equals(Object ot) {
		if (ot == null || !(ot instanceof WGraph_DS)) {
			return false;
		}
		Iterator<node_info> it = ((WGraph_DS) ot).getV().iterator();
		if (((WGraph_DS) ot).edgeSize() != this.edgeSize() || ((WGraph_DS) ot).nodeSize() != this.nodeSize)
			return false;
		while (it.hasNext()) {// check if the graph structures are equal
			if (!containsNode(it.next()))
				return false;
		}
		return true;
	}

	/**
	 * this method cheack if ot.graph is equal to this.ga.graph
	 * 
	 * @param n
	 * @return
	 */
	public boolean containsNode(node_info n) {
		Iterator<node_info> it = this.getV().iterator();
		while (it.hasNext()) {
			if (it.next().getKey() == n.getKey())
				return true;//stop if equal
		}
		return false;
	}

	/**
	 * represents the set of operations applicable on a node (vertex) in an
	 * (undirectional) weighted graph.
	 */
	private class NodeInfo implements node_info, Serializable {
		private int key;
		private double tag;
		private String info;
		private HashMap<Integer, Double> weights;//structure represents the weights
		private HashMap<Integer, node_info> ni;// represents all the neighbors of node

		public NodeInfo(int key) {// initialize fields
			this.key = key;
			this.weights = new HashMap<Integer, Double>();
			this.ni = new HashMap<Integer, node_info>();
		}


		@Override
		public int getKey() {
			return this.key;
		}

		@Override
		public String getInfo() {
			return this.info;
		}

		@Override
		public void setInfo(String s) {
			this.info = s;
		}

		@Override
		public double getTag() {
			return this.tag;
		}

		@Override
		public void setTag(double t) {
			this.tag = t;
		}

		public String toString() {
			return "node's key is: " + getKey() + ". tag is: " + getTag() + ". info is: " + getInfo() + ". ";
		}
	}
}