package lu.uni.iccToAlarms.iccMethodsRecognizer;

import java.util.List;

import lu.uni.iccToAlarms.utils.Constants;
import soot.Body;
import soot.Unit;
import soot.jimple.Stmt;

public class StartServiceRecognizer extends IccMethodsRecognizerHandler {

	public StartServiceRecognizer(IccMethodsRecognizerHandler next) {
		super(next);
	}

	@Override
	protected String getTypeRecognized() {
		return Constants.STARTSERVICE;
	}

	@Override
	protected List<Unit> generateUnits(Body b, Stmt stmt) {
		return this.ammf.generateGetService(b, stmt);
	}
}
