package com.robotemi.sdk.bumeraniamalaga

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robotemi.sdk.Robot
import android.view.WindowManager

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail
import android.widget.Toast
import com.robotemi.sdk.TtsRequest
import kotlinx.android.synthetic.main.activity_valoracion.*
import android.view.ViewGroup

import android.view.View

import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager

import android.widget.EditText
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_lock.*


class ValoracionActivity : AppCompatActivity() {

    private lateinit var robot: Robot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idioma: String? = intent.getStringExtra("idioma")


        if (idioma == "Español"){
            setContentView(R.layout.activity_valoracion)
        }
        else if (idioma == "Ingles"){
            setContentView(R.layout.activity_valoracion_en)
        }

        robot = Robot.getInstance()

        btnEnviarForm.setOnClickListener { enviarFormulario() }

        btnSalirForm.setOnClickListener{
            val intent = Intent(this@ValoracionActivity, MainActivity::class.java)
            if (idioma == null){
                intent.putExtra("idioma", "Español")
            }
            else {
                intent.putExtra("idioma", idioma.toString())
            }
            startActivity(intent)
            finish()
        }


        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        setupUI(findViewById(R.id.absParent))
    }

    private fun speak(text : String) {
        val ttsRequest = TtsRequest.create(text, false, language = TtsRequest.Language.ES_ES)
        robot.speak(ttsRequest)
    }

    private fun speakEn(text : String) {
        val ttsRequest = TtsRequest.create(text, false, language = TtsRequest.Language.EN_US)
        robot.speak(ttsRequest)
    }

    private fun enviarFormulario(){
        val idioma: String? = intent.getStringExtra("idioma")

        when(radioGroup.checkedRadioButtonId){
            radio_muymal.id -> {
                valoracion = 1
            }
            radio_mal.id -> {
                valoracion = 2
            }
            radio_regular.id -> {
                valoracion = 3
            }
            radio_bien.id -> {
                valoracion = 4
            }
            radio_muybien.id -> {
                valoracion = 5
            }
            else->{

                if (idioma == "Español") {
                    speak("Los campos marcados con un asterisco son obligatorios")
                    Toast.makeText(
                        this@ValoracionActivity,
                        "Los campos marcados con (*) son obligatorios",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else if (idioma == "Ingles"){
                    speakEn("Fields marked with an asterisk are required")
                    Toast.makeText(
                        this@ValoracionActivity,
                        "Fields marked with an asterisk (*) are required",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        if (valoracion != -1){
            enviarEmail()
        }


    }

    private fun enviarEmail(){
        var body = ""
        if(in_valoracion.text.isEmpty()){
            body = "Valoracion: ${valoracion}/5 \n" +
                    "Opinion: Sin opinion"
        }
        else{
            body="Valoracion: ${valoracion}/5 \n" +
                    "Opinion: ${in_valoracion.text}"
        }
        BackgroundMail.newBuilder(this)
            .withUsername("tech.bumerania@gmail.com")
            .withPassword("#Bumerania8998")
            .withMailto("tech.bumerania@gmail.com")
            .withType(BackgroundMail.TYPE_PLAIN)
            .withSubject("Valoracion")
            .withBody(body)
            .withOnSuccessCallback{
                val intent = Intent(this@ValoracionActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .send()
    }

    private fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener(OnTouchListener { v, event ->
                hideSoftKeyboard()
                false
            })
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until (view as ViewGroup).childCount) {
                val innerView: View = (view as ViewGroup).getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    companion object{
        private var valoracion = -1

    }



}


