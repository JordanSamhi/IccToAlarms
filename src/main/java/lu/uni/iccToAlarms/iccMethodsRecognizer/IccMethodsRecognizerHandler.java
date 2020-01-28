package lu.uni.iccToAlarms.iccMethodsRecognizer;

import java.util.List;

import lu.uni.iccToAlarms.factories.AlarmManagerMethodsFactory;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;

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
	
	protected abstract List<Unit> recognize(Body b, SootMethod sm, InvokeStmt stmt);
}
