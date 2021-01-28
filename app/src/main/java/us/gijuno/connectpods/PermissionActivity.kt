package us.gijuno.connectpods

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.util.*


class PermissionActivity : AppCompatActivity() {

    private var timer: Timer? = null

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        ) // Location (for BLE)
        // Run in background
        try {
            if (!Objects.requireNonNull(getSystemService(PowerManager::class.java))
                    .isIgnoringBatteryOptimizations(packageName)
            ) {
                val intent = Intent()
                getSystemService(POWER_SERVICE)
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        } catch (ignored: Throwable) {
        }
        // Wait for permissions to be granted.
        // When they are granted, go to MainActivity.
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var ok = true
                try {
                    if (!Objects.requireNonNull(getSystemService(PowerManager::class.java))
                            .isIgnoringBatteryOptimizations(
                                packageName
                            )
                    ) ok = false
                } catch (ignored: Throwable) {
                }
                if (ContextCompat.checkSelfPermission(
                        this@PermissionActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_DENIED
                ) ok = false
                if (ok) {
                    timer!!.cancel()
                    startActivity(Intent(this@PermissionActivity, MainActivity::class.java))
                    finish()
                }
            }
        }, 0, 100)

    }

    // Activity destroyed (or screen rotated). destroy the timer too
    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) timer!!.cancel()
    }
}