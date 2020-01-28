package lu.uni.IccToAlarms.iccMethodsRecognizer;

import java.util.List;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;

public interface IccMethodsRecognizer {
	public List<Unit> recognizeIccMethod(Body b, SootMethod sm, InvokeStmt stmt);
}
