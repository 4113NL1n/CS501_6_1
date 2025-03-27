package com.example.c6_1

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.c6_1.ui.theme.C6_1Theme
import com.google.android.gms.location.*

//Will seperate for seperation of concerns

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1001).apply {
            setMinUpdateIntervalMillis(1000)
        }.build()

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    enableEdgeToEdge()
                    setContent {
                        Scaffold (modifier = Modifier.fillMaxSize()){ innerPadding ->
                            C6_1Theme {
                                MainScreen(Modifier.padding(innerPadding), location)
                            }
                        }

                    }
                }
            }
        }
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                C6_1Theme {
                    RequestPermission(fusedLocationClient, locationRequest, locationCallback)
                }
            }
        }
    }
    //Restart when app is reopened, not created
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
    //Stops updating in the background
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

@Composable
fun RequestPermission(
    fusedLocationProviderClient: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    locationCallback: LocationCallback
){
    val context = LocalContext.current
    var hasLocationPermisson by remember{
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        isGranted : Boolean ->
        hasLocationPermisson = isGranted
        if(isGranted){
            try {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
            }catch (e : SecurityException){
                Log.e("Location", "Secruity Exception")
            }
        }
    }
    LaunchedEffect(hasLocationPermisson) {
        if(!hasLocationPermisson){
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            try {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
            }catch (e : SecurityException){
                Log.e("Location", "Secruity Exception")
            }
        }
    }

}
