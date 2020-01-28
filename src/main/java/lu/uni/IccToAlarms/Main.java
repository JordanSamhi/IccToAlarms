package lu.uni.IccToAlarms;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lu.uni.IccToAlarms.iccMethodsRecognizer.IccMethodsRecognizerHandler;
import lu.uni.IccToAlarms.iccMethodsRecognizer.StartActivityRecognizer;
import lu.uni.IccToAlarms.utils.Constants;
import soot.Body;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeStmt;
import soot.options.Options;

public class Main {
	
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		initializeSoot();
		PackManager.v().getPack("wjtp").add(
				new Transform("wjtp.myTransform", new SceneTransformer() {
					protected void internalTransform(String phaseName,
							Map options) {
						for(SootClass sc : Scene.v().getApplicationClasses()) {
							if(!sc.getName().startsWith("android.")) {
								for(SootMethod sm : sc.getMethods()) {
									Body b = sm.retrieveActiveBody();
									final PatchingChain<Unit> units = b.getUnits();
									for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
										final Unit u = iter.next();
										u.apply(new AbstractStmtSwitch() {
											public void caseInvokeStmt(InvokeStmt stmt) {
												SootMethod methodCalled = stmt.getInvokeExpr().getMethod();
												IccMethodsRecognizerHandler imrh = new StartActivityRecognizer(null);
												List<Unit> unitsToAdd = imrh.recognizeIccMethod(b, methodCalled, stmt);
												if(unitsToAdd != null && !unitsToAdd.isEmpty()) {
													logger.info(String.format("%-10s: %s", "Icc call", methodCalled.getSubSignature()));
													logger.info(String.format("%-10s: %s", "-- Method", sm.getSubSignature()));
													logger.info(String.format("%-10s: %s", "-- Class", sc));
													units.insertBefore(unitsToAdd, stmt);
													units.remove(stmt);
													b.validate();
												}
											}
										});
									}
								}
							}
						}
					}
				}));
		soot.Main.main(args);
	}

	private static void initializeSoot() {
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);

		Scene.v().addBasicClass(Constants.ANDROID_APP_PENDINGINTENT, SootClass.SIGNATURES);
		Scene.v().addBasicClass(Constants.JAVA_LANG_OBJECT, SootClass.SIGNATURES);
		Scene.v().addBasicClass(Constants.ANDROID_CONTENT_CONTEXT, SootClass.SIGNATURES);
		Scene.v().addBasicClass(Constants.ANDROID_APP_ALARMMANAGER, SootClass.SIGNATURES);
		Scene.v().addBasicClass(Constants.JAVA_LANG_SYSTEM, SootClass.SIGNATURES);
	}
}
