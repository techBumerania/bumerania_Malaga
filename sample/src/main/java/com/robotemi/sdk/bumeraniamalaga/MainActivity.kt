package com.robotemi.sdk.bumeraniamalaga

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.robotemi.sdk.*
import com.robotemi.sdk.Robot.*
import com.robotemi.sdk.constants.*
import com.robotemi.sdk.listeners.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.pm.PackageManager
import android.webkit.*
import com.robotemi.sdk.TtsRequest.Companion.create
import com.robotemi.sdk.navigation.model.Position
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), OnRobotReadyListener, OnGoToLocationStatusChangedListener, OnCurrentPositionChangedListener {

    private lateinit var robot: Robot


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        robot = Robot.getInstance()

        video.setVideoPath("android.resource://" + packageName + "/" + R.raw.cara_hablando)

        initOnClickListener()

        Thread {
            while (true){
                if (stateButton){
                    speak("Soy temi de Bumerania Robotics. Si desea más información acerca de mí, consulte con alguno de nuestros comerciales.")
                }
                Thread.sleep(40000)
            }
        }.start()
    }

    /**
     * EVENTS
     */
    override fun onStart() {
        super.onStart()
        robot.addOnRobotReadyListener(this)
        robot.addOnGoToLocationStatusChangedListener(this)
        robot.addOnCurrentPositionChangedListener(this)
    }

    override fun onStop() {
        robot.removeOnRobotReadyListener(this)
        robot.removeOnGoToLocationStatusChangedListener(this)
        robot.removeOnCurrentPositionChangedListener(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            try {
                val activityInfo =
                    packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
                robot.onStart(activityInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                throw RuntimeException(e)
            }
            refreshTemiUi()
        }
    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        if (status=="complete"){
            currentLocation=location
        }
    }

    override fun onCurrentPositionChanged(position: Position) {
        readTime = System.currentTimeMillis()
    }

    private fun initOnClickListener() {
        btnLeave.setOnClickListener{ leave() }
        btnBienvenida.setOnClickListener{ bienvenida() }
        btnConfig.setOnClickListener { loadConfigScreen() }
        btnVolverConfig.setOnClickListener { exitConfigScreen() }
        btnSetText.setOnClickListener { setSpeakText() }
        btnResetText.setOnClickListener { resetSpeakText() }
    }

    /**
     * ROBOT-RELATED FUNCTIONS
     */

    private fun speak(text : String) {
        val ttsRequest = create(text,false, language=language)
        robot.speak(ttsRequest)

    }

    private fun moveAndWait(location: String){
        robot.goTo(location)
        readTime = System.currentTimeMillis()

        var ellapsedTime : Long = 0
        var thresholdTime = 10000


        while (currentLocation!=location){
            ellapsedTime = System.currentTimeMillis() - readTime
            if (ellapsedTime > thresholdTime){
                speak("No he podido alcanzar la localización")
                break
            }
        }

    }

    /**
     * GENERAL FUNCTIONS
     */
    private fun leave(){
        val intent = Intent(this@MainActivity, LockActivity::class.java)
        intent.putExtra("idioma", idiomaActual)
        startActivity(intent)
        finish()
    }

    private fun bienvenida(){
        stateButton = false
        image.visibility = View.GONE
        btnBienvenida.visibility = View.GONE
        video.visibility = View.VISIBLE
        speak(speakText)
        Thread.sleep(10000)
        stateButton = true
        image.visibility = View.VISIBLE
        btnBienvenida.visibility = View.VISIBLE
        video.visibility = View.GONE
    }

    private fun loadConfigScreen(){
        mainLayout.visibility=View.GONE
        configLayout.visibility=View.VISIBLE
    }

    private fun exitConfigScreen(){
        mainLayout.visibility=View.VISIBLE
        configLayout.visibility=View.GONE
    }

    private fun setSpeakText(){
        if(txtEntrada.text.isNotEmpty()) {
            speakText = txtEntrada.text.toString()
            Toast.makeText(this@MainActivity, "Diálogo modificado correctamente", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetSpeakText(){
        speakText= defaultText
        Toast.makeText(this@MainActivity, "Diálogo reseteado correctamente", Toast.LENGTH_LONG).show()
    }

    /**
     * VARIABLES
     */

    companion object{

        private var stateButton = true

        private const val idiomaEspanol = "Español"
        private var idiomaActual = idiomaEspanol
        private var language = TtsRequest.Language.ES_ES


        private var currentLocation = ""

        private var readTime : Long = 0


        private var defaultText = "Bienvenidos a la decimocuarta edición de la Feria Aotec, con la vanguardia en telecomunicaciones y la tecnología que cambiará el futuro."
        private var speakText = defaultText
    }

    private fun refreshTemiUi() {
        try {
            val activityInfo = packageManager
                .getActivityInfo(componentName, PackageManager.GET_META_DATA)
            Robot.getInstance().onStart(activityInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}
