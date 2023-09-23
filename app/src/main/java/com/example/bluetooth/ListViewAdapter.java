package com.example.bluetooth;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Model> modellist;
    private ArrayList<Model> arrayList;
    private Toast toast;
    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog progress;
    static BluetoothAdapter myBluetooth = null;
    static BluetoothSocket btSocket = null;
    static boolean isBtConnected = false;
    ThisIsThread isThread = new ThisIsThread();

    Handler handler = new Handler();

    static ArrayList<Float> saved = new ArrayList<>();

    public ListViewAdapter(Context context, List<Model> modellist) {
        mContext = context;
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        this.modellist = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(modellist);
    }

    public class ViewHolder{
        TextView deviceName, status;
        ImageView icon, connection;
    }

    @Override
    public int getCount() {
        return modellist.size();
    }

    @Override
    public Object getItem(int i) {
        return modellist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row, null);
            holder.deviceName = view.findViewById(R.id.name);
            holder.status = view.findViewById(R.id.status);
            holder.icon = view.findViewById(R.id.bluetooth_type);
            holder.connection = view.findViewById(R.id.bluetooth_connection);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
        holder.deviceName.setText(modellist.get(position).getDeviceName());
        holder.status.setText(modellist.get(position).getStatus());
        if (modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                BluetoothClass.Device.PHONE_CELLULAR ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.PHONE_CORDLESS ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.PHONE_ISDN ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.PHONE_SMART ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.PHONE_UNCATEGORIZED) {
            holder.icon.setBackgroundResource(R.drawable.ic_phone_android_black_24dp);

        } else if (modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                BluetoothClass.Device.COMPUTER_WEARABLE ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.COMPUTER_DESKTOP ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.COMPUTER_LAPTOP ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.COMPUTER_UNCATEGORIZED ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.COMPUTER_SERVER ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA) {
            holder.icon.setBackgroundResource(R.drawable.ic_laptop_black_24dp);
        } else if (modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER) {
            holder.icon.setBackgroundResource(R.drawable.ic_headset_black_24dp);
        } else if (modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES ||
                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                        BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE) {
            holder.icon.setBackgroundResource(R.drawable.ic_headset_mic_black_24dp);
        } else {
            holder.icon.setBackgroundResource(R.drawable.ic_bluetooth_black_24dp);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                BluetoothClass.Device.PHONE_CELLULAR        ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.PHONE_CORDLESS        ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.PHONE_ISDN            ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.PHONE_SMART           ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.PHONE_UNCATEGORIZED   ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.COMPUTER_WEARABLE        ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.COMPUTER_DESKTOP          ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.COMPUTER_LAPTOP           ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.COMPUTER_UNCATEGORIZED    ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.COMPUTER_SERVER   ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES ||
                                modellist.get(position).getDevice().getBluetoothClass().getDeviceClass() ==
                                        BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE
                                        && !isIsBtConnected()) {
                    showAlert(mContext.getString(R.string.error_connect));
                } else if (!isIsBtConnected()) {
                    if (modellist.get(position).isTypePaired()) {
                        unPairDevice(modellist.get(position).getDevice(), holder.status, holder.connection);
                    } else {
                        pairDevice(modellist.get(position).getDevice(), holder.status, holder.connection);
                    }
                } else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Disconnect device")
                            .setMessage("Do you want to disconnect the device connected to this mobile phone!")
                            .setPositiveButton("Disconnect", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        btSocket.close();
                                        isBtConnected = false;
                                        mContext.startActivity(new Intent(mContext, MainActivity.class));
                                    } catch (Exception e) {}
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .create().show();

                }
            }
        });
        return view;
    }

    private void showToast(String text) {
        toast.setText(text);
        toast.show();
    }

    private void pairDevice(BluetoothDevice device, TextView textView, ImageView imageView) {
        try {
            ConnectBT connectBT = new ConnectBT(device.getName(), textView, imageView);
            connectBT.execute(device);
        } catch (Exception e) {
            showAlert("Can't Connect \"" + device.getName() + "\"");
        }
    }

    private void unPairDevice(BluetoothDevice device, TextView textView, ImageView imageView) {
        try {
            ConnectBT connectBT = new ConnectBT(device.getName(), textView, imageView);
            connectBT.execute(device);
        } catch (Exception e) {
            showAlert("Can't Connect \"" + device.getName() + "\"");
        }
    }

    public void showAlert(String error) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Connection Error")
                .setMessage(Html.fromHtml(error))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create().show();
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectBT extends AsyncTask<BluetoothDevice, Void, String> {
        private boolean ConnectSuccess = true;
        String nameD;
        String macAddres;
        TextView textView;
        ImageView imageView;
        String errorIs;
        public ConnectBT(String n, TextView textView, ImageView imageView) {
            nameD = n;
            this.textView = textView;
            this.imageView = imageView;
        }

        @SuppressLint("HardwareIds")
        @Override
        protected String doInBackground(BluetoothDevice... bluetoothDevices) {
            try {
                errorIs = "Bluetooth device is connected!";
                ConnectSuccess = false;
                if (!isIsBtConnected()) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(bluetoothDevices[0].getAddress());
                    macAddres = myBluetooth.getAddress();
                    btSocket = dispositivo.createRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();


                    btSocket.connect();
                    isBtConnected = true;
                    ConnectSuccess = true;
                }
            } catch (IOException e) {
                ConnectSuccess = false;
                errorIs = e.getMessage();
            }
            return errorIs;
        }

        @Override
        protected void onPreExecute() {
            if (nameD.equals("") || nameD == null) {
                nameD = macAddres;
            }
            progress = ProgressDialog.show(mContext, "",
                    "Connecting \"" + nameD + "\"...", true, false);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (!ConnectSuccess) {
                showAlert("Can't connect to " + nameD + "!\n" + result);
            } else {
                showToast("Bluetooth connected");
                imageView.setBackgroundResource(R.drawable.ic_check_black_24dp);
                if (!isThread.isAlive()) {
                    isThread.interrupt();
                    isThread.start();
                }
            }
        }
    }

    public class ThisIsThread extends Thread {

        @Override
        public void run() {
            int precise = 0;
            while (true) {
                try {
                    InputStream inputStream = btSocket.getInputStream();
                    int byteCount = inputStream.available();
                    if (byteCount > 0) {
                        byte[] rawBytes = new byte[byteCount];
                        inputStream.read(rawBytes);
                        final String str = new String(rawBytes);
//                        handler.post(new Runnable() {
//                            public void run() {
                        String[] strings = str.replace("  ", " ").
                                replace("\n", " ")
                                .replace("\t", " ")
                                .split(" ");
                        try {
//                            for (String string : strings) {
//                                if (i != 0 && i != strings.length - 1)
//                                saved.add(Float.parseFloat(string));
//                                Log.d("PR4", "," + string);
//
//                            }
                            for (int i = 0; i < strings.length; i++)
                                if (i != 0 && i != strings.length - 1)
                                    saved.add(Float.parseFloat(strings[i]));

                            if (saved.size() > 12000) {
                                saved.clear();
                                Log.d("PR1", "saved cleared");
                            }
                        } catch (Exception i) {
                            Log.d("PR3-", "handleMessage: " + i.getMessage());
                        }
//                            }
//                        });
                    }

                } catch (IOException e) {
                    precise++;
                    if (precise >= 10)
                        isBtConnected = false;
                }
            }
        }
    }

    public static boolean isIsBtConnected() {
        boolean returning = false;
        try {
            returning = btSocket.isConnected()  && isBtConnected;
        } catch (Exception e) {
            isBtConnected = false;
        }
        return returning;
    }
}
