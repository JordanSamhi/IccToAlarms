package lu.uni.iccToAlarms.iccMethodsRecognizer;

import java.util.List;

import lu.uni.iccToAlarms.factories.AlarmManagerMethodsFactory;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;

public abstract class IccMethodsRecognizerHandler implements IccMethodsRecognizer {
	
	protected IccMethodsRecognizerHandler next;
	AlarmManagerMethodsFactory ammf = null;
	
	public IccMethodsRecognizerHandler(IccMethodsRecognizerHandler next) {
		this.next = next;
		this.ammf = new AlarmManagerMethodsFactory();
	}

	@Override
	public List<Unit> recognizeIccMethod(Body b, SootMethod sm, InvokeStmt stmt) {
		List<Unit> units = this.recognize(b, sm, stmt);
		if(units != null) {
			return units;
		}else if(this.next != null) {
			return this.next.recognizeIccMethod(b, sm, stmt);
		}else {
			return null;
		}
	}
	
	private List<Unit> recognize(Body b, SootMethod sm, InvokeStmt stmt){
		if(sm.getSignature().equals(this.getTypeRecognized())) {
			return this.generateUnits(b, stmt);
		}
		return null;
	}
	
	protected abstract String getTypeRecognized();
	protected abstract List<Unit> generateUnits(Body b, Stmt stmt);
}