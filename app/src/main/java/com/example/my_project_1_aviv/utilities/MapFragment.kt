package com.example.my_project_1_aviv.utilities

import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : SupportMapFragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadMarkers()
    }

    private fun loadMarkers() {
        if (googleMap == null) return

        context?.let { ctx ->
            val scoreManager = ScoreManager(ctx)
            val scoreList = scoreManager.getAllScores()
            googleMap?.clear()
            for (score in scoreList) {
                val location = LatLng(score.lat, score.lon)
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("${score.name}: ${score.score}")
                )
            }

            val telAviv = LatLng(32.0853, 34.7818)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(telAviv, 10f))
        }
    }

    fun zoom(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}