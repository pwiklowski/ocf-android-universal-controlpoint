package com.wiklosoft.ocf.controlpoint;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.wiklosoft.ocf.OcfControlPoint;
import com.wiklosoft.ocf.OcfDevice;
import com.wiklosoft.ocf.OcfDeviceVariable;
import com.wiklosoft.ocf.OcfDeviceVariableCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class VariableListFragment extends Fragment {
    String TAG = "VariableListFragment";
    OcfControlPoint mController = null;
    ImageButton backButton = null;
    ListView mListView = null;
    OcfDevice mDevice = null;
    VariableListAdapter mAdapter = null;

    public static VariableListFragment newInstance(OcfDevice device)
    {
        VariableListFragment fragment = new VariableListFragment();

        fragment.setDevice(device);

        return fragment;
    }

    Map<String, OcfDeviceVariableCallback> mCallbackMap = new HashMap<>();


    public void updateVariable(final String json, final OcfDeviceVariable variable) {
        Activity a = getActivity();
        if (a != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject value = new JSONObject(json);

                        if (variable.getResourceType().contains("oic.r.light.dimming")){
                            String v = value.getString("dimmingSetting");
                            variable.setValue(v);
                        }

                        if (variable.getResourceType().contains("oic.r.colour.rgb")){
                            String v = value.getString("dimmingSetting");
                            variable.setValue(v);
                        }


                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setDevice(OcfDevice  device)
    {
        mDevice = device;
    }

    public VariableListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mController = ApplicationContext.getOcfControlPoint();

        if (mListView != null && mController != null && mDevice != null) {
            mAdapter = new VariableListAdapter(getActivity(), mDevice);
            if (mListView != null)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setAdapter(mAdapter);
                    }
                });
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null){

            //restore device
        }


        View root  = inflater.inflate(R.layout.fragment_variable_list, container, false);

        mListView =(ListView) root.findViewById(R.id.variableList);

        backButton = (ImageButton) root.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        for (int i=0; i<mDevice.variables().size(); i++){
            final OcfDeviceVariable v = mDevice.variables().get(i);

            String href = v.getHref();

            OcfDeviceVariableCallback callback = new OcfDeviceVariableCallback() {
                @Override
                public void update(final String json) {
                    updateVariable(json, v);
                }
            };
            mCallbackMap.put(href, callback);
            mDevice.observe(href, callback);
        }
    }

    @Override
    public void onPause()
    {
        for( String key: mCallbackMap.keySet()){
            mDevice.unobserve(key, mCallbackMap.get(key));
        }
        super.onPause();

    }
}
