/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2015 Thomas Pointhuber
 */

package compiler;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

import at.jku.ssw.cmm.compiler.Node;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Parser;
import at.jku.ssw.cmm.compiler.Scanner;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.compiler.Tab;


public class TabTest {

	/**
	 * create a Tab Obj and fill-them with a defined set of sourceCode
	 * 
	 * @param sourceCode string with sourcecode
	 * @return new Tab Obj
	 */
	public Tab createTabObj(String sourceCode) {
		InputStream is = new ByteArrayInputStream(sourceCode.getBytes());
		Scanner scanner = new Scanner(is);
		Parser parser = new Parser(scanner);
		
		// create some tokens to bypass NullPtrException when calling SemErr
		parser.t = scanner.Scan();
		parser.la = scanner.Scan();
		
		Tab tab = new Tab(parser);
		return tab;
	}
	
	@Test
	public void testOpenScope() {
		Tab tab = createTabObj("");

		assertEquals( tab.curLevel, -1);
		assertNotNull(tab.curScope);
		assertNull(tab.curScope.outer);
		
		tab.openScope();
		assertNotNull(tab.curScope);
		assertEquals( tab.curLevel, 0);
	}

	@Test
	public void testCloseScope() {
		Tab tab = createTabObj("");

		assertEquals( tab.curLevel, -1);

		tab.openScope();
		tab.closeScope();

		assertEquals( tab.curLevel, -1);
	}

	@Test
	public void testInsert() {
		Tab tab = createTabObj("");

		// test insert of a procedure
		Obj testProc = tab.insert(Obj.PROC, "testProc", Tab.noType, -1);

		assertEquals(testProc.kind, Obj.PROC);
		assertEquals(testProc.name, "testProc");
		assertEquals(testProc.line, -1);
		assertEquals(testProc.type.kind, Struct.NONE);

		assertEquals(tab.find("testProc"), testProc);
		
		// test insert of a variable
		Obj testVar = tab.insert(Obj.VAR, "testVar", Tab.intType, -1);

		assertEquals(testVar.kind, Obj.VAR);
		assertEquals(testVar.name, "testVar");
		assertEquals(testVar.type.kind, Struct.INT);
		
		assertEquals(tab.find("testVar"), testVar);
		
		// test insert of a type
		Obj testType = tab.insert(Obj.TYPE, "testType", Tab.floatType, -1);

		assertEquals(testType.kind, Obj.TYPE);
		assertEquals(testType.name, "testType");
		assertEquals(testType.type.kind, Struct.FLOAT);
		
		assertEquals(tab.find("testType"), testType);
		
		// test insert of a con
		Obj testCon = tab.insert(Obj.CON, "testCon", Tab.boolType, -1);

		assertEquals(testCon.kind, Obj.CON);
		assertEquals(testCon.name, "testCon");
		assertEquals(testCon.type.kind, Struct.BOOL);
		
		assertEquals(tab.find("testCon"), testCon);
	}

	@Test
	public void testFind() {
		Tab tab = createTabObj("");

		Obj testProc = tab.insert(Obj.PROC, "testProc", Tab.noType, -1);

		assertEquals(tab.find("testProc"), testProc);
		assertEquals(tab.find("invalidProc"), Tab.noObj);
		// TODO: Check for error-message
		
		tab.openScope();
		
		Obj testProc2 = tab.insert(Obj.PROC, "testProc2", Tab.noType, -1);

		assertEquals(tab.find("testProc"), testProc);
		assertEquals(tab.find("testProc2"), testProc2);
		
		tab.closeScope();
		
		assertEquals(tab.find("testProc2"), Tab.noObj);
		assertEquals(tab.find(null), Tab.noObj);
		// TODO: Check for error-message
	}

	@Test
	public void testFindField() {
		Tab tab = createTabObj("");
		
		Struct type = new Struct(Struct.STRUCT);
		
		tab.insert(Obj.TYPE, "testStruct", type, -1);
		
		tab.openScope();
		
		// create struct-vars
		Obj testVarInt = tab.insert(Obj.VAR, "testVarInt", Tab.intType, -1);
		Obj testVarFloat = tab.insert(Obj.VAR, "testVarFloat", Tab.floatType, -1);
		Obj testVarBool = tab.insert(Obj.VAR, "testVarBool", Tab.boolType, -1);
		
		// copy variables from local scope int struct
		type.fields = tab.curScope.locals;
        // copy size
        type.size = tab.curScope.size;
		
        tab.closeScope();
        
        // check if funcion find fields
        assertEquals(tab.findField("testVarInt", type), testVarInt);
        assertEquals(tab.findField("testVarFloat", type), testVarFloat);
        assertEquals(tab.findField("testVarBool", type), testVarBool);
        
        // check if funcion is working properly with incorrect types
        assertEquals(tab.findField("testVarNotDefined", type), Tab.noObj);
        assertEquals(tab.findField(null, type), Tab.noObj);
        assertEquals(tab.findField("testVarNotDefined", null), Tab.noObj);
        assertEquals(tab.findField(null, null), Tab.noObj);
        // TODO: Check for error-message
	}

