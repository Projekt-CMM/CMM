package compiler;

import at.jku.ssw.cmm.compiler.Node;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.compiler.Tab;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class NodeTest {

	@Test
	public void testNodeIntNodeNodeStruct() {
		// setup required variables
		Struct intStruct = new Struct(Struct.INT);
		Node intNode = new Node(10);
		Node int2Node = new Node(20);
		
		// create Node and test results
		Node testNode = new Node(Node.PLUS, intNode, int2Node, intStruct);
		assertEquals(testNode.kind, Node.PLUS);
		assertEquals(testNode.left, intNode);
		assertEquals(testNode.right, int2Node);
		assertEquals(testNode.type, intStruct);
	}

	@Test
	public void testNodeIntNodeNodeInt() {
		// setup required variables
		Node intNode = new Node(10);
		Node int2Node = new Node(20);
				
		// create Node and test results
		Node testNode = new Node(Node.PLUS, intNode, int2Node, 15);
		assertEquals(testNode.kind, Node.PLUS);
		assertEquals(testNode.left, intNode);
		assertEquals(testNode.right, int2Node);
		assertEquals(testNode.type, null);
		assertEquals(testNode.line, 15);
	}

	@Test
	public void testNodeIntNodeNodeIntIntInt() {
		// setup required variables
		Node intNode = new Node(10);
		Node int2Node = new Node(20);
						
		// create Node and test results
		Node testNode = new Node(Node.MINUS, intNode, int2Node, 15, 33, 19);
		assertEquals(testNode.kind, Node.MINUS);
		assertEquals(testNode.left, intNode);
		assertEquals(testNode.right, int2Node);
		assertEquals(testNode.type, null);
		assertEquals(testNode.line, 15);
		assertEquals(testNode.col, 33);
		assertEquals(testNode.colLength, 19);
	}

	@Test
	public void testNodeObj() {
		// setup required variables
		Struct intStruct = new Struct(Struct.INT);
		Obj newObj = new Obj(Obj.CON, "testObj", intStruct, 1);
		
		// create Node and test results
		Node testNode = new Node(newObj);
		assertEquals(testNode.kind, Node.IDENT);
		assertEquals(testNode.left, null);
		assertEquals(testNode.right, null);
		assertEquals(testNode.type, intStruct);
		assertEquals(testNode.obj, newObj);
	}

	@Test
	public void testNodeObjIntIntInt() {
		// setup required variables
		Struct intStruct = new Struct(Struct.INT);
		Obj newObj = new Obj(Obj.CON, "testObj", intStruct, 1);
		
		// create Node and test results
		Node testNode = new Node(newObj, 33, 12, 5);
		assertEquals(testNode.kind, Node.IDENT);
		assertEquals(testNode.left, null);
		assertEquals(testNode.right, null);
		assertEquals(testNode.type, intStruct);
		assertEquals(testNode.obj, newObj);
		assertEquals(testNode.line, 33);
		assertEquals(testNode.col, 12);
		assertEquals(testNode.colLength, 5);
	}

	@Test
	public void testNodeBoolean() {
		Node trueNode = new Node(true);
		assertEquals(trueNode.kind, Node.BOOLCON);
		assertEquals(trueNode.left, null);
		assertEquals(trueNode.right, null);
		assertEquals(trueNode.type, Tab.boolType);
		assertEquals(trueNode.val, 1);
		assertEquals(trueNode.fVal, 0, 0.001);
		
		Node falseNode = new Node(false);
		assertEquals(falseNode.kind, Node.BOOLCON);
		assertEquals(falseNode.left, null);
		assertEquals(falseNode.right, null);
		assertEquals(falseNode.type, Tab.boolType);
		assertEquals(falseNode.val, 0);
		assertEquals(trueNode.fVal, 0, 0.001);
	}

	@Test
	public void testNodeInt() {
		Node intNode = new Node(42);
		assertEquals(intNode.kind, Node.INTCON);
		assertEquals(intNode.left, null);
		assertEquals(intNode.right, null);
		assertEquals(intNode.type, Tab.intType);
		assertEquals(intNode.val, 42);
		assertEquals(intNode.fVal, 0, 0.001);
	}

	@Test
	public void testNodeIntIntIntInt() {
		Node intNode = new Node(96, 13, 22, 2);
		assertEquals(intNode.kind, Node.INTCON);
		assertEquals(intNode.left, null);
		assertEquals(intNode.right, null);
		assertEquals(intNode.type, Tab.intType);
		assertEquals(intNode.val, 96);
		assertEquals(intNode.fVal, 0, 0.001);
		assertEquals(intNode.line, 13);
		assertEquals(intNode.col, 22);
		assertEquals(intNode.colLength, 2);
	}

	@Test
	public void testNodeFloat() {
		Node floatNode = new Node(123.45f);
		assertEquals(floatNode.kind, Node.FLOATCON);
		assertEquals(floatNode.left, null);
		assertEquals(floatNode.right, null);
		assertEquals(floatNode.type, Tab.floatType);
		assertEquals(floatNode.fVal, 123.45f, 0.01);
		assertEquals(floatNode.val, 0);
	}

	@Test
	public void testNodeChar() {
		Node charNode = new Node('d');
		assertEquals(charNode.kind, Node.CHARCON);
		assertEquals(charNode.left, null);
		assertEquals(charNode.right, null);
		assertEquals(charNode.type, Tab.charType);
		assertEquals(charNode.val, 'd');
		assertEquals(charNode.fVal, 0, 0.001);
	}

	@Test
	public void testNodeString() {
		Node stringNode = new Node("asdf");
		assertEquals(stringNode.kind, Node.STRINGCON);
		assertEquals(stringNode.left, null);
		assertEquals(stringNode.right, null);
		assertEquals(stringNode.type, Tab.stringType);
	}
	
	@Test
	@Ignore
	public void testCountNodes() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testDump() {
		fail("Not yet implemented");
	}

}
