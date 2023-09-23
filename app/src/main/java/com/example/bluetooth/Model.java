package com.example.bluetooth;

import android.bluetooth.BluetoothDevice;

public class Model {
    String deviceName;
    String status;
    BluetoothDevice device;
    boolean typePaired;

    public Model(String deviceName, String status, boolean typePaired, BluetoothDevice device) {
        this.deviceName = deviceName;
        this.status = status;
        this.typePaired = typePaired;
        this.device = device;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getStatus() {
        return this.status;
    }

    public boolean isTypePaired() {
        return typePaired;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}