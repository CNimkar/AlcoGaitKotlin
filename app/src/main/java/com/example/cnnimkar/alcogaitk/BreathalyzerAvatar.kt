package com.example.cnnimkar.alcogaitk

import BACtrackAPI.API.BACtrackAPI
import BACtrackAPI.API.BACtrackAPICallbacks
import BACtrackAPI.Constants.BACTrackDeviceType
import BACtrackAPI.Exceptions.BluetoothLENotSupportedException
import BACtrackAPI.Exceptions.BluetoothNotEnabledException
import BACtrackAPI.Exceptions.LocationServicesNotEnabledException
import BACtrackAPI.Mobile.Constants.Errors
import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.uber.sdk.android.core.UberSdk
import com.uber.sdk.android.rides.RideParameters
import com.uber.sdk.android.rides.RideRequestButton
import com.uber.sdk.android.rides.RideRequestButtonCallback
import com.uber.sdk.rides.client.ServerTokenSession
import com.uber.sdk.rides.client.SessionConfiguration
import com.uber.sdk.rides.client.error.ApiError
import kotlinx.android.synthetic.main.avatar_screen.*


class BreathalyzerAvatar : AppCompatActivity() {


    val SOBER = 0.00f..0.01f
    val TIPSY = 0.02f..0.06f
    val DRUNK = 0.07f..0.125f
    val WASTED = 0.13f..0.25f

    val SOBER_XP = 100;
    val TIPSY_XP = 50;
    val DRUNK_XP = -20;
    val WASTED_XP = -30;

    val PERMISSIONS_FOR_SCAN: Byte = 100
    val TAG = "BACTrackDemo"
    val apiKey = "37a05ec73c4544328aee3cbd0d8a97"
    var currentXP = 10
    var earnedXP = 0
    lateinit var mAPI: BACtrackAPI
    lateinit var mCallbacks: BACtrackAPICallbacks
    var mContext = this

    //Uber stuff
    val CLIENT_ID = "1lMZjexIabeZ3aPskuwnU4yCAcn94kI9"
    val SERVER_TOKEN = "DI_c-avelqyOmBhtjwXFhddccerBCuEHszuUTUHR"
    val REDIRECT_URI = "https://nimkar.io"
    val ACESS_TOKEN = "KA.eyJ2ZXJzaW9uIjoyLCJpZCI6Imw5SXozbzFpVEpDanBaWEtVQS9vNkE9PSIsImV4cGlyZXNfYXQiOjE1MjY4MDMwODcsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.s2xZKwANigKGIWTaCfI-R0vXIp98KeWOTELyVmOm0wY"

