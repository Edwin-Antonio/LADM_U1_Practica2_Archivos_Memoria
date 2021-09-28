package mx.tecnm.tepic.ladm_u1_practica2_archivos_memoria

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var radioOp = 0

        btn_guardar.setOnClickListener {
            var mensaje = ""
            if (radioOp==0){
                if(guardarEnMemoriaInterna(et_frase.text.toString())==true){
                    mensaje = "Se guardo con éxito"
                }else{
                    mensaje = "Error al guardar datos"
                }
                AlertDialog.Builder(this)
                    .setTitle("Resultado")
                    .setMessage(mensaje)
                    .setPositiveButton("ok"){
                            d,i-> d.dismiss()
                    }
                    .show()
            }else{
                if (radioOp==1){
                    guardarEnMemoriaExterna()
                }
            }
            et_frase.setText("")
            et_nombreArchivo.setText("")
            radiogroup.clearCheck()
        }

        btn_abrir.setOnClickListener {
            var contenido = abrirMemoriaInterna()
            var mensaje = ""

            et_frase.setText("")
            radiogroup.clearCheck()
            radiogroup.check(0)
            if(radioOp==0)
            {
                if(contenido.isEmpty()==true){
                    mensaje = "ERROR ARCHIVO NO ENCONTRADO"
                }else
                {
                    mensaje = "Lectura con éxito"
                }
                AlertDialog.Builder(this)
                    .setTitle("Resultado")
                    .setMessage(mensaje)
                    .setPositiveButton("ok"){
                            d,i-> d.dismiss()
                    }
                    .show()
                et_posts.setText(contenido)
            }else{
                abrirEnMemoriaExterna()

            }
        }

        radiogroup.setOnCheckedChangeListener { group, i ->

            if(i == R.id.rbtn_interna){
                radioOp = 0
            }else {
                // Permisos para interactuar con la memoria externa (SD) del telefono e igualmente agregar los persimos en el que esta en el "manifest" de est proyecto
                if (i == R.id.rbtn_externa){
                    if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
                    }else{

                    }
                    radioOp = 1
                }
            }

        }



        btn_nuevo.setOnClickListener {
            cleanTexts() // Funcion propia creada hasta la parte de abajo, checar ahí la funcion
            et_frase.requestFocus()
            if(et_frase.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }

    }

    private fun abrirEnMemoriaExterna() {
        try {
            //INFORMACION SI LA SD SE ENCUENTRA MONTADA
            if(Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED){
                AlertDialog.Builder(this)
                    .setTitle("ERROR DE SD")
                    .setMessage("No hay memoria SD")
                    .show()
                return
            }

            var rutaSD = getExternalFilesDir(null)!!.absolutePath
            var archivoSD = File(rutaSD,"${et_nombreArchivo.text.toString()}.txt")
            var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(archivoSD)))

            et_posts.setText(flujoEntrada.readText())
            AlertDialog.Builder(this)
                .setTitle("Resultado")
                .setMessage("Lectura con éxito")
                .setPositiveButton("ok"){
                        d,i-> d.dismiss()
                }
                .show()
        }catch (ioe:IOException){
            Toast.makeText(this,"ERROR NO SE ENCONTRO EL ARCHIVO",Toast.LENGTH_LONG).show()
        }
    }

    private fun guardarEnMemoriaExterna() {
        try {

            //INFORMACION SI LA SD SE ENCUENTRA MONTADA
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                AlertDialog.Builder(this)
                    .setTitle("ERROR DE SD")
                    .setMessage("No hay memoria SD")
                    .show()
                return
            }

            //ENRUTAMIENTO A LA MEMORIA SD PARA CREAR EL ARCHIVO
            var rutaSD = getExternalFilesDir(null)!!.absolutePath
            var archivoEnSD = File(rutaSD, "${et_nombreArchivo.text.toString()}.txt")
            var flujoSalida = OutputStreamWriter(FileOutputStream(archivoEnSD))

            flujoSalida.write(et_frase.text.toString())
            flujoSalida.flush()
            flujoSalida.close()

            AlertDialog.Builder(this)
                .setMessage("guardado en memoria externa")
                .setPositiveButton("OK"){
                        d,i-> d.dismiss()
                }
                .show()
        }catch (ioe:IOException){
            Toast.makeText(this,ioe.message,Toast.LENGTH_LONG).show()
        }
    }

    private fun guardarEnMemoriaInterna(data:String) : Boolean {
        try{
            var flujoSalida = OutputStreamWriter( openFileOutput("${et_nombreArchivo.text.toString()}.txt", MODE_PRIVATE))

            flujoSalida.write(data)  //Le decimos que es lo que va a guardar
            flujoSalida.flush()     //Empuja la petición para que se escriba
            flujoSalida.close()     //Cierra el archivo y guarda lo que se haya almacenado
        }catch(ioe: IOException){
            return false
        }
        return true
    }

    private fun abrirMemoriaInterna() : String{
        var data = ""

        try {
            var flujoEntrada = BufferedReader(InputStreamReader( openFileInput("${et_nombreArchivo.text.toString()}.txt")))
            data = flujoEntrada.readText()
            flujoEntrada.close()
        }catch (ioe:IOException){
            return ""
        }
        return data
    }

    private fun cleanTexts(){
        et_nombreArchivo.setText("")
        et_frase.setText("")
        et_posts.setText("")
        radiogroup.clearCheck()
    }
}