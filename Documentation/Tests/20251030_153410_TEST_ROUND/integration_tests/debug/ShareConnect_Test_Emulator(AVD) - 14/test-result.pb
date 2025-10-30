

emulator-5554primary*û
c
test-results.logOcom.google.testing.platform.runtime.android.driver.AndroidInstrumentationDriver®
•/home/milosvasic/Projects/ShareConnect/ShareConnector/build/outputs/androidTest-results/connected/debug/ShareConnect_Test_Emulator(AVD) - 14/testlog/test-results.log 2
text/plain2¿
QOcom.google.testing.platform.runtime.android.driver.AndroidInstrumentationDriver"INSTRUMENTATION_FAILED*OTest run failed to complete. Instrumentation run failed due to Process crashed.2è*äLogcat of last crash: 
Process: com.shareconnect.debug, PID: 30961
java.lang.RuntimeException: Unable to get provider androidx.startup.InitializationProvider: androidx.startup.StartupException: java.lang.NoSuchMethodException: com.shareconnect.rsssync.RSSSyncManager.<init> []
	at android.app.ActivityThread.installProvider(ActivityThread.java:7770)
	at android.app.ActivityThread.installContentProviders(ActivityThread.java:7276)
	at android.app.ActivityThread.handleBindApplication(ActivityThread.java:6983)
	at android.app.ActivityThread.-$$Nest$mhandleBindApplication(Unknown Source:0)
	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2236)
	at android.os.Handler.dispatchMessage(Handler.java:106)
	at android.os.Looper.loopOnce(Looper.java:205)
	at android.os.Looper.loop(Looper.java:294)
	at android.app.ActivityThread.main(ActivityThread.java:8177)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:552)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:971)
Caused by: androidx.startup.StartupException: java.lang.NoSuchMethodException: com.shareconnect.rsssync.RSSSyncManager.<init> []
	at androidx.startup.AppInitializer.doInitialize(AppInitializer.java:187)
	at androidx.startup.AppInitializer.discoverAndInitialize(AppInitializer.java:239)
	at androidx.startup.AppInitializer.discoverAndInitialize(AppInitializer.java:207)
	at androidx.startup.InitializationProvider.onCreate(InitializationProvider.java:49)
	at android.content.ContentProvider.attachInfo(ContentProvider.java:2619)
	at android.content.ContentProvider.attachInfo(ContentProvider.java:2589)
	at android.app.ActivityThread.installProvider(ActivityThread.java:7765)
	... 11 more
Caused by: java.lang.NoSuchMethodException: com.shareconnect.rsssync.RSSSyncManager.<init> []
	at java.lang.Class.getConstructor0(Class.java:3325)
	at java.lang.Class.getDeclaredConstructor(Class.java:3063)
	at androidx.startup.AppInitializer.doInitialize(AppInitializer.java:165)
	... 17 more