    lateinit var requestButton : RideRequestButton

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.avatar_screen)

        xps.text = currentXP.toString()

        mCallbacks = BACtrackAPICallback()

        supportActionBar!!.hide()

        //test
        GlideApp
                .with(this)
                .load(R.drawable.sober)
                .centerCrop()
                .into(avatarImage)

        currXP.text = "XP : "+currentXP
        delete.setOnClickListener {
            report.visibility =  View.GONE
        }

        try {
            mAPI = BACtrackAPI(this, mCallbacks, apiKey)
        } catch (e: BluetoothLENotSupportedException) {
            e.printStackTrace()
            this.setStatus(R.string.TEXT_ERR_BLE_NOT_SUPPORTED)
        } catch (e: BluetoothNotEnabledException) {
            e.printStackTrace()
            this.setStatus(R.string.TEXT_ERR_BT_NOT_ENABLED)
        } catch (e: LocationServicesNotEnabledException) {
            e.printStackTrace()
            this.setStatus(R.string.TEXT_ERR_LOCATIONS_NOT_ENABLED)
        }

        connectToNearest.setOnClickListener({
            requestButton.loadRideInformation()
            connectNearestClicked()
        })

        disconnect.setOnClickListener({
            disconnectClicked()
        })

        blow.setOnClickListener({
            startBlowProcessClicked()
        })

        val numbers: IntArray = intArrayOf(R.drawable.sober, R.drawable.tipsy, R.drawable.drunk, R.drawable.wasted)
        var click = 0

        demo.setOnClickListener({

            if(click == 4) {click = 0;}

            GlideApp
                    .with(this)
                    .load(numbers[click])
                    .centerCrop()
                    .into(avatarImage)

            click++
        })



       // startService(intentFor<BreathalyzerAvatar>().newTask())
       startForegroundService(Intent(this, GaitService::class.java))

         var  builder : SessionConfiguration.Builder =  SessionConfiguration.Builder()

         // mandatory
      var config : SessionConfiguration =  builder.setClientId(CLIENT_ID)
    // required for enhanced button features
    .setServerToken(SERVER_TOKEN)
    // required for implicit grant authentication
    .setRedirectUri(REDIRECT_URI)
    // optional: set sandbox as operating environment
    .setEnvironment(SessionConfiguration.Environment.SANDBOX)
    .build();

        UberSdk.initialize(config);

        requestButton = RideRequestButton(this);

        var rideParameters : RideParameters  = RideParameters.Builder()
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                .setDropoffLocation(42.279576, -71.8070876, "Home", "189 Grove St, Worcester, MA")
                .setPickupLocation(42.2750591, -71.8087017, "Work", "Fuller Labs WPI, Worcester, MA")
                .build()





         var session  = ServerTokenSession(config);

        var rideCallback =  (object : RideRequestButtonCallback {

    override fun onRideInformationLoaded() {

    }

    override fun onError(apiError : ApiError) {

    }

   override fun onError(throwable : Throwable ) {

    }
})
        requestButton.setRideParameters(rideParameters)
        requestButton.setSession(session)
        requestButton.setCallback(rideCallback)
        requestButton.loadRideInformation()

        //targetLinear.addView(requestButton)
     //   requestButton.layoutParams.height = 250;
     //   requestButton.layoutParams.width = 550;

        requestButton.loadRideInformation()


    }

    fun connectNearestClicked() {
        if (mAPI != null) {
            setStatus(R.string.TEXT_CONNECTING)
            if (ContextCompat.checkSelfPermission(this@BreathalyzerAvatar,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this@BreathalyzerAvatar,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this@BreathalyzerAvatar,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSIONS_FOR_SCAN.toInt())
            } else {
                /**
                 * Permission already granted, start scan.
                 */

                mAPI.connectToNearestBreathalyzer()
            }
        }
    }

    fun disconnectClicked() {
        if (mAPI != null) {
            mAPI.disconnect()
        }
    }

    fun startBlowProcessClicked() {
        var result = false
        if (mAPI != null) {
            result = mAPI.startCountdown()
        }
        if (!result)
                                                      
        else
            Log.d(TAG, "Blow process start requested")
    }


    fun setStatus(message: String) {
        runOnUiThread {
            Log.d(TAG, message)
            statusMessage.text = message
        }
    }

    fun showResult(message: Float){
        runOnUiThread {
            bac.text = message.toString()
            report.visibility = View.VISIBLE
        }
    }

    private fun setStatus(resourceId: Int) {
        this.setStatus(this.resources.getString(resourceId))
    }

    fun showImage(alcoholInput: Double) {
        var image_to_load = 0

        runOnUiThread {
            when {

                alcoholInput in SOBER -> {
                    image_to_load = R.drawable.sober
                    earnedXP = SOBER_XP
                }

                alcoholInput in TIPSY -> {
                    image_to_load = R.drawable.tipsy
                    earnedXP = TIPSY_XP
                }

                alcoholInput in DRUNK -> {
                    image_to_load = R.drawable.drunk
                    earnedXP = DRUNK_XP
                }

                alcoholInput in WASTED -> {
                    image_to_load = R.drawable.wasted
                    earnedXP = WASTED_XP
                }

                else ->
                    image_to_load = R.drawable.ic_launcher_background
            }

            blow.visibility = View.GONE
            disconnect.visibility = View.GONE


            currentXP += earnedXP
            currXP.text = "XP : "+currentXP
            xpBar.progress = currentXP
            xps.text = "+ "+earnedXP
            GlideApp
                    .with(this)
                    .load(image_to_load)
                    .centerCrop()
                    .into(avatarImage)

            statusMessage.visibility = View.GONE
            demo.visibility = View.GONE


            targetLinear.addView(requestButton)
            requestButton.layoutParams.height = 230;
            requestButton.layoutParams.width = 550;
        }
    }

    inner class APIKeyVerificationAlert : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String): String {
            return urls[0]
        }

        override fun onPostExecute(result: String) {
            val apiApprovalAlert = AlertDialog.Builder(mContext)
            apiApprovalAlert.setTitle("API Approval Failed")
            apiApprovalAlert.setMessage(result)
            apiApprovalAlert.setPositiveButton(
                    "Ok"
            ) { dialog, id ->
                mAPI.disconnect()
                setStatus(R.string.TEXT_DISCONNECTED)
                dialog.cancel()
            }

            apiApprovalAlert.create()
            apiApprovalAlert.show()
        }
    }

    inner class BACtrackAPICallback : BACtrackAPICallbacks {

        override fun BACtrackAPIKeyDeclined(errorMessage: String) {
            val verify = APIKeyVerificationAlert()
            verify.execute(errorMessage)
        }

        override fun BACtrackConnected(bacTrackDeviceType: BACTrackDeviceType) {
            runOnUiThread {
                connectToNearest.visibility = View.GONE
                disconnect.visibility = View.VISIBLE
                blow.visibility = View.VISIBLE
                demo.visibility = View.VISIBLE
            }
            setStatus(R.string.TEXT_CONNECTED)
        }

        override fun BACtrackDidConnect(s: String) {
            setStatus(R.string.TEXT_DISCOVERING_SERVICES)
        }

        override fun BACtrackDisconnected() {
            runOnUiThread {
                connectToNearest.visibility = View.VISIBLE
                disconnect.visibility = View.GONE
                blow.visibility = View.GONE
                demo.visibility = View.GONE
            }
            setStatus(R.string.TEXT_DISCONNECTED)
        }

        override fun BACtrackFoundBreathalyzer(bluetoothDevice: BluetoothDevice) {
            Log.d(TAG, "Found breathalyzer : " + bluetoothDevice.name)
        }

        override fun BACtrackCountdown(currentCountdownCount: Int) {
            setStatus(getString(R.string.TEXT_COUNTDOWN) + " " + currentCountdownCount)
        }

        override fun BACtrackStart() {
            setStatus(R.string.TEXT_BLOW_NOW)
        }

        override fun BACtrackBlow() {
            setStatus(R.string.TEXT_KEEP_BLOWING)
        }

        override fun BACtrackAnalyzing() {
            setStatus(R.string.TEXT_ANALYZING)
        }

        override fun BACtrackResults(measuredBac: Float) {
            setStatus("Completed.")
            showResult(measuredBac)
            showImage(measuredBac.toDouble())
        }


        override fun BACtrackError(errorCode: Int) {
            if (errorCode == Errors.ERROR_BLOW_ERROR.toInt())
                setStatus(R.string.TEXT_ERR_BLOW_ERROR)
        }


        //NOT IMPLEMENTED
        override fun BACtrackAPIKeyAuthorized() {}
        override fun BACtrackConnectionTimeout() {}
        override fun BACtrackSerial(p0: String?) {}
        override fun BACtrackFirmwareVersion(p0: String?) {}
        override fun BACtrackBatteryVoltage(p0: Float) {}
        override fun BACtrackUseCount(p0: Int) {}
        override fun BACtrackBatteryLevel(p0: Int) {}
    }
}
