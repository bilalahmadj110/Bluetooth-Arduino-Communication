package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

public class Connect extends AppCompatActivity {
    ScrollView scrollView1;
    TextView empty1, empty2;
    ProgressBar paired, available;
    Switch bluetoothOnOff;
    Button scanDevices;
    ListView pairedDevicesList, availableDevicesList;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayList<Model> pairedList = new ArrayList<>();
    ArrayList<Model> scanList = new ArrayList<>();
    ListViewAdapter adapter;
    Toast toast;
    boolean letBluetoothScan;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        pairedDevices = mBluetoothAdapter.getBondedDevices();
                        bluetoothOnOff.setText("Turn off Bluetooth");
                        bluetoothOnOff.setChecked(true);
                        if (letBluetoothScan) {
                            letBluetoothScan = false;
                            mBluetoothAdapter.startDiscovery();
                        }
                        paired.setVisibility(ProgressBar.INVISIBLE);
                        refreshPairedDevices();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        try {
                            ListViewAdapter.isBtConnected = false;
                        } catch (Exception e) {

                        }
                        empty1.setText("Turn on Bluetooth to show paired devices");
                        empty1.setVisibility(View.VISIBLE);
                        empty2.setVisibility(View.VISIBLE);
                        scanList.clear();
                        bluetoothOnOff.setText("Turn on Bluetooth");
                        bluetoothOnOff.setChecked(false);
                        paired.setVisibility(ProgressBar.INVISIBLE);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        availableDevicesList.setLayoutParams(lp);
                        pairedDevicesList.setLayoutParams(lp);
                        clear();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        showToast("Turning on bluetooth...");
                        paired.setVisibility(ProgressBar.VISIBLE);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        paired.setVisibility(ProgressBar.VISIBLE);
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        showToast("Device Connected");
                        break;

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                showToast("Scanning...");
                scanDevices.setText("Cancel Bluetooth Scanning");
                scanDevices.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp),
                        null, null);
//                scanDevices.drawab
                available.setVisibility(View.VISIBLE);
                empty2.setVisibility(View.GONE);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                scanList.add(new Model(device.getName(), device.getAddress(), false, device));
//                device.getBluetoothClass() == BluetoothClass.Device.
                if (scanList.size() == 0)
                    empty2.setVisibility(View.GONE);
                refreshDevices();
                scrollView1.fullScroll(View.FOCUS_DOWN);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                scanDevices.setEnabled(true);
                scanDevices.setText("Scan Bluetooth Devices");
                scanDevices.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.ic_search_black_24dp),
                        null, null);
                available.setVisibility(View.INVISIBLE);
                scrollView1.fullScroll(View.FOCUS_DOWN);
            }
        }
    };

    @SuppressLint({"ShowToast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        scrollView1 = findViewById(R.id.scroller_for_main);

        setTitle("Connect Device");
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        empty1 = findViewById(R.id.empty1);
        empty2 = findViewById(R.id.empty2);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        paired = findViewById(R.id.paired_gif);
        available = findViewById(R.id.available_gif);
        pairedDevicesList = findViewById(R.id.paired_devices);

        availableDevicesList = findViewById(R.id.available_devices);
        scanDevices = findViewById(R.id.scan_bluetooth);
        bluetoothOnOff = findViewById(R.id.bluetooth_on_off);

        if (mBluetoothAdapter != null) {
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);

            bluetoothOnOff.setOnCheckedChangeListener(null);
            bluetoothOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!mBluetoothAdapter.isEnabled() && isChecked) {
                        mBluetoothAdapter.enable();
                    } else if (!isChecked && mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                    }

                    if (isChecked && !mBluetoothAdapter.isEnabled())
                        bluetoothOnOff.setChecked(false);
                    else if (!isChecked && mBluetoothAdapter.isEnabled())
                        bluetoothOnOff.setChecked(true);
                }
            });
            scanDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (scanDevices.getText().toString().equals("Scan Bluetooth Devices")) {
                        scanList.clear();
                        if (mBluetoothAdapter.isEnabled())
                            mBluetoothAdapter.startDiscovery();
                        else {
                            showAlert();
                        }
                    } else if (scanDevices.getText().toString().equals("Cancel Bluetooth Scanning")) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                }
            });
            if (mBluetoothAdapter.isEnabled()) {
                refreshPairedDevices();
                bluetoothOnOff.setText("Turn off Bluetooth");
                empty2.setText("Tap \"Scan Bluetooth Devices\" to show nearby devices here");
            } else if (!mBluetoothAdapter.isEnabled()) {
                bluetoothOnOff.setText("Turn on Bluetooth");
                empty2.setText("Tap \"Scan Bluetooth Devices\" to show nearby devices here");
                empty1.setText("Turn on Bluetooth to show paired devices");
            }
        } else {
            paired.setVisibility(View.VISIBLE);
            available.setVisibility(View.VISIBLE);
            showToast("Your device doesn't support bluetooth! This app may misbehave while using.");
        }
    }

    public void clear() {
        pairedList.clear();
        adapter = new ListViewAdapter(this, pairedList);
        pairedDevicesList.setAdapter(adapter);
    }

    public void refreshDevices() {
        LinearLayout.LayoutParams lp = null;
        adapter = new ListViewAdapter(this, scanList);
        availableDevicesList.setAdapter(adapter);
        if (scanList.size() == 0) {
            empty2.setText("No nearby devices in range");
            empty2.setVisibility(View.VISIBLE);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
        } else {
            empty2.setText("Tap \"Scan Bluetooth Devices\" to show nearby devices here");
            empty2.setVisibility(View.GONE);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    (scanList.size() * 117) + 2);
        }
        availableDevicesList.setLayoutParams(lp);
        scrollView1.fullScroll(View.FOCUS_DOWN);
    }

    public void refreshPairedDevices() {
        pairedList.clear();
        for (BluetoothDevice bt : pairedDevices)
            pairedList.add(new Model(bt.getName(), bt.getAddress(), true, bt));
        if (pairedList.size() != 0) {
            empty1.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    (pairedList.size() * 117) + 2);
            pairedDevicesList.setLayoutParams(lp);
        } else {
            empty1.setVisibility(View.VISIBLE);
            empty1.setText("No paired device to show!");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            pairedDevicesList.setLayoutParams(lp);
        }
        adapter = new ListViewAdapter(this, pairedList);
        pairedDevicesList.setAdapter(adapter);
    }

    public void showToast(String text) {
        toast.setText(text);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(Connect.this, MainActivity.class));
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    protected void onResume() {
        bluetoothOnOff.setChecked(mBluetoothAdapter.isEnabled());
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }
    }

    public void showAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Scanning Error")
                .setMessage("You must have your Bluetooth on in order to scan nearby available devices!")
                .setPositiveButton("Turn ON", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        letBluetoothScan = true;
                        bluetoothOnOff.performClick();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }
}
