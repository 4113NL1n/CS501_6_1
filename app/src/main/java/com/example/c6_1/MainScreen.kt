package com.example.c6_1

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MainScreen(modifier: Modifier, location : Location){
    val currLocation = remember{
        mutableStateOf(LatLng(location.latitude,location.longitude))
    }
    val cameraPostion = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(currLocation.value,12f)
    }
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPostion
    )

}