package io.nrbtech.rxandroidble.sample.example3_discovery;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;

import io.nrbtech.rxandroidble.RxBleConnection;
import io.nrbtech.rxandroidble.RxBleDevice;
import io.nrbtech.rxandroidble.sample.DeviceActivity;
import io.nrbtech.rxandroidble.sample.R;
import io.nrbtech.rxandroidble.sample.SampleApplication;
import io.nrbtech.rxandroidble.sample.databinding.ActivityExample3Binding;
import io.nrbtech.rxandroidble.sample.example4_characteristic.CharacteristicOperationExampleActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class ServiceDiscoveryExampleActivity extends AppCompatActivity {

    private ActivityExample3Binding binding;
    private DiscoveryResultsAdapter adapter;
    private RxBleDevice bleDevice;
    private String macAddress;
    private final CompositeDisposable servicesDisposable = new CompositeDisposable();

    public void onConnectToggleClick() {
        final Disposable disposable = bleDevice.establishConnection(false)
                .flatMapSingle(RxBleConnection::discoverServices)
                .take(1) // Disconnect automatically after discovery
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::updateUI)
                .subscribe(adapter::swapScanResult, this::onConnectionFailure);
        servicesDisposable.add(disposable);

        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExample3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        macAddress = getIntent().getStringExtra(DeviceActivity.EXTRA_MAC_ADDRESS);
        //noinspection ConstantConditions
        getSupportActionBar().setSubtitle(getString(R.string.mac_address, macAddress));
        bleDevice = SampleApplication.getRxBleClient(this).getBleDevice(macAddress);

        // Set up click listener
        binding.connect.setOnClickListener(v -> onConnectToggleClick());

        configureResultList();
    }

    private void configureResultList() {
        binding.scanResults.setHasFixedSize(true);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        binding.scanResults.setLayoutManager(recyclerLayoutManager);
        adapter = new DiscoveryResultsAdapter();
        binding.scanResults.setAdapter(adapter);
        adapter.setOnAdapterItemClickListener(view -> {
            final int childAdapterPosition = binding.scanResults.getChildAdapterPosition(view);
            final DiscoveryResultsAdapter.AdapterItem itemAtPosition = adapter.getItem(childAdapterPosition);
            onAdapterItemClick(itemAtPosition);
        });
    }

    private void onAdapterItemClick(DiscoveryResultsAdapter.AdapterItem item) {

        if (item.type == DiscoveryResultsAdapter.AdapterItem.CHARACTERISTIC) {
            final Intent intent = CharacteristicOperationExampleActivity.startActivityIntent(this, macAddress, item.uuid);
            // If you want to check the alternative advanced implementation comment out the line above and uncomment one below
//            final Intent intent = AdvancedCharacteristicOperationExampleActivity.startActivityIntent(this, macAddress, item.uuid);
            startActivity(intent);
        } else {
            //noinspection ConstantConditions
            Snackbar.make(findViewById(android.R.id.content), R.string.not_clickable, Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    private void updateUI() {
        binding.connect.setEnabled(!isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();
        servicesDisposable.clear();
    }
}
