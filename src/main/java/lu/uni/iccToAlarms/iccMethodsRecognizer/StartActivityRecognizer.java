package lu.uni.iccToAlarms.iccMethodsRecognizer;

import java.util.ArrayList;
import java.util.List;

import lu.uni.iccToAlarms.factories.AlarmManagerMethodsFactory;
import lu.uni.iccToAlarms.utils.Constants;
import soot.Body;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;

public class StartActivityRecognizer extends IccMethodsRecognizerHandler {

	public StartActivityRecognizer(IccMethodsRecognizerHandler next) {
		super(next);
	}

	@Override
	protected List<Unit> recognize(Body b, SootMethod sm, InvokeStmt stmt) {
		List<Unit> unitsToAdd = null;
		if(sm.getSignature().equals(Constants.STARTACTIVITY)) {
			unitsToAdd = this.ammf.generateGetActivity(b, stmt);
		}
		return unitsToAdd;
	}
}
