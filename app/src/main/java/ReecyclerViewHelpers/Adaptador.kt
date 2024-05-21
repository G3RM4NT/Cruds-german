package RecyclerViewHelper

import German.Antonio.germanantoniocrud.R
import Modelo.dataClassMascotas
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import Modelo.Conexion
import android.app.AlertDialog


class
 Adaptador(private var Datos: List<dataClassMascotas>) : RecyclerView.Adapter<ViewHolder>() {


    fun ActualizarLista(nuevalista: List<dataClassMascotas>){

        Datos = nuevalista
        notifyDataSetChanged()
    }


    ///////////////////TODO: ELIMINAR DATOS

    fun eliminarDatos(nombreMascota: String, posicion: Int){

        //Actulizo la lista de datos y notifico el adaptador
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch (Dispatchers.IO){
            val objConexion = Conexion().cadenaConexion()


            val deleteMascota = objConexion?.prepareStatement(
                "delete from tbmascotas where nombreMascota = ? ")!!
            deleteMascota.setString(1, nombreMascota)
            deleteMascota.executeUpdate()

            val commit = objConexion?.prepareStatement("commit")!!
            commit.executeUpdate()

        }

Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)

        return ViewHolder(vista)
    }
    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = Datos[position]
        holder.textView.text = producto.nombreMascotas


        holder.imgBorrar.setOnClickListener {
            ///creamos un alert dialog


            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar la mascota?")


            //Botones
            builder.setPositiveButton("si"){
                dialog, which ->
                eliminarDatos(producto.nombreMascotas, position)
            }
            builder.setNegativeButton("No"){
                dialog, which ->
                dialog.dismiss()

            }

            val dialog = builder.create()
            dialog.show()

        }

    }

}
