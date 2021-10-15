package com.robotemi.sdk.bumeraniamalaga

import com.andrognito.pinlockview.PinLockView
import com.andrognito.pinlockview.IndicatorDots
import android.os.Bundle
import com.andrognito.pinlockview.PinLockListener
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.robotemi.sdk.Robot
import kotlinx.android.synthetic.main.activity_lock.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.system.exitProcess

class LockActivity : AppCompatActivity() {
    private var mPinLockView: PinLockView? = null
    private var mIndicatorDots: IndicatorDots? = null

    private lateinit var robot: Robot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        robot = Robot.getInstance()

        val idioma: String? = intent.getStringExtra("idioma")

        mPinLockView = findViewById<View>(R.id.pin_lock_view) as PinLockView
        mIndicatorDots = findViewById<View>(R.id.indicator_dots) as IndicatorDots

        //attach lock view with dot indicator
        mPinLockView!!.attachIndicatorDots(mIndicatorDots)

        //set lock code length
        mPinLockView!!.pinLength = 4

        //set listener for lock code change
        mPinLockView!!.setPinLockListener(object : PinLockListener {
            override fun onComplete(pin: String) {
                Log.d(TAG, "lock code: $pin")

                //User input true code
                if (pin == TRUE_CODE) {
                    finish()
                    exitProcess(0)
                } else {
                    Toast.makeText(this@LockActivity, "Código incorrecto", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onEmpty() {
                Log.d(TAG, "lock code is empty!")
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String) {
                Log.d(
                    TAG,
                    "Pin changed, new length $pinLength with intermediate pin $intermediatePin"
                )
            }
        })

        btnVolver.setOnClickListener{
            val intent = Intent(this@LockActivity, MainActivity::class.java)
            if (idioma == null){
                intent.putExtra("idioma", "Español")
            }
            else {
                intent.putExtra("idioma", idioma.toString())
            }
            startActivity(intent)
            finish()
        }

    }

    companion object {
        private val TAG = LockActivity::class.java.simpleName
        private const val TRUE_CODE = "1234"
    }
}