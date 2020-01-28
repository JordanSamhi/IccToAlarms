package lu.uni.IccToAlarms.iccMethodsRecognizer;

import java.util.List;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;

public abstract class IccMethodsRecognizerHandler implements IccMethodsRecognizer {
	
	private IccMethodsRecognizerHandler next;
	
	public IccMethodsRecognizerHandler(IccMethodsRecognizerHandler next) {
		this.next = next;
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
	
	protected Local addLocal(Body b, Type t) {
		Local l = Jimple.v().newLocal("l", t);
		b.getLocals().add(l);
		return l;
	}
	
	protected SootMethodRef getMethodRef(String className, String methodName) {
		return Scene.v().getSootClass(className).getMethod(methodName).makeRef();
	}
}
