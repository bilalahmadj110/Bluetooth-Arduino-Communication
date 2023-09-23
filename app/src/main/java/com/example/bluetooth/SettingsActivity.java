package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    RelativeLayout maxMin, graphFreq, graphBackColor, graphLineColor, lineThickness;
    Shared_Preferences sharedPreferences;
    Toast toast;
    TextView maxMinTextView, minMinTextView, graphFreqTextView, colorLine, graphColor, lineThickTextView;

    List<String> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        colorList = Arrays.asList(getResources().getStringArray(R.array.color_list));

        sharedPreferences = new Shared_Preferences(this);

        // Settings/Shared_Preferences text view updates
        maxMinTextView = findViewById(R.id.max_y);
        minMinTextView = findViewById(R.id.min_y);
        graphFreqTextView = findViewById(R.id.freq);
        graphColor = findViewById(R.id.color);
        colorLine = findViewById(R.id.color_line);
        lineThickTextView = findViewById(R.id.thick_line);

        // Relative Layout ...
        maxMin = findViewById(R.id.set_max_min_y);
        graphFreq = findViewById(R.id.set_graph_freq);
        graphBackColor = findViewById(R.id.set_graph_color);
        graphLineColor = findViewById(R.id.set_graph_Line_color);
        lineThickness = findViewById(R.id.set_graph_Line_thick);

        // LISTENERS
        maxMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInput(1);
            }
        });
        graphFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInput(2);
            }
        });
        lineThickness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInput(3);
            }
        });
        graphLineColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(true);
            }
        });
        graphBackColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(false);
            }
        });

        setTitle("Preferences");
        updateValues();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    public void showDialog(final boolean isLine) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setCancelable(true)
                .setTitle("Choose color for " + (isLine ? "line" : "graph"))
                .setSingleChoiceItems(getResources().getStringArray(R.array.color_list),
                        sharedPreferences.getInt(isLine ? "line-color" : "graph-color", isLine ? 1 : 8), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sharedPreferences.updateValue(isLine ? "line-color" : "graph-color", i);
                                updateValues();
                                dialogInterface.dismiss();
                            }
                        })
                .show();
    }

    public void showInput(final int what) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        final TextView textView = view.findViewById(R.id.dialog_text);
        final EditText editText = view.findViewById(R.id.input_dialog);
        final TextView textView2 = view.findViewById(R.id.dialog_text2);
        final EditText editText2 = view.findViewById(R.id.input_dialog2);

        textView2.setText("Enter max value for y-axis");
        if (what == 3) {
            textView2.setVisibility(View.GONE);
            editText2.setVisibility(View.GONE);
            textView.setText("Enter thickness of graph line: (1 - 10 units)");
            editText.setHint("Enter value between 1-10");
            editText.setText(sharedPreferences.getInt("line-thick", sharedPreferences.getLINE_THICKNESS()) + "");
        } else if (what == 2) {
            textView2.setVisibility(View.GONE);
            editText2.setVisibility(View.GONE);
            textView.setText("Enter graph update rate every second: (1 - 200 Hz)");
            editText.setHint("Enter value Hz");
            editText.setText(sharedPreferences.getInt("graph-freq", sharedPreferences.getGRAPH_FREQ()) + "");
        } else {
            textView.setText("Enter min value for y-axis:");
            editText.setHint("Enter lower y value");
            editText2.setHint("Enter upper y value");
            editText.setText(sharedPreferences.getInt("min-y", sharedPreferences.getMIN_Y()) + "");
            editText2.setText(sharedPreferences.getInt("max-y", sharedPreferences.getMAX_Y()) + "");
        }

        new AlertDialog.Builder(SettingsActivity.this)
                .setCancelable(true)
                .setTitle("Enter value")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("   Done   ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (what == 3) {
                            try {
                                if (Integer.parseInt(editText.getText().toString()) > 0 && Integer.parseInt(editText.getText().toString()) < 11) {
                                    sharedPreferences.updateValue("line-thick", Integer.parseInt(editText.getText().toString()));
                                    updateValues();
                                    showToast("Preferences saved successfully");
                                }
                            } catch (Exception e) {
                                showToast("Preferences not saved");
                            }
                        } else if (what == 2) {
                            try {
                                if (Integer.parseInt(editText.getText().toString()) < 201 && Integer.parseInt(editText.getText().toString()) > 0) {
                                    sharedPreferences.updateValue("graph-freq", Integer.parseInt(editText.getText().toString()));
                                    updateValues();
                                    showToast("Preferences saved successfully");
                                } else
                                    showToast("Preferences not saved");
                            } catch (Exception e) {
                                showToast("Preferences not saved");
                            }
                        } else {
                            try {
                                int value1 = Integer.parseInt(editText.getText().toString());
                                int value2 = Integer.parseInt(editText2.getText().toString());
                                if (value2 != 0 && value1 != 0) {
                                    sharedPreferences.updateValue("min-y", value1);
                                    sharedPreferences.updateValue("max-y", value2);
                                    updateValues();
                                    showToast("Preferences saved successfully");
                                } else
                                    showToast("Preferences not saved");
                            } catch (Exception e) {
                                showToast("Preferences not saved");
                            }
                        }
                    }
                })
                .setNegativeButton("Discard", null)
                .show();
    }

    public void showToast(String text) {
        toast.setText(text);
        toast.show();
    }

    @SuppressLint("SetTextI18n")
    public void updateValues() {
        lineThickTextView.setText(sharedPreferences.getInt("line-thick", sharedPreferences.getLINE_THICKNESS()) +
                (sharedPreferences.getInt("line-thick", sharedPreferences.getLINE_THICKNESS()) > 1 ? " units" : " unit"));
        graphFreqTextView.setText(sharedPreferences.getInt("graph-freq", sharedPreferences.getGRAPH_FREQ()) + " Hz");
        minMinTextView.setText("min:  " + sharedPreferences.getInt("min-y", sharedPreferences.getMIN_Y()) + "");
        maxMinTextView.setText("max: " + sharedPreferences.getInt("max-y", sharedPreferences.getMAX_Y()) + "");

        int color1 = sharedPreferences.getInt("graph-color", sharedPreferences.getGRAPH_COLOR());
        int color2 = sharedPreferences.getInt("line-color", sharedPreferences.getLINE_COLOR());

        colorLine.setBackgroundColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(color2))));
        graphColor.setBackgroundColor(getResources().getColor(sharedPreferences.getColorID(colorList.get(color1))));

        Log.d("Tag", "COLOR:: " + color1 + " ... " + color2 + " ... " + colorList.get(color1) + " ... " + colorList.get(color2));
    }

}
