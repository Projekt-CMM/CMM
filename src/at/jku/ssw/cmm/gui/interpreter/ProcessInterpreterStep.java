package at.jku.ssw.cmm.gui.interpreter;

import at.jku.ssw.cmm.gui.GUIrightPanel;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.compiler.Node;

/**
 * UNUSED
 * 
 * @author fabian
 *
 */
public class ProcessInterpreterStep {

	public ProcessInterpreterStep(GUImainMod modMain, GUIrightPanel master) {
		this.modMain = modMain;
		this.master = master;
	}

	@SuppressWarnings("unused")
	private final GUImainMod modMain;
	private final GUIrightPanel master;

	public void interpret(Node n) {

		this.master.selectFunction();
		this.master.globalSelectRoot();

		this.master.updateGlobals();
		this.master.updateCallStack();
		this.master.updateLocals();

		switch (n.kind) {
		case Node.ASSIGN:
			// Call with assignment, for example | i = add( i, 1 );
			if (n.right.kind == Node.CALL) {
				// Calling a function from include file -> step over by default
				if (n.right.obj.ast.line <= this.master.getBeginLine())
					this.master.setStepOverButtonAlone();
				// Calling a local function -> show step over button
				else
					this.master.setStepOverButton();
			}
			break;
		case Node.CALL:
			// Calling a function from include file -> step over by default
			if (n.obj.ast.line <= this.master.getBeginLine())
				this.master.setStepOverButtonAlone();
			// Calling a local function -> show step over button
			else
				this.master.setStepOverButton();
			break;
		case Node.IF:
			break;
		case Node.WHILE:
			break;
		case Node.LSS:
			break;
		case Node.GTR:
			break;
		case Node.RETURN:
			break;
		case Node.TRAP:
			break;
		case -1:
			break;
		default:
			break;
		}
	}

}
