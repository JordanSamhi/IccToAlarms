package lu.uni.IccToAlarms;

import java.util.Iterator;
import java.util.Map;

import lu.uni.IccToAlarms.utils.Constants;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.LongType;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import soot.options.Options;

public class Main {
	
	public static void main(String[] args) {
		G.reset();
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_output_dir("/home/jordan/lab/iccToAlarms/instrumentedApp");
		
		Scene.v().addBasicClass(Constants.ANDROID_APP_PENDINGINTENT, SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.object", SootClass.SIGNATURES);
		Scene.v().addBasicClass("android.content.Context", SootClass.SIGNATURES);
		Scene.v().addBasicClass("android.app.AlarmManager", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
		
		PackManager.v().getPack("jtp").add(new Transform("jtp.myTransformer", new BodyTransformer() {

			@Override
			protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
				final PatchingChain<Unit> units = b.getUnits();

				for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
					final Unit u = iter.next();
					u.apply(new AbstractStmtSwitch() {
						public void caseInvokeStmt(InvokeStmt stmt) {
							SootMethod methodCalled = stmt.getInvokeExpr().getMethod();
							if(methodCalled.getSignature().equals("<android.app.Activity: void startActivity(android.content.Intent)>")) {
								// Locals
								Local pi = addLocal(b, RefType.v("android.app.PendingIntent"));
								Local obj = addLocal(b, RefType.v("java.lang.object"));
								Local am = addLocal(b, RefType.v("android.app.AlarmManager"));
								Local l = addLocal(b, LongType.v());
								Local thiss = b.getThisLocal();
								
								// pi = PendingIntent.getActivity(this, 0, intent, 0)
								SootMethod getActivity = Scene.v().getSootClass("android.app.PendingIntent")
										.getMethod("android.app.PendingIntent getActivity(android.content.Context,int,android.content.Intent,int)");
								
								Value intent = stmt.getInvokeExpr().getArg(0);
								units.insertBefore(Jimple.v().newAssignStmt(pi, 
										Jimple.v().newStaticInvokeExpr(getActivity.makeRef(), 
												thiss, IntConstant.v(0), intent, IntConstant.v(0))), u);
								
								// obj = getSystenService("alarm")
								SootMethod getSystemService = Scene.v().getSootClass("android.content.Context")
										.getMethod("java.lang.Object getSystemService(java.lang.String)");
								units.insertBefore(Jimple.v().newAssignStmt(obj,
										Jimple.v().newVirtualInvokeExpr(thiss, getSystemService.makeRef(),
												StringConstant.v("alarm"))), u);
								
								//am = (AlarmManager) obj;
								units.insertBefore(Jimple.v().newAssignStmt(am,
										Jimple.v().newCastExpr(obj, RefType.v("android.app.AlarmManager"))), u);
								
								// l = currentTimeMillis()
								SootMethod currentTimeMillis = Scene.v().getSootClass("java.lang.System")
										.getMethod("long currentTimeMillis()");
								units.insertBefore(
										Jimple.v().newAssignStmt(l,
												Jimple.v().newStaticInvokeExpr(currentTimeMillis.makeRef())), u);
								
								// l = l - 1;
								units.insertBefore(Jimple.v().newAssignStmt(l,
										Jimple.v().newSubExpr(l, LongConstant.v(10000))), u);
								
								// am.set()
								SootMethod set = Scene.v().getSootClass("android.app.AlarmManager")
										.getMethod("void set(int,long,android.app.PendingIntent)");
								units.insertBefore(
										Jimple.v().newInvokeStmt(
												Jimple.v().newVirtualInvokeExpr(am, set.makeRef(), 
														IntConstant.v(0), l, pi)), u);
								units.remove(u);
								b.validate();
							}
						}
					});
				}
			}
		}));
		soot.Main.main(args);
	}
	private static Local addLocal(Body b, Type t) {
		Local l = Jimple.v().newLocal("l", t);
		b.getLocals().add(l);
		return l;
	}
}
