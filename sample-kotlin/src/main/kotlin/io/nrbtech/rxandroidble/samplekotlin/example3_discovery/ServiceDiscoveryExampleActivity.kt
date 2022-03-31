package io.nrbtech.rxandroidble.samplekotlin.example3_discovery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.nrbtech.rxandroidble.RxBleDevice
import io.nrbtech.rxandroidble.samplekotlin.R
import io.nrbtech.rxandroidble.samplekotlin.SampleApplication
import io.nrbtech.rxandroidble.samplekotlin.databinding.ActivityExample3Binding
import io.nrbtech.rxandroidble.samplekotlin.example3_discovery.DiscoveryResultsAdapter.AdapterItem
import io.nrbtech.rxandroidble.samplekotlin.example4_characteristic.CharacteristicOperationExampleActivity
import io.nrbtech.rxandroidble.samplekotlin.util.isConnected
import io.nrbtech.rxandroidble.samplekotlin.util.showSnackbarShort
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

private const val EXTRA_MAC_ADDRESS = "extra_mac_address"

class ServiceDiscoveryExampleActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context, macAddress: String) =
            Intent(context, ServiceDiscoveryExampleActivity::class.java).apply {
                putExtra(EXTRA_MAC_ADDRESS, macAddress)
            }
    }

    private lateinit var bleDevice: RxBleDevice

    private lateinit var macAddress: String

    private val resultsAdapter = DiscoveryResultsAdapter { onAdapterItemClick(it) }

    private val discoveryDisposable = CompositeDisposable()

    private var binding: ActivityExample3Binding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example3)

        binding = ActivityExample3Binding.inflate(layoutInflater)

        binding!!.connect.setOnClickListener { onConnectToggleClick() }

        macAddress = intent.getStringExtra(EXTRA_MAC_ADDRESS)!!
        supportActionBar!!.subtitle = getString(R.string.mac_address, macAddress)
        bleDevice = SampleApplication.rxBleClient.getBleDevice(macAddress)

        binding!!.scanResults.apply {
            setHasFixedSize(true)
            adapter = resultsAdapter
        }
    }

    private fun onConnectToggleClick() {
        bleDevice.establishConnection(false)
            .flatMapSingle { it.discoverServices() }
            .take(1) // Disconnect automatically after discovery
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { updateUI() }
            .doFinally { updateUI() }
            .subscribe({ resultsAdapter.swapScanResult(it) }, { showSnackbarShort("Connection error: $it") })
            .let { discoveryDisposable.add(it) }
    }

    private fun onAdapterItemClick(item: AdapterItem) {
        when (item.type) {
            AdapterItem.CHARACTERISTIC -> {
                startActivity(CharacteristicOperationExampleActivity.newInstance(this, macAddress, item.uuid))
                // If you want to check the alternative advanced implementation comment out the line above and uncomment one below
//            startActivity(AdvancedCharacteristicOperationExampleActivity.newInstance(this, macAddress, item.uuid))
            }
            else -> showSnackbarShort(R.string.not_clickable)
        }
    }

    private fun updateUI() {
        binding!!.connect.isEnabled = !bleDevice.isConnected
    }

    override fun onPause() {
        super.onPause()
        discoveryDisposable.clear()
    }
}
