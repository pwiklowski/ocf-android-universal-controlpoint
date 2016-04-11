package ocfcontrolpoint.wiklosoft.iotcontrolpoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ocfcontrolpoint.wiklosoft.libocf.OcfDevice;

/**
 * Created by pawwik on 29.06.15.
 */
public class DeviceListAdapter extends BaseAdapter {
    List<OcfDevice> mList;
    LayoutInflater mInflater;

    DeviceListAdapter(Context context, List<OcfDevice> list)
    {
        mList = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public OcfDevice getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = mInflater.inflate(R.layout.device_entry, null);

        TextView name = (TextView) vi.findViewById(R.id.deviceName);
        TextView id = (TextView) vi.findViewById(R.id.deviceId);

        OcfDevice device = mList.get(position);

        name.setText(device.getName());
        id.setText(device.getDi());


        return vi;
    }
}
