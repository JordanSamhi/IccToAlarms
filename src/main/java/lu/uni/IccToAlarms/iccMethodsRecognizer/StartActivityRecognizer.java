package lu.uni.IccToAlarms.iccMethodsRecognizer;

import java.util.ArrayList;
import java.util.List;

import lu.uni.IccToAlarms.utils.Constants;
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
			unitsToAdd = new ArrayList<Unit>();
			Local pi = this.addLocal(b, RefType.v(Constants.ANDROID_APP_PENDINGINTENT));
			Local obj = this.addLocal(b, RefType.v(Constants.JAVA_LANG_OBJECT));
			Local am = this.addLocal(b, RefType.v(Constants.ANDROID_APP_ALARMMANAGER));
			Local l = this.addLocal(b, LongType.v());
			Local thiss = b.getThisLocal();

			// pi = PendingIntent.getActivity(this, 0, intent, 0)
			SootMethodRef getActivity = this.getMethodRef(Constants.ANDROID_APP_PENDINGINTENT, Constants.GETACTIVITY); 
			Value intent = stmt.getInvokeExpr().getArg(0);
			unitsToAdd.add(Jimple.v().newAssignStmt(pi, 
					Jimple.v().newStaticInvokeExpr(getActivity, 
							thiss, IntConstant.v(0), intent, IntConstant.v(0))));

			// obj = getSystenService("alarm")
			SootMethodRef getSystemService = this.getMethodRef(Constants.ANDROID_CONTENT_CONTEXT, Constants.GETSYSTEMSERVICE);
			unitsToAdd.add(Jimple.v().newAssignStmt(obj,
					Jimple.v().newVirtualInvokeExpr(thiss, getSystemService,
							StringConstant.v(Constants.ALARM))));

			//am = (AlarmManager) obj;
			unitsToAdd.add(Jimple.v().newAssignStmt(am,
					Jimple.v().newCastExpr(obj, RefType.v(Constants.ANDROID_APP_ALARMMANAGER))));

			// l = currentTimeMillis()
			SootMethodRef currentTimeMillis = this.getMethodRef(Constants.JAVA_LANG_SYSTEM, Constants.CURRENTTIMEMILLIS);
			unitsToAdd.add(
					Jimple.v().newAssignStmt(l,
							Jimple.v().newStaticInvokeExpr(currentTimeMillis)));

			// l = l - 1;
			unitsToAdd.add(Jimple.v().newAssignStmt(l,
					Jimple.v().newSubExpr(l, LongConstant.v(10000))));

			// am.set()
			SootMethodRef set = this.getMethodRef(Constants.ANDROID_APP_ALARMMANAGER, Constants.SET);
			unitsToAdd.add(
					Jimple.v().newInvokeStmt(
							Jimple.v().newVirtualInvokeExpr(am, set, 
									IntConstant.v(0), l, pi)));
		}
		return unitsToAdd;
	}
}
