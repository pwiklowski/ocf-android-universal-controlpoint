package ocfcontrolpoint.wiklosoft.iotcontrolpoint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ocfcontrolpoint.wiklosoft.libocf.OcfControlPoint;
import ocfcontrolpoint.wiklosoft.libocf.OcfDevice;


public class DeviceListFragment extends Fragment {
    ListView mListView = null;
    List<OcfDevice> mDevices = new ArrayList<>();
    OcfControlPoint mControlPoint = ApplicationContext.getOcfControlPoint();
    ImageButton mSearchButton = null;

    OcfControlPoint.OcfOnDeviceFound deviceFoundCallback = new OcfControlPoint.OcfOnDeviceFound() {
        @Override
        public void deviceFound(OcfDevice dev) {
            mDevices.add(dev);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mListView != null)
                        mListView.setAdapter(new DeviceListAdapter(getContext(), mDevices));
                }
            });
        }
    };

    public DeviceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle instance){
        super.onCreate(instance);
        mControlPoint.addOnDeviceFoundCallback(deviceFoundCallback);
        mControlPoint.searchDevices();
    }

    @Override
    public void onStop(){
        super.onStop();
        mControlPoint.removeOnDeviceFoundCallback(deviceFoundCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.fragment_device_list2, container, false);
        mSearchButton = (ImageButton) root.findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mControlPoint.searchDevices();
            }
        });

        mListView =(ListView) root.findViewById(R.id.deviceList);


        mListView.setAdapter(new DeviceListAdapter(getContext(), mDevices));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OcfDevice device = mDevices.get(i);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, VariableListFragment.newInstance(device)).addToBackStack("")
                        .commit();
            }
        });
        return root;
    }

}
