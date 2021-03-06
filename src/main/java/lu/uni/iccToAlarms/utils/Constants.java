package lu.uni.iccToAlarms.utils;

public class Constants {

	/**
	 * Classes
	 */
	public static final String ANDROID_APP_PENDINGINTENT = "android.app.PendingIntent";
	public static final String JAVA_LANG_OBJECT = "java.lang.object";
	public static final String ANDROID_CONTENT_CONTEXT = "android.content.Context";
	public static final String ANDROID_APP_ALARMMANAGER = "android.app.AlarmManager";
	public static final String JAVA_LANG_SYSTEM = "java.lang.System";
	
	/**
	 * Methods
	 */
	public static final String GETACTIVITY = "android.app.PendingIntent getActivity(android.content.Context,int,android.content.Intent,int)";
	public static final String GETBROADCAST = "android.app.PendingIntent getBroadcast(android.content.Context,int,android.content.Intent,int)";
	public static final String GETSERVICE = "android.app.PendingIntent getService(android.content.Context,int,android.content.Intent,int)";
	public static final String GETSYSTEMSERVICE = "java.lang.Object getSystemService(java.lang.String)";
	public static final String CURRENTTIMEMILLIS = "long currentTimeMillis()";
	public static final String SET = "void set(int,long,android.app.PendingIntent)";
	public static final String STARTACTIVITY = "<android.app.Activity: void startActivity(android.content.Intent)>";
	public static final String SENDBROADCAST = "<android.content.ContextWrapper: void sendBroadcast(android.content.Intent)>";
	public static final String STARTSERVICE = "<android.content.ContextWrapper: android.content.ComponentName startService(android.content.Intent)>";
	
	/**
	 * String Constants
	 */
	public static final String ALARM = "alarm";
	public static final String REF_TMP = "ref_tmp";
}
