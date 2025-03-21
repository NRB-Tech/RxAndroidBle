package io.nrbtech.rxandroidble.sample.example1_scanning;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.nrbtech.rxandroidble.RxBleDevice;

import io.nrbtech.rxandroidble.scan.ScanResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.nrbtech.rxandroidble.scan.ScanResultInterface;

import java.util.Locale;

class ScanResultsAdapter extends RecyclerView.Adapter<ScanResultsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView line1;
        TextView line2;

        ViewHolder(View itemView) {
            super(itemView);
            line1 = itemView.findViewById(android.R.id.text1);
            line2 = itemView.findViewById(android.R.id.text2);
        }
    }

    interface OnAdapterItemClickListener {

        void onAdapterViewClick(View view);
    }

    private static final Comparator<ScanResult> SORTING_COMPARATOR = (lhs, rhs) ->
            lhs.getBleDevice().getMacAddress().compareTo(rhs.getBleDevice().getMacAddress());
    private final List<ScanResult> data = new ArrayList<>();
    private OnAdapterItemClickListener onAdapterItemClickListener;
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (onAdapterItemClickListener != null) {
                onAdapterItemClickListener.onAdapterViewClick(v);
            }
        }
    };

    void addScanResult(ScanResult bleScanResult) {
        // Not the best way to ensure distinct devices, just for sake on the demo.

        for (int i = 0; i < data.size(); i++) {

            if (data.get(i).getBleDevice().equals(bleScanResult.getBleDevice())) {
                data.set(i, bleScanResult);
                notifyItemChanged(i);
                return;
            }
        }

        data.add(bleScanResult);
        Collections.sort(data, SORTING_COMPARATOR);
        notifyDataSetChanged();
    }

    void clearScanResults() {
        data.clear();
        notifyDataSetChanged();
    }

    private boolean showDetails = false;
    void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
        notifyDataSetChanged();
    }

    ScanResult getItemAtPosition(int childAdapterPosition) {
        return data.get(childAdapterPosition);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ScanResult rxBleScanResult = data.get(position);
        final RxBleDevice bleDevice = rxBleScanResult.getBleDevice();
        holder.line1.setText(String.format(Locale.getDefault(), "%s (%s)", bleDevice.getMacAddress(), bleDevice.getName()));
        String details = String.format(Locale.getDefault(), "RSSI: %d", rxBleScanResult.getRssi());
        if (showDetails) {
            details += String.format(Locale.getDefault(),
                    "\nlegacy: %s"
                            + "\nconnectable: %s"
                            + "\ndata status: %s"
                            + "\nprimary phy: %s"
                            + "\nsecondary phy: %s"
                            + "\nadvertising sid: %s"
                            + "\ntx power: %s"
                            + "\nperiodic adv interval: %s",
                    rxBleScanResult.isLegacy(),
                    rxBleScanResult.isConnectable(),
                    rxBleScanResult.getDataStatus(),
                    rxBleScanResult.getPrimaryPhy(),
                    rxBleScanResult.getSecondaryPhy(),
                    rxBleScanResult.getAdvertisingSid() == ScanResultInterface.SID_NOT_PRESENT
                            ? "not present"
                            : rxBleScanResult.getAdvertisingSid(),
                    rxBleScanResult.getTxPower() == ScanResultInterface.TX_POWER_NOT_PRESENT
                            ? "not present"
                            : rxBleScanResult.getTxPower(),
                    rxBleScanResult.getPeriodicAdvertisingInterval() == ScanResultInterface.PERIODIC_INTERVAL_NOT_PRESENT
                            ? "not present"
                            : rxBleScanResult.getPeriodicAdvertisingInterval()
            );
        }
        holder.line2.setText(details);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.two_line_list_item, parent, false);
        itemView.setOnClickListener(onClickListener);
        return new ViewHolder(itemView);
    }

    void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }
}
