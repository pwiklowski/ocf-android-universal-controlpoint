package ocfcontrolpoint.wiklosoft.iotcontrolpoint;

import android.app.Activity;
import android.content.Context;
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
    TextView value;
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
        if (vi == null)
            vi = mInflater.inflate(R.layout.variable_entry, null);

        name = (TextView) vi.findViewById(R.id.variableName);
        value = (TextView) vi.findViewById(R.id.variableValue);
        final OcfDeviceVariable variable = mDevice.variables().get(position);

        name.setText(variable.getHref());
        //value.setText(Integer.toString(variable.getValue()));


        return vi;
    }
}