	@Test
	public void testLookup() {
		Tab tab = createTabObj("");

		tab.insert(Obj.VAR, "outerScopeTestVar", Tab.intType, -1);
		
		// create new Scope
		tab.openScope();
		
		Obj innnerScopeTestVar = tab.insert(Obj.VAR, "innnerScopeTestVar", Tab.floatType, -1);

		assertEquals(tab.lookup("innnerScopeTestVar"), innnerScopeTestVar);
		assertEquals(tab.lookup("outerScopeTestVar"), Tab.noObj);
		
		tab.closeScope();
		
		// check for Null-Ptr-Exceptions
		assertEquals(tab.lookup(null), Tab.noObj);
		// TODO: detect SemErr
	}

	@Test
	public void testLookupType() {
		Tab tab = createTabObj("");

		Struct outerScopeTestType = tab.insert(Obj.TYPE, "outerScopeTestType", new Struct(Struct.INT), -1).type;
		
		// create new Scope
		tab.openScope();
		
		Struct innnerScopeTestType = tab.insert(Obj.TYPE, "innnerScopeTestType", new Struct(Struct.STRUCT), -1).type;
		
		assertEquals(tab.lookupType(innnerScopeTestType), true);
		assertEquals(tab.lookupType(outerScopeTestType), false);
		
		// check for Null-Ptr-Exceptions
		assertEquals(tab.lookupType(null), false);
		// TODO: detect SemErr
	}

	@Ignore
	@Test
	public void testCheckForwardParams() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testCheckIfForwardsResolved() {
		fail("Not yet implemented");
	}

	@Test
	public void testIntVal() {
		Tab tab = createTabObj("");
		
		// test problematic values
		assertEquals(tab.intVal("0"),0);
		assertEquals(tab.intVal("-2147483648"),-2147483648);
		assertEquals(tab.intVal("2147483647"),2147483647);
		
		// test normal values
		assertEquals(tab.intVal("10"),10);
		assertEquals(tab.intVal("-256870"),-256870);
		
		// test wrong values
		assertEquals(tab.intVal("\"asdf\""),0);
		assertEquals(tab.intVal("2147483648"),0);
		assertEquals(tab.intVal("10.5"),0);
		assertEquals(tab.intVal("'a'"),0);
		assertEquals(tab.intVal(null),0);
		// TODO: Check for error-message
	}

	@Test
	public void testFloatVal() {
		Tab tab = createTabObj("");
		
		// test problematic values
		assertEquals(tab.floatVal("0.0"),0.0, 1e-4);
		assertEquals(tab.floatVal("1234.5E0"),1234.5E0, 1e-4);
		assertEquals(tab.floatVal("1.0E+1"),1.0E+1, 1e-4);
		assertEquals(tab.floatVal("1.0E-1"),1.0E-1, 1e-4);
		
		// test normal values
		assertEquals(tab.floatVal("0"),0, 1e-4);
		assertEquals(tab.floatVal("12E5"),12E5, 1e-4);
		assertEquals(tab.floatVal("10.15"),10.15, 1e-4);
		assertEquals(tab.floatVal("20.15e-5"),20.15e-15, 1e-2);
		assertEquals(tab.floatVal("-33.2E5"),-33.2E5, 1e-4);
		
		// test wrong values
		assertEquals(tab.floatVal("test"),0, 1e-4);
		assertEquals(tab.floatVal("a"),0, 1e-4);
		assertEquals(tab.floatVal("E"),0, 1e-4);
		assertEquals(tab.floatVal(null),0, 1e-4);
		// TODO: Check for error-message
	}

