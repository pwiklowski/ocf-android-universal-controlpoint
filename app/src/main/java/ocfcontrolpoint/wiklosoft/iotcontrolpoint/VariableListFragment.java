package ocfcontrolpoint.wiklosoft.iotcontrolpoint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ocfcontrolpoint.wiklosoft.libocf.OcfControlPoint;
import ocfcontrolpoint.wiklosoft.libocf.OcfDevice;


public class VariableListFragment extends Fragment {
    OcfControlPoint mController = null;
    ImageButton backButton = null;
    ListView mListView = null;
    OcfDevice mDevice = null;
    public static VariableListFragment newInstance(OcfDevice device)
    {
        VariableListFragment fragment = new VariableListFragment();

        fragment.setDevice(device);

        return fragment;
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
            final VariableListAdapter adapter = new VariableListAdapter(getActivity(), mDevice);
            if (mListView != null)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setAdapter(adapter);
                    }
                });
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onStop()
    {
        super.onStop();

    }
}
