# AlcoGaitKotlin
BreathalyzerAvatar.kt contains code to show output gif for BAC input.
We get a BAC input from a Bluetooth Breathalyzer.
I use Glide library for gif support.
GlideAppGen class generates GlideApp instance. 
The annotation processor (kapt) processes @GlideModule annotation on GlideAppGen.

Breathalyzer.kt uses GlideApp instance.
Important 'val's:
1. SOBER, TIPSY, DRUNK, WASTED are four Double ranges.
2. apiKey is generated for you by BACTrack, on registration.

Important 'var's:
1. mAPI is BACTrackAPI instance, connectToNearestBreathalyzer, disconnect,
  startCountdown are the methods it provides. 
  For more details about the api,refer to https://developer.bactrack.com/documentation#android-bactrack-api
2. mCallbacks is a reference to all the callbacks in BACtrackAPICallbacks interface.
  refer to above URL to understand these callbacks in detail. Mostly they are the callbacks
  that go through every step of search, conncetion, error handling and most imporant of all BAC result.
  BACtrackResults is the callback that's worth mentioning here, which returns float value.
  This float value is the calculated BAC.
  
  Important methods:
  1. connectNearestClicked is a wrapper for mAPI.connectToNearestBreathalyzer()
  2. dissconectClicked is a wrapper for mAPI.disconnect()
  3. startBlowProcessClicked is a wrapper for mAPI.startCountdown()
  4. BACtrackConnected override disables connect button and enables disconnect and blow buttons.
  5. BACtrackDisconnected override disables disconnect and blow and enables connect button.
  6. BACtrackResults override calls showImage method that uses GlideApp instance to set gifs.
  The above step is done on UI thread. 
