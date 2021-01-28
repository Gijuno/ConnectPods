package us.gijuno.connectpods

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

/**
 * A simple starter class that starts the service when the device is booted, or after an update
 */
class Starter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (Objects.requireNonNull(intent.action)) {
            Intent.ACTION_MY_PACKAGE_REPLACED, Intent.ACTION_BOOT_COMPLETED -> startPodsService(
                context
            )
        }
    }

    companion object {
        fun startPodsService(context: Context) {
            context.startService(Intent(context, PodsService::class.java))
        }

        fun restartPodsService(context: Context) {
            context.stopService(Intent(context, PodsService::class.java))
            try {
                Thread.sleep(500)
            } catch (ignored: Throwable) {
            }
            context.startService(Intent(context, PodsService::class.java))
        }
    }
}
