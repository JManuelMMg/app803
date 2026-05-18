package com.example.appjuan803

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.CircleOptions


class UbicacionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLatitud: TextView
    private lateinit var tvLongitud: TextView

    // Lanzador del permiso
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permiso concedido, obtener ubicación
                obtenerUbicacion()
            } else {
                // Permiso denegado — mostrar dialogo para ir a Ajustes
                mostrarDialogoAjustes()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubicacion)

        tvLatitud = findViewById(R.id.tvLatitud)
        tvLongitud = findViewById(R.id.tvLongitud)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapFragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar el mapa: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        // Verificar permiso apenas el mapa esté listo
        verificarPermiso()
    }

    private fun verificarPermiso() {
        when {
            // Ya tiene permiso
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                obtenerUbicacion()
            }

            // Ya rechazó antes — explicar por qué se necesita
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(this)
                    .setTitle("Permiso de ubicación")
                    .setMessage("Esta app necesita acceder a tu ubicación para mostrarte en el mapa.")
                    .setPositiveButton("Entendido") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            // Primera vez — pedir directamente
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun mostrarDialogoAjustes() {
        AlertDialog.Builder(this)
            .setTitle("Permiso requerido")
            .setMessage("Denegaste el permiso de ubicación. Ve a Ajustes para activarlo manualmente.")
            .setPositiveButton("Ir a Ajustes") { _, _ ->
                // Abrir directamente los ajustes de la app
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

     @SuppressLint("MissingPermission")
     private fun obtenerUbicacion() {
         if (ContextCompat.checkSelfPermission(
                 this, Manifest.permission.ACCESS_FINE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED
         ) return

         try {
             mMap.isMyLocationEnabled = true
         } catch (e: SecurityException) {
             e.printStackTrace()
         }

         // Ir directo a ubicación fresca, no confiar en lastLocation
         solicitarUbicacionActual()
     }

     @SuppressLint("MissingPermission")
     private fun solicitarUbicacionActual() {
         if (ContextCompat.checkSelfPermission(
                 this, Manifest.permission.ACCESS_FINE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED
         ) return

         val locationRequest = LocationRequest.Builder(
             Priority.PRIORITY_HIGH_ACCURACY, 2000L  // más frecuente
         )
             .setMinUpdateIntervalMillis(1000L)
             .setMaxUpdates(1)  // solo una lectura fresca
             .build()

         fusedLocationClient.requestLocationUpdates(
             locationRequest,
             object : LocationCallback() {
                 override fun onLocationResult(result: LocationResult) {
                     val loc = result.lastLocation ?: return
                     mostrarEnMapa(loc.latitude, loc.longitude)
                     fusedLocationClient.removeLocationUpdates(this)
                 }
             },
             mainLooper
         )
     }

    private fun mostrarEnMapa(lat: Double, lng: Double) {
        val posicion = LatLng(lat, lng)

        tvLatitud.text = "Lat:  %.6f°".format(lat)
        tvLongitud.text = "Lng: %.6f°".format(lng)

        mMap.clear()

        // Marcador personalizado estilo Maps
        mMap.addMarker(
            MarkerOptions()
                .position(posicion)
                .title("Mi ubicación")
                .snippet("Lat: %.4f, Lng: %.4f".format(lat, lng))
        )

        // Círculo azul alrededor (como el radio de precisión en Maps)
        mMap.addCircle(
            com.google.android.gms.maps.model.CircleOptions()
                .center(posicion)
                .radius(80.0)
                .strokeColor(0x554285F4.toInt())
                .fillColor(0x224285F4.toInt())
                .strokeWidth(2f)
        )

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 17f))
    }
}