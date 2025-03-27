package com.example.c6_1

import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MainScreen(modifier: Modifier, location : Location){

    val context = LocalContext.current
    val geocode = Geocoder(context)
    val defaultLocation = remember{mutableStateOf(LatLng(location.latitude,location.longitude))}
    val customLocation = remember{mutableListOf<LatLng>()}
    var where = remember { mutableStateOf("Fetching address...") }
    customLocation.add(defaultLocation.value)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation.value, 12f)
    }
    LaunchedEffect(location) {
        defaultLocation.value = LatLng(location.latitude, location.longitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation.value, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { LatLng ->
            customLocation.add(LatLng)
        }
    ){
        customLocation.forEach {
            latlng ->
            MarkerInfoWindow(
                state = MarkerState(position = latlng)
                ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .border(
                            BorderStroke(1.dp, Color.Black),
                            RoundedCornerShape(10)
                        )
                        .clip(RoundedCornerShape(10))
                        .background(Color.Blue)
                        .padding(20.dp)
                ) {
                    val lat = latlng.latitude
                    val long = latlng.longitude
                    geocode.getFromLocation(lat,long, 1, object : Geocoder.GeocodeListener{
                        override fun onGeocode(addresses: MutableList<Address>) {
                            where.value = addresses.firstOrNull()?.getAddressLine(0) ?: "Address not found"
                        }
                        fun onError(){
                            where.value = "Error Occured"
                        }
                    })
                    Text(text = where.value)
                }
            }
        }
    }


}