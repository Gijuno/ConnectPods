package us.gijuno.connectpods

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PowerManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if Bluetooth LE is available on this device. If not, show an error
        val btAdapter =
            (Objects.requireNonNull(getSystemService(BLUETOOTH_SERVICE)) as BluetoothManager).adapter
        if (btAdapter == null || btAdapter.isEnabled && btAdapter.bluetoothLeScanner == null || !packageManager.hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE
            )
        ) {
            startActivity(Intent(this, NobtActivity::class.java))
            finish()
            return
        }

        // Check if all permissions have been granted
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
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) ok = false

        if (ok) {
            Starter.startPodsService(applicationContext)
            //Warn MIUI users that their rom has known issues
            try {
                @SuppressLint("PrivateApi") val c = Class.forName("android.os.SystemProperties")
                val miuiVersion = c.getMethod("get", String::class.java)
                    .invoke(c, "ro.miui.ui.version.code") as String
                if (miuiVersion != null && !miuiVersion.isEmpty()) {
                    try {
                        applicationContext.openFileInput("miuiwarn").close()
                    } catch (ignored: Throwable) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(R.string.miui_warning)
                        builder.setMessage(R.string.miui_warning_desc)
                        builder.setNeutralButton(R.string.miui_warning_continue) { dialog, which -> dialog.dismiss() }
                        builder.setOnDismissListener { dialog: DialogInterface? ->
                            try {
                                applicationContext.openFileOutput(
                                    "miuiwarn",
                                    MODE_PRIVATE
                                ).close()
                            } catch (ignored2: Throwable) {
                            }
                        }
                        builder.show()
                    }
                }
            } catch (ignored: Throwable) {
            }
        } else {
            startActivity(Intent(this@MainActivity, IntroActivity::class.java))
            finish()
        }

    }
}