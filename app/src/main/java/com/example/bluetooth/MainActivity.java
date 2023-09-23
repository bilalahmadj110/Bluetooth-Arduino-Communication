package com.example.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static LineGraphSeries<DataPoint> series;
    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bluetooth Logger/";
    String fileName = "log.txt";
    GraphView graph;
    Shared_Preferences sharedPreferences;
    List<String> colorList;
    boolean isPlay = false;
    Toast toast;
    ArrayList<Float> saved = new ArrayList<>();
    int maxY, minY, thickness, freq, lineColor, backColor;
    boolean isConnected = false;
    private Handler handler = new Handler();
    private float lastXPoint = 0;
    private Menu menu;

    int realTimeMinY, realTimeMaxY;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString("msg");
            if (string.equals("no")) {
                graph.setKeepScreenOn(false);
                menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_bluetooth_white_24dp));
                menu.getItem(1).setTitle("Connect bluetooth");
                isConnected = false;
                Log.d("PR2", "Bluetooth:: Connected: false, isPlay=" + isPlay);
                try {
                    stop();
                } catch (Exception e) {
                }
            } else {
                graph.setKeepScreenOn(true);
                menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_bluetooth_connected_white_24dp));
                isConnected = true;
                save();
                menu.getItem(1).setTitle("Disconnect bluetooth");
                Log.d("PR2", "Bluetooth:: true Connected: true, isPlay=" + isPlay);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realTimeMaxY = Integer.MAX_VALUE;
        realTimeMinY = Integer.MIN_VALUE;

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        colorList = Arrays.asList(getResources().getStringArray(R.array.color_list));

        sharedPreferences = new Shared_Preferences(this);
        sharedPreferences.clearStrings();
        getPref();

        graph = findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
        });


        graph.addSeries(series);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);



        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxY(maxY);
        graph.getViewport().setMinY(-minY);
        graph.getViewport().setYAxisBoundsManual(true);


        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);

        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time ->");
//        gridLabel.setHorizontalLabelsVisible(false);
        gridLabel.setHorizontalAxisTitleColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(lineColor))));
        gridLabel.setVerticalAxisTitleColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(lineColor))));
        gridLabel.setHighlightZeroLines(true);
        gridLabel.setHumanRounding(true, true);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMinimumIntegerDigits(0);
        nf.setMaximumIntegerDigits(Integer.MAX_VALUE);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