	@Test
	public void testCharVal() {
		Tab tab = createTabObj("");
		
		// test problematic values
		assertEquals(tab.charVal("' '"),' ');
		assertEquals(tab.charVal("'\\n'"),'\n');
		assertEquals(tab.charVal("'\\r'"),'\r');
		assertEquals(tab.charVal("'\\t'"),'\t');
		assertEquals(tab.charVal("'\\0'"),'\0');
		assertEquals(tab.charVal("'\\''"),'\'');
		assertEquals(tab.charVal("'\\\\'"),'\\');
		
		// test normal values
		assertEquals(tab.charVal("'a'"),'a');
		assertEquals(tab.charVal("'%'"),'%');
		assertEquals(tab.charVal("'5'"),'5');
		
		// test wrong values
		assertEquals(tab.charVal(""),'\0');
		assertEquals(tab.charVal("\\"),'\0');
		assertEquals(tab.charVal("''"),'\0');
		assertEquals(tab.charVal("'ab'"),'\0');
		assertEquals(tab.charVal("abc"),'\0');
		assertEquals(tab.charVal("c"),'\0');
		assertEquals(tab.charVal("\"string\""),'\0');
		assertEquals(tab.charVal("'\\%'"),'\0');
		assertEquals(tab.charVal(null),'\0');
		// TODO: Check for error-message
	}

	@Test
	public void testStringVal() {
		Tab tab = createTabObj("");
		
		// test problematic values
		assertEquals(tab.stringVal("\"\""),"");
		assertEquals(tab.stringVal("\" \"")," ");
		assertEquals(tab.stringVal("\"\\r\""),"\r");
		assertEquals(tab.stringVal("\"\\n\""),"\n");
		assertEquals(tab.stringVal("\"\\t\""),"\t");
		assertEquals(tab.stringVal("\"\\0\""),"\0");
		assertEquals(tab.stringVal("\"\\\"\""),"\"");
		assertEquals(tab.stringVal("\"\\\\\""),"\\");
		
		// test normal values
		assertEquals(tab.stringVal("\"test string\""),"test string");
		assertEquals(tab.stringVal("\"asdf\""),"asdf");
		assertEquals(tab.stringVal("\"line \\n newline\""),"line \n newline");
		
		// test wrong values
		assertEquals(tab.stringVal(""),"");
		assertEquals(tab.stringVal("\""),"");
		assertEquals(tab.stringVal("asdf"),"");
		assertEquals(tab.stringVal("'asdf'"),"");
		assertEquals(tab.stringVal("'b'"),"");
		assertEquals(tab.stringVal("\"\\s\""),"");
		assertEquals(tab.stringVal(null),"");
		// TODO: Check for error-message
	}

	@Test
	public void testIsCastOperator() {
		Tab tab = createTabObj("");
		
		// test correct cast-operators
		assertEquals(tab.isCastOperator(Node.A2S),true);
		assertEquals(tab.isCastOperator(Node.B2I),true);
		assertEquals(tab.isCastOperator(Node.C2I),true);
		assertEquals(tab.isCastOperator(Node.C2S),true);
		assertEquals(tab.isCastOperator(Node.I2F),true);
		assertEquals(tab.isCastOperator(Node.F2I),true);
		
		// test incorrect cast-operators
		assertEquals(tab.isCastOperator(Node.IDENT),false);
		assertEquals(tab.isCastOperator(Node.INDEX),false);
		assertEquals(tab.isCastOperator(Node.CALL),false);
	}

	@Test
	public void testCheckFunctionParams() {
		Tab tab = createTabObj("");

		// TODO: better tests
		
		// check for Null-Ptr-Exceptions
		tab.checkFunctionParams(tab.find("print"), null);
		tab.checkFunctionParams(null, new Node(tab.find("print")));
		tab.checkFunctionParams(null, null);
		// TODO: detect SemErr
	}

	@Test
	public void testImpliciteTypeCon() {
		Tab tab = createTabObj("");
		
		// test impliciteTypeCon if no type-change occour
		Node intNode = new Node(3);
		assertEquals(tab.impliciteTypeCon(intNode, Tab.intType),intNode);
		
		Node charNode = new Node('c');
		assertEquals(tab.impliciteTypeCon(charNode, Tab.charType),charNode);

		// test impliciteTypeCon if type-change occour
		assertEquals(tab.impliciteTypeCon(intNode, Tab.floatType).kind,Node.I2F);
		assertEquals(tab.impliciteTypeCon(intNode, Tab.floatType).left,intNode);

		assertEquals(tab.impliciteTypeCon(intNode, Tab.boolType).kind,Node.I2B);
		assertEquals(tab.impliciteTypeCon(intNode, Tab.boolType).left,intNode);
		
		assertEquals(tab.impliciteTypeCon(charNode, Tab.intType).kind,Node.C2I);
		assertEquals(tab.impliciteTypeCon(charNode, Tab.intType).left,charNode);
		
		// check for Null-Ptr-Exceptions
		tab.impliciteTypeCon(new Node(5), null);
		tab.impliciteTypeCon(null, Tab.intType);
		tab.impliciteTypeCon(null, null);
		// TODO: detect SemErr
	}

