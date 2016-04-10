package ocfcontrolpoint.wiklosoft.libocf;

import android.util.Log;

/**
 * Created by Pawel Wiklowski on 02.04.16.
 */
public class OcfControlPoint {
    private static final String TAG = "OcfControlPoint";

    static {
        System.loadLibrary("test");
    }
    private native void init();
    public native void searchDevices();



    public OcfControlPoint(){
        init();
    }

    private void deviceFound(OcfDevice dev){
        Log.d(TAG, "deviceFound " + dev);
        dev.get(dev.variables().get(0).getHref(), new OcfDeviceVariableCallback() {
            @Override
            public void update(String json) {
                Log.d(TAG, "variable updated" + json);
            }
        });


    }
}
