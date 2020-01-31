package lu.uni.iccToAlarms.factories;

import java.util.ArrayList;
import java.util.List;

import lu.uni.iccToAlarms.utils.Constants;
import soot.Body;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;

public class AlarmManagerMethodsFactory {
	
	private List<Unit> generateGenericBody(Body b, Stmt stmt, String alarmMethodSignature){
		List<Unit> unitsToAdd = new ArrayList<Unit>();
		Local pi = this.addLocal(b, RefType.v(Constants.ANDROID_APP_PENDINGINTENT));
		Local obj = this.addLocal(b, RefType.v(Constants.JAVA_LANG_OBJECT));
		Local am = this.addLocal(b, RefType.v(Constants.ANDROID_APP_ALARMMANAGER));
		Local l = this.addLocal(b, LongType.v());
		Local thisLocal = b.getThisLocal();

		SootMethodRef alarmMethod = this.getMethodRef(Constants.ANDROID_APP_PENDINGINTENT, alarmMethodSignature); 
		Value intent = stmt.getInvokeExpr().getArg(0);
		unitsToAdd.add(Jimple.v().newAssignStmt(pi,
				Jimple.v().newStaticInvokeExpr(alarmMethod, 
						thisLocal, IntConstant.v(0), intent, IntConstant.v(0))));

		SootMethodRef getSystemService = this.getMethodRef(Constants.ANDROID_CONTENT_CONTEXT, Constants.GETSYSTEMSERVICE);
		unitsToAdd.add(Jimple.v().newAssignStmt(obj,
				Jimple.v().newVirtualInvokeExpr(thisLocal, getSystemService,
						StringConstant.v(Constants.ALARM))));

		unitsToAdd.add(Jimple.v().newAssignStmt(am,
				Jimple.v().newCastExpr(obj, RefType.v(Constants.ANDROID_APP_ALARMMANAGER))));

		SootMethodRef currentTimeMillis = this.getMethodRef(Constants.JAVA_LANG_SYSTEM, Constants.CURRENTTIMEMILLIS);
		unitsToAdd.add(
				Jimple.v().newAssignStmt(l,
						Jimple.v().newStaticInvokeExpr(currentTimeMillis)));

		unitsToAdd.add(Jimple.v().newAssignStmt(l,
				Jimple.v().newSubExpr(l, LongConstant.v(10000))));

		SootMethodRef set = this.getMethodRef(Constants.ANDROID_APP_ALARMMANAGER, Constants.SET);
		unitsToAdd.add(
				Jimple.v().newInvokeStmt(
						Jimple.v().newVirtualInvokeExpr(am, set,
								IntConstant.v(0), l, pi)));
		return unitsToAdd;
	}

	public List<Unit> generateGetActivity(Body b, Stmt stmt){
		return this.generateGenericBody(b, stmt, Constants.GETACTIVITY);
	}

	public List<Unit> generateGetBroadcast(Body b, Stmt stmt){
		return this.generateGenericBody(b, stmt, Constants.GETBROADCAST);
	}

	public List<Unit> generateGetService(Body b, Stmt stmt){
		return this.generateGenericBody(b, stmt, Constants.GETSERVICE);
	}
	
	private SootMethodRef getMethodRef(String className, String methodName) {
		return Scene.v().getSootClass(className).getMethod(methodName).makeRef();
	}
	
	private Local addLocal(Body b, Type t) {
		Local l = Jimple.v().newLocal(Constants.REF_TMP, t);
		b.getLocals().add(l);
		return l;
	}
}