	@Test
	public void testExpliciteTypeCon() {
		Tab tab = createTabObj("");
		
		// test expliciteTypeCon if no type-change occour
		Node intNode = new Node(3);
		assertEquals(tab.expliciteTypeCon(intNode, Tab.intType),intNode);
				
		Node charNode = new Node('c');
		assertEquals(tab.expliciteTypeCon(charNode, Tab.charType),charNode);
		
		// test expliciteTypeCon if type-change occour
		assertEquals(tab.impliciteTypeCon(intNode, Tab.floatType).kind,Node.I2F);
		assertEquals(tab.impliciteTypeCon(intNode, Tab.floatType).left,intNode);
		
		assertEquals(tab.expliciteTypeCon(intNode, Tab.charType).kind,Node.I2C);
		assertEquals(tab.expliciteTypeCon(intNode, Tab.charType).left,intNode);
		
		// check for Null-Ptr-Exceptions
		tab.expliciteTypeCon(new Node('a'), null);
		tab.expliciteTypeCon(null, Tab.boolType);
		tab.expliciteTypeCon(null, null);
		// TODO: detect SemErr
	}

	@Test
	public void testDoImplicitCastByAritmetic() {
		Tab tab = createTabObj("");
		
		// TODO finish test
		// test doImplicitCastByAritmetic if no type-change occour
		Node intNode = new Node(3);
		assertEquals(tab.doImplicitCastByAritmetic(intNode, Tab.intType, Tab.intType),intNode);
				
		// test doImplicitCastByAritmetic if type-change occour
		Node charNode = new Node('c');
		assertEquals(tab.doImplicitCastByAritmetic(charNode, Tab.charType, Tab.intType).kind,Node.C2I);

		// check for Null-Ptr-Exceptions
		tab.doImplicitCastByAritmetic(null, Tab.intType, Tab.floatType);
		tab.doImplicitCastByAritmetic(null, Tab.boolType, Tab.intType);
		tab.doImplicitCastByAritmetic(new Node(5), Tab.intType, null);
		tab.doImplicitCastByAritmetic(new Node(5), null, Tab.intType);
		tab.doImplicitCastByAritmetic(null, null, null);
		// TODO: detect SemErr
	}

	@Test
	public void testGetNameOfType() {
		Tab tab = createTabObj("");
		
		// test getNameOfType with normal types
		assertEquals(tab.getNameOfType(Tab.noType), "void");
		assertEquals(tab.getNameOfType(Tab.intType), "int");
		assertEquals(tab.getNameOfType(Tab.floatType), "float");
		assertEquals(tab.getNameOfType(new Struct(Struct.STRUCT)), "struct");
		
		// check for Null-Ptr-Exceptions
		assertEquals(tab.getNameOfType(null), "null");
	}

	@Ignore
	@Test
	public void testDumpStruct() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testDumpObj() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testDumpScope() {
		fail("Not yet implemented");
	}

	@Test
	public void testTab() {
		Tab tab = createTabObj("");
		
		// check if default-procedures exist
		assertEquals(tab.find("print"), Tab.printProc);
		assertEquals(tab.find("read").kind, Obj.PROC);
		assertEquals(tab.find("print").type.kind, Struct.NONE);
		
		assertEquals(tab.find("read"), Tab.readProc);
		assertEquals(tab.find("read").kind, Obj.PROC);
		assertEquals(tab.find("read").type.kind, Struct.CHAR);
		
		// check if default-types exist
		assertEquals(tab.find("bool").type, Tab.boolType);
		assertEquals(tab.find("bool").kind, Obj.TYPE);
		assertEquals(tab.find("bool").type.kind, Struct.BOOL);
		
		assertEquals(tab.find("int").type, Tab.intType);
		assertEquals(tab.find("int").kind, Obj.TYPE);
		assertEquals(tab.find("int").type.kind, Struct.INT);
		
		assertEquals(tab.find("float").type, Tab.floatType);
		assertEquals(tab.find("float").kind, Obj.TYPE);
		assertEquals(tab.find("float").type.kind, Struct.FLOAT);
		
		assertEquals(tab.find("string").type, Tab.stringType);
		assertEquals(tab.find("string").kind, Obj.TYPE);
		assertEquals(tab.find("string").type.kind, Struct.STRING);
		
		// check if default-constants exist
		assertEquals(tab.find("true").type, Tab.boolType);
		assertEquals(tab.find("true").kind, Obj.CON);
		assertNotEquals(tab.find("true").val, 0);
		
		assertEquals(tab.find("false").type, Tab.boolType);
		assertEquals(tab.find("false").kind, Obj.CON);
		assertEquals(tab.find("false").val, 0);
	}

}
