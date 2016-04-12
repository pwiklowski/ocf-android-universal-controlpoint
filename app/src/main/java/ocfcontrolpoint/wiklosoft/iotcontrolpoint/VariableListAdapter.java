package ocfcontrolpoint.wiklosoft.iotcontrolpoint;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import ocfcontrolpoint.wiklosoft.libocf.OcfDevice;
import ocfcontrolpoint.wiklosoft.libocf.OcfDeviceVariable;
/**
 * Created by pawwik on 29.06.15.
 */
public class VariableListAdapter extends BaseAdapter {
    OcfDevice mDevice;
    LayoutInflater mInflater;
    TextView name;
    Activity mActivity;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        OcfDeviceVariable variable = mDevice.variables().get(position);

        Log.d("VariableListAdapter ", variable.getHref() + " " + variable.getResourceType());
        if (vi == null){
            if (variable.getResourceType().equals("oic.r.light.dimming")) {
                vi = mInflater.inflate(R.layout.resource_light_dimming, null);

            }else if (variable.getResourceType().equals("oic.r.colour.rgb")) {
                vi = mInflater.inflate(R.layout.resource_colour_rgb, null);
            }else{
                vi = mInflater.inflate(R.layout.variable_entry, null);
            }

        }

        name = (TextView) vi.findViewById(R.id.variableName);

        name.setText(variable.getHref());

        return vi;
    }
}
