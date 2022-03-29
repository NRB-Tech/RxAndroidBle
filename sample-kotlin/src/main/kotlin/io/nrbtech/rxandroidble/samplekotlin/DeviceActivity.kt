package io.nrbtech.rxandroidble.samplekotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.nrbtech.rxandroidble.samplekotlin.databinding.ActivityDeviceBinding
import io.nrbtech.rxandroidble.samplekotlin.example2_connection.ConnectionExampleActivity
import io.nrbtech.rxandroidble.samplekotlin.example3_discovery.ServiceDiscoveryExampleActivity

private const val EXTRA_MAC_ADDRESS = "extra_mac_address"

class DeviceActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context, macAddress: String): Intent =
            Intent(context, DeviceActivity::class.java).apply { putExtra(EXTRA_MAC_ADDRESS, macAddress) }
    }

    private lateinit var macAddress: String

    private var binding: ActivityDeviceBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        macAddress = intent.getStringExtra(EXTRA_MAC_ADDRESS)!!
        supportActionBar!!.subtitle = getString(R.string.mac_address, macAddress)

        binding = ActivityDeviceBinding.inflate(layoutInflater)

        binding!!.connect.setOnClickListener { startActivity(ConnectionExampleActivity.newInstance(this, macAddress)) }
        binding!!.discovery.setOnClickListener { startActivity(ServiceDiscoveryExampleActivity.newInstance(this, macAddress)) }
    }
}
