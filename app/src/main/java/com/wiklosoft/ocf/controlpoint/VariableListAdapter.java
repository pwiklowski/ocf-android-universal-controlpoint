package com.wiklosoft.ocf.controlpoint;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.wiklosoft.ocf.OcfDevice;
import com.wiklosoft.ocf.OcfDeviceVariable;
import com.wiklosoft.ocf.OcfDeviceVariableCallback;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by pawwik on 29.06.15.
 */
public class VariableListAdapter extends BaseAdapter {
    OcfDevice mDevice;
    LayoutInflater mInflater;
    TextView name;
    Activity mActivity;
    private String TAG = "VariableListAdapter";

    VariableListAdapter(Activity context, OcfDevice device)
    {
        mActivity = context;
        mDevice = device;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDevice.variables().size();
    }

    @Override
    public OcfDeviceVariable getItem(int position) {
        return mDevice.variables().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View vi, ViewGroup parent) {
        final OcfDeviceVariable variable = mDevice.variables().get(position);
        JSONObject value = variable.getValue();

        if (variable.getResourceType().equals("oic.r.light.dimming")) {
            vi = mInflater.inflate(R.layout.resource_light_dimming, null);
            SeekBar var = (SeekBar) vi.findViewById(R.id.variableValueSeekBar);

            Log.d(TAG, "Variable value" + variable.getHref() + value);
            if (value != null) {
                try {
                    int v = value.getInt("dimmingSetting");

                    if (value.has("range")) {
                        String[] range = value.getString("range").split(",");
                        if (range.length == 2) {
                            var.setMax(Integer.valueOf(range[1]));
                        }
                    }
                    var.setProgress(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            var.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int value = seekBar.getProgress();
                    String v = "{\"rt\": \"oic.r.light.dimming\",\n" +
                            "    \"dimmingSetting\": " + Integer.toString(value) + ",\n" +
                            "    \"range\": \"0,255\"}";

                    mDevice.post(variable.getHref(), v, new OcfDeviceVariableCallback() {
                        @Override
                        public void update(String json) {
                            Log.d("VariableListAdapter", "value set");
                        }
                    });

                }
            });
        }else if (variable.getResourceType().equals("oic.r.switch.binary")) {
            vi = mInflater.inflate(R.layout.resource_binnary, null);

            Switch sw = (Switch) vi.findViewById(R.id.switchButton);

            if (value != null) {
                boolean checked = false;
                try {
                    checked = value.getBoolean("value");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sw.setChecked(checked);
            }
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String v = "{\"value\": "+b+"}";

                    mDevice.post(variable.getHref(), v, new OcfDeviceVariableCallback() {
                        @Override
                        public void update(String json) {
                            Log.d("VariableListAdapter", "value set");
                        }
                    });
                }
            });


        }else if (variable.getResourceType().equals("oic.r.colour.rgb")) {
            vi = mInflater.inflate(R.layout.resource_colour_rgb, null);
            final SeekBar red = (SeekBar) vi.findViewById(R.id.red);
            final SeekBar green = (SeekBar) vi.findViewById(R.id.green);
            final SeekBar blue = (SeekBar) vi.findViewById(R.id.blue);

            red.setMax(255);
            green.setMax(255);
            blue.setMax(255);

            Log.d(TAG, "Variable value" + variable.getHref() + value  );

            if (value != null) {
                String[] colors = new String[0];
                try {
                    colors = value.getString("dimmingSetting").split(",");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (colors.length == 3) {
                    red.setProgress(Integer.valueOf(colors[0]));
                    green.setProgress(Integer.valueOf(colors[1]));
                    blue.setProgress(Integer.valueOf(colors[2]));
                }
            }


            red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    String v = "{\"rt\": \"oic.r.colour.rgb\",\n" +
                            "    \"dimmingSetting\": \"" + red.getProgress()+ "," +green.getProgress()+"," +blue.getProgress()+ "\"\n" +
                            "    }";

                    mDevice.post(variable.getHref(), v, new OcfDeviceVariableCallback() {
                        @Override
                        public void update(String json) {
                            Log.d("VariableListAdapter", "value set");
                        }
                    });

                }
            });
            green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    String v = "{\"rt\": \"oic.r.colour.rgb\",\n" +
                            "    \"dimmingSetting\": \"" + red.getProgress()+ "," +green.getProgress()+"," +blue.getProgress()+ "\"\n" +
                            "    }";

                    mDevice.post(variable.getHref(), v, new OcfDeviceVariableCallback() {
                        @Override
                        public void update(String json) {
                            Log.d("VariableListAdapter", "value set");
                        }
                    });

                }
            });
            blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    String v = "{\"rt\": \"oic.r.colour.rgb\",\n" +
                            "    \"dimmingSetting\": \"" + red.getProgress()+ "," +green.getProgress()+"," +blue.getProgress()+ "\"\n" +
                            "    }";

                    mDevice.post(variable.getHref(), v, new OcfDeviceVariableCallback() {
                        @Override
                        public void update(String json) {
                            Log.d("VariableListAdapter", "value set");
                        }
                    });
                }
            });
        }else{
            vi = mInflater.inflate(R.layout.variable_entry, null);
        }


        name = (TextView) vi.findViewById(R.id.variableName);
        name.setText(variable.getHref());




        return vi;
    }
}
