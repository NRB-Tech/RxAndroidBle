package io.nrbtech.rxandroidble.samplekotlin.example1_scanning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.nrbtech.rxandroidble.samplekotlin.example1_scanning.ScanResultsAdapter.ViewHolder
import io.nrbtech.rxandroidble.scan.ScanResult
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
            if (showDetails) {
                holder.rssi.setText(
                    String.format(
                        Locale.getDefault(),
                        "RSSI: %d\n" +
                                "legacy: %s\n" +
                                "connectable: %s\n" +
                                "data status: %d\n" +
                                "primary phy: %d\n" +
                                "secondary phy %d\n" +
                                "advertising sid: %d\n" +
                                "tx power: %d\n" +
                                "periodic adv interval: %d",
                        rssi,
                        isLegacy,
                        isConnectable,
                        dataStatus,
                        primaryPhy,
                        secondaryPhy,
                        advertisingSid,
                        txPower,
                        periodicAdvertisingInterval
                    )
                )
            } else {
                holder.rssi.setText(
                    String.format(
                        Locale.getDefault(),
                        "RSSI: %d",
                        rssi
                    )
                )
            }
            holder.itemView.setOnClickListener { onClickListener(this) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(android.R.layout.two_line_list_item, parent, false)
            .let { ViewHolder(it) }
}
