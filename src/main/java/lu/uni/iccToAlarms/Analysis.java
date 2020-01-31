package lu.uni.iccToAlarms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.uni.iccToAlarms.iccMethodsRecognizer.IccMethodsRecognizerHandler;
import lu.uni.iccToAlarms.iccMethodsRecognizer.SendBroadcastRecognizer;
import lu.uni.iccToAlarms.iccMethodsRecognizer.StartActivityRecognizer;
import lu.uni.iccToAlarms.iccMethodsRecognizer.StartServiceRecognizer;
import lu.uni.iccToAlarms.utils.CommandLineOptions;
import lu.uni.iccToAlarms.utils.Constants;
import soot.Body;
import soot.G;
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

public class Analysis {

	private CommandLineOptions options;

	private Logger logger = LoggerFactory.getLogger(Main.class);

	public Analysis(String[] args) {
		this.options = new CommandLineOptions(args);
	}

	public void run() {
		initializeSoot();
		PackManager.v().getPack("wjtp").add(
				new Transform("wjtp.myTransform", new SceneTransformer() {
					protected void internalTransform(String phaseName,
							@SuppressWarnings("rawtypes") Map options) {
						for(SootClass sc : Scene.v().getApplicationClasses()) {
							if(!isSystemClass(sc.getName()) && sc.isConcrete()) {
								for(SootMethod sm : sc.getMethods()) {
									Body b = sm.retrieveActiveBody();
									if(sm.isConcrete()) {
										final PatchingChain<Unit> units = b.getUnits();
										for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
											final Unit u = iter.next();
											u.apply(new AbstractStmtSwitch() {
												public void caseInvokeStmt(InvokeStmt stmt) {
													SootMethod methodCalled = stmt.getInvokeExpr().getMethod();
													IccMethodsRecognizerHandler imrh = new StartActivityRecognizer(null);
													imrh = new SendBroadcastRecognizer(imrh);
													imrh = new StartServiceRecognizer(imrh);
													List<Unit> newUnits = imrh.recognizeIccMethod(b, methodCalled, stmt);
													if(newUnits != null && !newUnits.isEmpty()) {
														logger.info(String.format("%-16s: %s", "Icc call found", methodCalled.getSubSignature()));
														logger.info(String.format("%-16s: %s", "-- Class", sc));
														logger.info(String.format("%-16s: %s", "-- Method", sm.getSubSignature()));
														logger.info("Successfully transformed !");
														units.insertBefore(newUnits, stmt);
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
					}
				}));
		PackManager.v().runPacks();
		PackManager.v().writeOutput();
	}

	private void initializeSoot() {
		G.reset();
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);
		Options.v().set_android_jars(this.options.getPlatforms());
		List<String> apps = new ArrayList<String>();
		apps.add(this.options.getApk());
		Options.v().set_process_dir(apps);
		Options.v().set_output_dir(this.options.getOutput());
		Options.v().set_force_overwrite(true);
		

		Scene.v().addBasicClass(Constants.ANDROID_APP_PENDINGINTENT, SootClass.SIGNATURES);
		Scene.v().addBasicClass(Constants.JAVA_LANG_OBJECT, SootClass.SIGNATURES);
		Scene.v().addBasicClass(Constants.ANDROID_CONTENT_CONTEXT, SootClass.SIGNATURES);
		Scene.v().addBasicClass(Constants.ANDROID_APP_ALARMMANAGER, SootClass.SIGNATURES);
		Scene.v().addBasicClass(Constants.JAVA_LANG_SYSTEM, SootClass.SIGNATURES);
		Scene.v().loadNecessaryClasses();
	}

	// Inspired by Flowdroid
	private boolean isSystemClass(String className) {
		return (className.startsWith("android.") || className.startsWith("java.") || className.startsWith("javax.")
				|| className.startsWith("sun.") || className.startsWith("org.omg.")
				|| className.startsWith("org.w3c.dom.") || className.startsWith("com.google.")
				|| className.startsWith("com.android."));
	}
}