//        gridLabel.setNumVerticalLabels(5);

        gridLabel.setGridColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(lineColor))));
        series.setColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(lineColor))));
        series.setThickness(thickness);
        graph.setBackgroundColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(backColor))));
        graph.getGridLabelRenderer().setHighlightZeroLines(true);
        graph.getGridLabelRenderer().setGridColor(sharedPreferences.getColorID(colorList.get(lineColor)));
        graph.setTitleColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(lineColor))));
        graph.getViewport().setBorderColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(lineColor))));
        graph.getLegendRenderer().setTextColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(lineColor))));

        graph.getViewport().scrollToEnd();

        try {
            CheckConnection checkConnection = new CheckConnection();
            checkConnection.start();
        } catch (Exception ignored) { }

    }

    public void addRandomData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPlay) {
                    try {

                        ArrayList<Float> newArray = new ArrayList<>(ListViewAdapter.saved);
                        int size = newArray.size();
                        if (size > 0) {
                            ListViewAdapter.saved.clear();
                            float newX = (float) (1. / freq) / size;
                            Log.d("PR3", "DATA:: lastX=" + lastXPoint + " ... newX=" + newX + " ... size: " + size);
                            for (int i = 0; i < size; i++) {
                                lastXPoint = lastXPoint + newX;
                                float a = newArray.get(i);
                                saved.addAll(newArray);
                                series.appendData(new DataPoint(lastXPoint, a), true, 2000);
                                Log.d("PR3", "DATA:: loop: lastX=" + lastXPoint + " ... newX=" + newX + " ... index=" + a);
                            }
//                            graph.getViewport().scrollToEnd();
                        } else {
//                            lastXPoint += (float) (1. / freq);
//                            series.appendData(new DataPoint(lastXPoint, 0), true, 1000);
                            Log.d("PR3", "DATA:: lastX=" + lastXPoint);
                        }
                    } catch (Exception ignored) { }
                    addRandomData();
                }
            }
        }, 1000 / freq);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d("PR1", "ONcreate:: ");
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (isPlay) {
            menu.getItem(0).setTitle("Stop");
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
        } else {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
            menu.getItem(0).setTitle("Play");
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            finishAffinity();
        else
            ActivityCompat.finishAffinity(MainActivity.this);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_connect_bluetooth:
                if (item.getTitle().equals("Disconnect bluetooth"))
                    showAlert();
                else {
                    startActivity(new Intent(MainActivity.this, Connect.class));
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                return true;
            case R.id.action_play_stop:
                if (menu.getItem(0).getTitle().equals("Play")) {
                    if (!isPlay && isConnected)
                        play();
                    else
                        showAlert2();
                    Log.d("PR1", "ACTION PLAY" + isConnected + "; isPlay:: " + isPlay);
                } else {
                    isPlay = false;
                    Log.d("PR1", "ACTOION STOP " + isConnected + "; isPlay:: " + isPlay);
                    stop();
                }
                return true;
            case R.id.action_export:
                if (saved.size() > 0 || sharedPreferences.getInt("last", 0) != 0)
                    showInput();
                else
                    showToast("No data available for export");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void play() {
        isPlay = true;
        menu.getItem(0).setTitle("Stop");
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
        addRandomData();
    }

    public void stop() {
        ListViewAdapter.saved.clear();
        menu.getItem(0).setTitle("Play");
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
    }

    public void saveToFile(final String fileName) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    new File(path).mkdir();
                    File file = new File(path + fileName);
                    if (!file.exists()) {
                        boolean a = file.createNewFile();
                    } else {
                        boolean a = file.delete();
                        a = file.createNewFile();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    StringBuilder stringBuilder = new StringBuilder();
                    int last = sharedPreferences.getInt("last", 0);
                    int size = saved.size();
                    if (last > 0) {
                        for (int i = 0; i < last; i++) {
                            String got = sharedPreferences.getString((i + 1) + "", "").replace(";", "\n");
                            stringBuilder.append(got).append("\n\n");
                        }
                        sharedPreferences.clearStrings();
                    }
                    if (size > 0) {
                        for (float val : saved) {
                            stringBuilder.append(val).append("\n");
                        }
                        saved.clear();
                    }
                    fileOutputStream.write((stringBuilder.toString() + System.getProperty("line.separator")).getBytes());
                    showToast("File \"" + fileName + "\" exported successfully");
                } catch (FileNotFoundException ignored) { } catch (IOException ignored) { }
            }
        }, 100);
    }

    public void showInput() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        final TextView textView = view.findViewById(R.id.dialog_text);
        final EditText editText = view.findViewById(R.id.input_dialog);
        final TextView textView2 = view.findViewById(R.id.dialog_text2);
        final EditText editText2 = view.findViewById(R.id.input_dialog2);

        textView2.setVisibility(View.GONE);
        editText2.setVisibility(View.GONE);
        textView.setText("Enter file name to export into internal storage:\nNote: It will overwrite any existing file if found with same name!");
        editText.setHint("Enter file name...");
        editText.setMaxLines(1);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setText("log");

        new android.app.AlertDialog.Builder(MainActivity.this)
                .setCancelable(true)
                .setTitle("Enter value")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("   Done   ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String t = editText.getText().toString();
                            if (TextUtils.isEmpty(t))
                                throw new Exception();
                            if (!t.contains(".txt"))
                                t += ".txt";
                            fileName = t;
                            if (isPermissionGivenForStorage())
                                saveToFile(t);
                            else
                                requestPermissionForStorage();
                        } catch (Exception e) {
                            showToast("Filename invalid");
                        }
                    }
                })
                .setNegativeButton("Discard", null)
                .show();
    }

    public void save() {
        if (saved.size() > 50000) {
            int last = sharedPreferences.getInt("last", 0);
            sharedPreferences.updateValue("" + (last + 1), TextUtils.join(";", saved));
            sharedPreferences.updateValue("last", last + 1);
            saved.clear();
            Log.d("PR1", "List cleared");
        }
    }

    public void showAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Disconnect Device")
                .setMessage("Do you really want to disconnect bluetooth device?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ListViewAdapter.btSocket.close();
                            isConnected = false;
                        } catch (Exception e) {
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    public void showAlert2() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Please connect to bluetooth device to start showing graph.")
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this, Connect.class));
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    public void getPref() {
        thickness = sharedPreferences.getInt("line-thick", sharedPreferences.getLINE_THICKNESS());
        freq = sharedPreferences.getInt("graph-freq", sharedPreferences.getGRAPH_FREQ());
        minY = sharedPreferences.getInt("min-y", sharedPreferences.getMIN_Y());
        maxY = sharedPreferences.getInt("max-y", sharedPreferences.getMAX_Y());
        backColor = sharedPreferences.getInt("graph-color", sharedPreferences.getGRAPH_COLOR());
        lineColor = sharedPreferences.getInt("line-color", sharedPreferences.getLINE_COLOR());
    }

    public boolean isPermissionGivenForStorage() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionForStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToFile(fileName);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    retryAlert();
                else
                    settingAlert();
        }
    }

    public void showToast(String text) {
        toast.setText(text);
        toast.show();
    }

    public void settingAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Permission Disabled")
                .setMessage("We're unable to request permission for accessing storage, maybe you've disabled permission." +
                        " Please go to Settings then Permission Section and allow app to access storage for the smooth flow of application.")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Deny", null)
                .create().show();
    }

    public void retryAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Allow Permission")
                .setMessage("We need to access external storage for exporting logger file. Kindly allow app to access storage.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isPermissionGivenForStorage())
                            requestPermissionForStorage();
                    }
                })
                .setNegativeButton("Deny", null)
                .create().show();
    }

    public class CheckConnection extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                try {
                    bundle.putString("msg", (ListViewAdapter.isIsBtConnected() && BluetoothAdapter.getDefaultAdapter().isEnabled() ? "yes" : "no"));
                } catch (Exception e) {
                    bundle.putString("msg", "no");
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }
    }
}