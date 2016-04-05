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
    private native int init();
    public native void searchDevices();


    public OcfControlPoint(){
        init();
    }

    private void deviceFound(String id){
        Log.d(TAG, "deviceFound " + id);


    }
}
