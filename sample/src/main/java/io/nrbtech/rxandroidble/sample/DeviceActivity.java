package io.nrbtech.rxandroidble.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.nrbtech.rxandroidble.sample.databinding.ActivityDeviceBinding;
import io.nrbtech.rxandroidble.sample.example2_connection.ConnectionExampleActivity;
import io.nrbtech.rxandroidble.sample.example3_discovery.ServiceDiscoveryExampleActivity;

public class DeviceActivity extends AppCompatActivity {

    public static final String EXTRA_MAC_ADDRESS = "extra_mac_address";
    private String macAddress;
    private ActivityDeviceBinding binding;

    public void onConnectClick() {
        final Intent intent = new Intent(this, ConnectionExampleActivity.class);
        intent.putExtra(EXTRA_MAC_ADDRESS, macAddress);
        startActivity(intent);
    }

    public void onDiscoveryClick() {
        final Intent intent = new Intent(this, ServiceDiscoveryExampleActivity.class);
        intent.putExtra(EXTRA_MAC_ADDRESS, macAddress);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up click listeners
        binding.connect.setOnClickListener(v -> onConnectClick());
        binding.discovery.setOnClickListener(v -> onDiscoveryClick());

        macAddress = getIntent().getStringExtra(EXTRA_MAC_ADDRESS);
        //noinspection ConstantConditions
        getSupportActionBar().setSubtitle(getString(R.string.mac_address, macAddress));
    }
}
