package lu.uni.iccToAlarms.iccMethodsRecognizer;

import java.util.List;

import lu.uni.iccToAlarms.utils.Constants;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;

public class StartActivityRecognizer extends IccMethodsRecognizerHandler {

	public StartActivityRecognizer(IccMethodsRecognizerHandler next) {
		super(next);
	}

	@Override
	protected List<Unit> recognize(Body b, SootMethod sm, InvokeStmt stmt) {
		List<Unit> unitsToAdd = null;
		if(sm.getSignature().equals(Constants.STARTACTIVITY)) {
			unitsToAdd = this.ammf.generateGetBroadcast(b, stmt);
		}
		return unitsToAdd;
	}
}