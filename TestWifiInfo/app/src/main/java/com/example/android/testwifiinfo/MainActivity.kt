package com.example.android.testwifiinfo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.net.wifi.WifiManager
import android.net.NetworkInfo
import android.content.IntentFilter
import android.content.Intent
import android.content.BroadcastReceiver
import android.view.View
import com.example.android.miningtag.CustomSQL
import com.example.android.miningtag.Red


class MainActivity : AppCompatActivity() {
  val customSQL = CustomSQL(this, "miBD", null, 1)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    lblSSID.text = "INICIO"

    val permisos = arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION)
    var granted = true
    for (p in permisos){
      //Acumula evaluaciones
      granted = granted and (ActivityCompat.checkSelfPermission(this,p) == PackageManager.PERMISSION_GRANTED)
    }
    if(!granted){
      ActivityCompat.requestPermissions(this, permisos,1)
    }

    btnRegistrar.setOnClickListener {
      if(customSQL.insertar(lblSSID.text.toString(),txtName.text.toString())){
        lblName.text = "Bienvenido a ${txtName.text}"
        txtName.text.clear()
        txtName.isEnabled = false
        btnRegistrar.isEnabled = false
        btnRegistrar.visibility = View.INVISIBLE
        txtName.visibility = View.INVISIBLE

      }
    }

  }

  fun getCurrentBssid(context: Context): String? {
    var bssid: String? = null
    val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connManager.activeNetworkInfo.isConnected) {
      if(connManager.activeNetworkInfo.type.equals(ConnectivityManager.TYPE_WIFI)) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.connectionInfo
        if (connectionInfo != null && !connectionInfo.bssid.isBlank()) {
          bssid = connectionInfo.bssid
        }
      }else{
        lblSSID.text = "No está conectado a una red WIFI"
        lblName.text = ""
        btnRegistrar.isEnabled = false
        btnRegistrar.visibility = View.INVISIBLE
        txtName.isEnabled = false
        txtName.visibility = View.INVISIBLE
      }
    }
    return bssid
  }


  private val networkStateReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val ni = manager.activeNetworkInfo
      onNetworkChange(ni)
    }
  }

  public override fun onResume() {
    super.onResume()
    registerReceiver(
      networkStateReceiver,
      IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    )
  }

  public override fun onPause() {
    unregisterReceiver(networkStateReceiver)
    super.onPause()
  }

  private fun onNetworkChange(networkInfo: NetworkInfo?) {
    if (networkInfo != null) {
      val bssid = getCurrentBssid(this)
      if(!bssid.isNullOrBlank()) {
        var red: Red? = customSQL.buscar(bssid)
        lblSSID.text = bssid
        if(red != null){
          lblName.text = "Bienvenido a ${red.nombre}"
          btnRegistrar.isEnabled = false
          btnRegistrar.visibility = View.INVISIBLE
          txtName.isEnabled = false
          txtName.visibility = View.INVISIBLE
        }else{
          lblName.text = "La red no se encuentra registrada"
          btnRegistrar.visibility = View.VISIBLE
          btnRegistrar.isEnabled = true
          txtName.visibility = View.VISIBLE
          txtName.isEnabled = true
        }
      }

    }
  }
}
