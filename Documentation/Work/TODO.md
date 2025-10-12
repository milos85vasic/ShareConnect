# TODO

## MVP

- [Add new automation tests cases into the automation tests](./Tasks/003%20New%20automation%20tests%20to%20add/TASK.md)
- Regular HTTP downloads/urls support
- Contacts flow
  - Report problem
  - Request a feature
  - Misc
- Publishing
  - Google Play Store
  - RuStore
  - AppGallery
  - RuMarket

## For 1.0.1

- [Connect with auto-discovery profiles systems (integrate on the back side as well)](./Tasks/004%20Auto-discovery%20profiles%20systems/TASK.md) 
- Sync the user data (WebDAV)
- Premium features
  - Extra theme packs
  - Additional Providers support
  - Tbd
- Publishing
  - Samsung Store
  - Xiaomi
  - F-Droid
  - APKPure
  - https://apt.izzysoft.de
  - https://www.openapk.net/libretorrent/org.proninyaroslav.libretorrent/
  - https://www.apkmirror.com/apk/proninyaroslav/libretorrent
  - https://libretorrent.en.aptoide.com/app
- Support the project / Donations

# For 1.0.2

- Tbd

## FIXME

- qBitConnect: Crash at start
  ```
   E  FATAL EXCEPTION: main
    Process: com.shareconnect.qbitconnect.debug, PID: 17393
    java.net.BindException: bind failed: EADDRINUSE (Address already in use)
        at libcore.io.IoBridge.bind(IoBridge.java:149)
        at java.net.PlainSocketImpl.socketBind(PlainSocketImpl.java:162)
        at java.net.AbstractPlainSocketImpl.bind(AbstractPlainSocketImpl.java:427)
        at java.net.ServerSocket.bind(ServerSocket.java:399)
        at java.net.ServerSocket.bind(ServerSocket.java:353)
        at io.grpc.okhttp.OkHttpServer.start(OkHttpServer.java:79)
        at io.grpc.internal.ServerImpl.start(ServerImpl.java:185)
        at io.grpc.internal.ServerImpl.start(ServerImpl.java:94)
        at digital.vasic.asinka.transport.GrpcTransport.startServer(GrpcTransport.kt:59)
        at digital.vasic.asinka.AsinkaClient.start(AsinkaClient.kt:73)
        at com.shareconnect.profilesync.ProfileSyncManager.start(ProfileSyncManager.kt:51)
        at com.shareconnect.qbitconnect.App$initializeProfileSync$1.invokeSuspend(App.kt:101)
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:100)
        at android.os.Handler.handleCallback(Handler.java:959)
        at android.os.Handler.dispatchMessage(Handler.java:100)
        at android.os.Looper.loopOnce(Looper.java:257)
        at android.os.Looper.loop(Looper.java:342)
        at android.app.ActivityThread.main(ActivityThread.java:9634)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:619)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:929)
        Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [StandaloneCoroutine{Cancelling}@c6301bb, Dispatchers.Main]
    Caused by: android.system.ErrnoException: bind failed: EADDRINUSE (Address already in use)
        at libcore.io.Linux.bind(Native Method)
        at libcore.io.ForwardingOs.bind(ForwardingOs.java:138)
        at libcore.io.ForwardingOs.bind(ForwardingOs.java:138)
        at libcore.io.IoBridge.bind(IoBridge.java:145)
        ... 21 more
  ```

## In progress

- Testing and fixing issues
- Internal test release [1.0.0 @ 004](./Changes/1.0.0/004.md)
- Firebase configuration for all Application modules
- Migrate from kts and version catalogs to Groovy configs
- Increase all dependency versions
- Connector applications integration
  - qBitConnector
    - Rename composeApp to something meaningful
  - uTorrent Connector (TBD)
    - Asinka to connect both main app anf the connector app
    - New icons
    - Themes sync and applying
      - Theme synchronization settings switch
- Internal test release [1.0.0 @ 005](./Changes/1.0.0/005.md)
- [Enable RAG for AIs and fine tuning](./Tasks/001%20RAG%20and%20MCP%20integration/TASK.md)
  - Move RAG materials into separate Git submodule
    - Submodule Upstreams
    - Private RAG
      - Submodule/Repo Upstreams
  - Obtain Android development documentation with wget mirroring
- Welcome tutorial/wizard
- Internal test release [1.0.0 @ 006](./Changes/1.0.0/005.md)

## Completed

- Internal test release [1.0.0 @ 002](./Changes/1.0.0/002.md)
- Add record exception on all critical error catch sports
- Optimization for small screens and tablets
- https://deepwiki.com/ support
- FIXME: Not all strings are localized yet
- Make sure that shared magnet link is added into the clipboard
- Invite beta testers
- Firebase configuration for the DEV variant
- Internal test release [1.0.0 @ 003](./Changes/1.0.0/003.md)
- Asinka - Update Kotlin version
- Connector applications integration
  - qBitConnector
    - Asinka to connect both main app anf the connector app
    - New icons
    - Themes sync and applying
      - Theme synchronization settings switch
  - TransmissionConnector
    - Asinka to connect both main app anf the connector app
    - New icons
    - Themes sync and applying
      - Theme synchronization settings switch
- FIXME: Open shared magnet link on remote endpoint after signing in
  - Connect our torrent controller as an option
- [Onboarding flow](./Tasks/002%20Onboarding/TASK.md)
- Code coverage reports
  - If low create generic task and send it to AI agent to extend the tests
  - Code coverage badges
- Application names
- qBitConnect: Wrong launcher icon! Same happens with TransmissionConnect!
- qBitConnect: Running application crashes with the following stack trace:
  ```
  E  FATAL EXCEPTION: main
    Process: com.shareconnect.qbitconnect.debug, PID: 5021
    java.lang.UnsupportedOperationException: Use Grpc.newServerBuilderForPort() instead
        at io.grpc.okhttp.OkHttpServerProvider.builderForPort(OkHttpServerProvider.java:41)
        at io.grpc.okhttp.OkHttpServerProvider.builderForPort(OkHttpServerProvider.java:25)
        at io.grpc.ServerBuilder.forPort(ServerBuilder.java:44)
        at digital.vasic.asinka.transport.GrpcTransport.startServer(GrpcTransport.kt:54)
        at digital.vasic.asinka.AsinkaClient.start(AsinkaClient.kt:73)
        at com.shareconnect.languagesync.LanguageSyncManager.start(LanguageSyncManager.kt:54)
        at com.shareconnect.qbitconnect.App$initializeLanguageSync$1.invokeSuspend(App.kt:172)
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:100)
        at android.os.Handler.handleCallback(Handler.java:959)
        at android.os.Handler.dispatchMessage(Handler.java:100)
        at android.os.Looper.loopOnce(Looper.java:257)
        at android.os.Looper.loop(Looper.java:342)
        at android.app.ActivityThread.main(ActivityThread.java:9634)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:619)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:929)
        Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [StandaloneCoroutine{Cancelling}@d5d4953, Dispatchers.Main]
  ```
  Note: Make sure that other applications do not have the same issue!
- Gradle version upgraded

### Archive

- The archive of the [completed TODO items](./Archive.md)
