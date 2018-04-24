# AlcoGaitKotlin

<img src="https://user-images.githubusercontent.com/7290739/39168918-b1a9282e-4763-11e8-8c26-580ab4cbdf61.jpg" width="400" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://user-images.githubusercontent.com/7290739/39168919-b1b7cc30-4763-11e8-8e9d-245c398e6874.jpg" width="400" />

## 1. Background service started &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  2. Abnormal gait detected 

<img src="https://user-images.githubusercontent.com/7290739/39168920-b1c62e24-4763-11e8-9479-a11bdb8ac8cb.jpg" width="400" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://user-images.githubusercontent.com/7290739/39168921-b1d47de4-4763-11e8-9efb-83a2f303f898.jpg" width="400" />              

## 3. Uber booking button is &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  4.Uber deeplink 

BreathalyzerAvatar.kt contains code to show output gif for BAC input.
We get a BAC input from a Bluetooth Breathalyzer.
I use Glide library for gif support.
GlideAppGen class generates GlideApp instance. 
The annotation processor (kapt) processes @GlideModule annotation on GlideAppGen.

Breathalyzer.kt uses GlideApp instance.
Important 'val's:
1. SOBER, TIPSY, DRUNK, WASTED are four Float ranges.
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
  4. startCountdown triggers series of callbacks like BACtrackCountdown, BACtrackStart,
     BACtrackBlow, BACtrackAnalyzing, BACtrackResults or BACtrackError.
  5. BACtrackConnected override disables connect button and enables disconnect and blow buttons.
  6. BACtrackDisconnected override disables disconnect and blow and enables connect button.
  7. BACtrackResults override calls showImage method that uses GlideApp instance to set gifs.
  The above step is done on UI thread. 
  8. setupDemoImages is a test func that sets up all images to be shown one by one on click of a button.
  9. setupUber sets up Uber button with ride booking functionality.
  10. onSensorChanged in GaitService.kt looks for "abnormal gait. This service being a stub service,
  (Gait functionality usnig server with ML algorithm to be setup) it looks for x accelerometer value 
  greater than 6 or lower than -6.  That is "assumed" as an abnormal gait.
