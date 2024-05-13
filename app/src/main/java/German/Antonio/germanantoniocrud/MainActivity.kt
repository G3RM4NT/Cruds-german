package German.Antonio.germanantoniocrud

import Modelo.Conexion
import Modelo.dataClassMascotas
import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.ResultSet

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
        //1. Mandar a llamar a todos los elementos
        val txtNombre = findViewById<EditText>(R.id.txtnombre)
        val txtPeso = findViewById<EditText>(R.id.txtpeso)
        val txtEdad = findViewById<EditText>(R.id.txtedad)
        val btnAgregar = findViewById<Button>(R.id.btnagragar)
        val rcvMascotas = findViewById<RecyclerView>(R.id.rcvMascotas)



        //Primer paso

        //Asignar layout al recycleview
        rcvMascotas.layoutManager = LinearLayoutManager(
            this)

        fun obtenerDatos(): List<dataClassMascotas> {

            val objConexion = Conexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from tbMascotas")!!
            val mascotas = mutableListOf<dataClassMascotas>()



            while (resultSet.next()){


                val nombre = resultSet.getString("nombreMascota")!!
                val mascotaa = dataClassMascotas(nombre)
                mascotas.add(mascotaa)
            }

            return mascotas
        }

        CoroutineScope(Dispatchers.IO).launch {
            val mascotasDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(mascotasDB)
                rcvMascotas.adapter = adapter
            }
        }





        //2. Programar el boton para agregar
        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //1. Creo un objeto de la clase conexion
                val objConexion = Conexion().cadenaConexion()

                //2. Creo una variable que contenga un PrepareStatement
                val addMascota = objConexion?.prepareStatement("insert into tbMascotas values(?, ?, ?)")!!
                addMascota.setString(1, txtNombre.text.toString())
                addMascota.setInt(2, txtPeso.text.toString().toInt())
                addMascota.setInt(3, txtEdad.text.toString().toInt())

                addMascota.executeUpdate()



                val nuevaMascotas = obtenerDatos()
                withContext(Dispatchers.Main) {
                    (rcvMascotas.adapter as? Adaptador)?.ActualizarLista((nuevaMascotas))
                }
            }
        }
    }
}