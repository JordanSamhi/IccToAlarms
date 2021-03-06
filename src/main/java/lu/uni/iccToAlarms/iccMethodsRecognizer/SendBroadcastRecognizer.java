package lu.uni.iccToAlarms.iccMethodsRecognizer;

import java.util.List;

import lu.uni.iccToAlarms.utils.Constants;
import soot.Body;
import soot.Unit;
import soot.jimple.Stmt;

public class SendBroadcastRecognizer extends IccMethodsRecognizerHandler {

	public SendBroadcastRecognizer(IccMethodsRecognizerHandler next) {
		super(next);
	}

	@Override
	protected String getTypeRecognized() {
		return Constants.SENDBROADCAST;
	}

	@Override
	protected List<Unit> generateUnits(Body b, Stmt stmt) {
		return this.ammf.generateGetBroadcast(b, stmt);
	}
}
