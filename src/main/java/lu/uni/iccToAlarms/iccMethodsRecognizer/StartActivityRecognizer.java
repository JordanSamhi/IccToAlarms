package lu.uni.iccToAlarms.iccMethodsRecognizer;

import java.util.List;

import lu.uni.iccToAlarms.utils.Constants;
import soot.Body;
import soot.Unit;
import soot.jimple.Stmt;

public class StartActivityRecognizer extends IccMethodsRecognizerHandler {

	public StartActivityRecognizer(IccMethodsRecognizerHandler next) {
		super(next);
	}

	@Override
	protected String getTypeRecognized() {
		return Constants.STARTACTIVITY;
	}

	@Override
	protected List<Unit> generateUnits(Body b, Stmt stmt) {
		return this.ammf.generateGetActivity(b, stmt);
	}
}
