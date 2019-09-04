package com.example.android.miningtag

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class CustomSQL(val context:Context?,
                val name:String,
                val factory:SQLiteDatabase.CursorFactory?,
                var version:Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE redes(BSSID String PRIMARY KEY, nombre String)"
        db?.execSQL(query)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = "DROP TABLE IF EXISTS redes;"
        db?.execSQL(query)
        onCreate(db)
    }
    fun insertar(bssid:String,
                 nombre:String):Boolean{
        var result:Boolean = false
        try {
            val db = this.writableDatabase
            var cv = ContentValues()
            cv.put("BSSID",bssid)
            cv.put("nombre",nombre)

            var resultado = db.insert("redes",null,cv)
            db.close()
            if(resultado != -1L){
                result = true
                Toast.makeText(context,"Red ${nombre} insertada",Toast.LENGTH_SHORT).show()

            }
        }catch (e:SQLException){
            Toast.makeText(context,"Error al insertar ${e.message}",Toast.LENGTH_SHORT).show()
            Log.e("sqlInsertar",e.message)
        }
        return result
    }
    fun listar() : ArrayList<Red>{
        var lista:ArrayList<Red> = ArrayList()
        try {
            val db = this.writableDatabase
            var cursor:Cursor? = null
            cursor = db.rawQuery("select * from redes",null)
            if(cursor.moveToFirst()){
                do {
                    val bssid = cursor.getString(0)
                    val nombre = cursor.getString(1)
                    val registro = Red(bssid, nombre)
                    //Log.i("sqlListar","BSSID: ${bssid} - NOMBRE: ${nombre}")
                    lista.add(registro)
                }while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        }catch (e:SQLException){
            Toast.makeText(context,"Error al listar ${e.message}",Toast.LENGTH_SHORT).show()
            Log.e("sqlListar",e.message)
        }
        return lista
    }
    fun buscar(bssid:String) : Red?{
        var registro:Red? = null
        //Log.i("sqlBuscar","Buscando... ${bssid}")
        try {
            val db = this.writableDatabase
            val args = arrayOf(bssid.toString())
            var cursor:Cursor? = null
            cursor = db.rawQuery("select * from redes where BSSID = ?",args)
            //Log.i("sqlBuscar","Encontrados.... ${cursor.count}")

            if(cursor.moveToFirst()){
                do {
                    val bssid = cursor.getString(0)
                    val nombre = cursor.getString(1)
                    registro = Red(bssid, nombre)
                    //Toast.makeText(context,"BSSID encontrado",Toast.LENGTH_SHORT).show()
                    //Log.i("sqlBuscar","Encontrado ${bssid}")
                }while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        }catch (e:SQLException){
            Toast.makeText(context,"Error al listar ${e.message}",Toast.LENGTH_SHORT).show()
            Log.e("sqlBuscar",e.message)
        }
        return registro
    }
    fun eliminarTodo() : Boolean{
        var result:Boolean = false
        try {
            val db = this.writableDatabase
            var resultado = db.delete("redes","1 = 1",null)
            db.close()
            if(resultado != 0){
                result = true
            }
        }catch (e:SQLException){
            Toast.makeText(context,"Error al eliminar ${e.message}",Toast.LENGTH_SHORT).show()
            Log.e("sqlEliminar",e.message)
        }
        return result
    }

    fun eliminar(ssid:String){
        try {
            val db = this.writableDatabase
            val args = arrayOf(ssid.toString())
            var resultado = db.delete("redes","BSSID = ?",args)
            db.close()
            if(resultado == 0){
                Toast.makeText(context,"Red no eliminada",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context,"Red eliminada",Toast.LENGTH_LONG).show()
            }

        }catch (e:SQLException){
            Toast.makeText(context,"Error al eliminar ${e.message}",Toast.LENGTH_SHORT).show()
            Log.e("sqlEliminar",e.message)
        }
    }

}