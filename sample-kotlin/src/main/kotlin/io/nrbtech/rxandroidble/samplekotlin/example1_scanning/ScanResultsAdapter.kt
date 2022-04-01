package io.nrbtech.rxandroidble.samplekotlin.example1_scanning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.nrbtech.rxandroidble.samplekotlin.example1_scanning.ScanResultsAdapter.ViewHolder
import io.nrbtech.rxandroidble.scan.ScanResult
import io.nrbtech.rxandroidble.scan.ScanResultInterface
import java.util.*

internal class ScanResultsAdapter(
    private val onClickListener: (ScanResult) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val device: TextView = itemView.findViewById(android.R.id.text1)
        val rssi: TextView = itemView.findViewById(android.R.id.text2)
    }

    private val data = mutableListOf<ScanResult>()

    fun addScanResult(bleScanResult: ScanResult) {
        // Not the best way to ensure distinct devices, just for the sake of the demo.
        data.withIndex()
            .firstOrNull { it.value.bleDevice == bleScanResult.bleDevice }
            ?.let {
                // device already in data list => update
                data[it.index] = bleScanResult
                notifyItemChanged(it.index)
            }
            ?: run {
                // new device => add to data list
                with(data) {
                    add(bleScanResult)
                    sortBy { it.bleDevice.macAddress }
                }
                notifyDataSetChanged()
            }
    }

    fun clearScanResults() {
        data.clear()
        notifyDataSetChanged()
    }

    private var showDetails = false
    fun setShowDetails(showDetails: Boolean) {
        this.showDetails = showDetails
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            holder.device.text = String.format("%s (%s)", bleDevice.macAddress, bleDevice.name)
            var details = String.format(
                Locale.getDefault(),
                "RSSI: %d",
                rssi
            )
            if (showDetails) {
                details += String.format(
                    Locale.getDefault(),
                    """
                        
                        legacy: %s
                        connectable: %s
                        data status: %s
                        primary phy: %s
                        secondary phy: %s
                        advertising sid: %s
                        tx power: %s
                        periodic adv interval: %s
                        """.trimIndent(),
                    isLegacy,
                    isConnectable,
                    dataStatus,
                    primaryPhy,
                    secondaryPhy,
                    if (advertisingSid == ScanResultInterface.SID_NOT_PRESENT)
                        "not present" else advertisingSid,
                    if (txPower == ScanResultInterface.TX_POWER_NOT_PRESENT)
                        "not present" else txPower,
                    if (periodicAdvertisingInterval
                        == ScanResultInterface.PERIODIC_INTERVAL_NOT_PRESENT)
                        "not present" else periodicAdvertisingInterval
                )
            }
            holder.rssi.text = details
            holder.itemView.setOnClickListener { onClickListener(this) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(android.R.layout.two_line_list_item, parent, false)
            .let { ViewHolder(it) }
}
