package at.jku.ssw.cmm.compiler;

/*--------------------------------------------------------------------------------
NodeList   Builds lists of AST nodes
========   =========================
--------------------------------------------------------------------------------*/

public class NodeList {
	Node head, tail;

	// Append x to the list
	public void add(Node x) {
		if (x != null) {
			if (head == null) head = x; else tail.next = x;
			tail = x;
		}
	}

	// Retrieve the head of the list
	public Node get() {
		return head;
	}
}