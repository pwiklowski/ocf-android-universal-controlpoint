package ocfcontrolpoint.wiklosoft.libocf;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

    List<OcfOnDeviceFound> mOcfOnDeviceFoundList = new ArrayList<>();
    List<OcfDevice> mDevices = new ArrayList<>();

    public interface OcfOnDeviceFound{
        void deviceFound(OcfDevice dev);
    }

    public OcfControlPoint(){
        init();
    }

    public void addOnDeviceFoundCallback(OcfOnDeviceFound callback){
        mOcfOnDeviceFoundList.add(callback);
    }

    public void removeOnDeviceFoundCallback(OcfOnDeviceFound callback){
        mOcfOnDeviceFoundList.remove(callback);
    }
    //Do not touch it, method called from JNI
    private void deviceFound(OcfDevice dev){
        Log.d(TAG, "deviceFound " + dev);
        mDevices.add(dev);

        for(int i=0;i<mOcfOnDeviceFoundList.size(); i++){
            mOcfOnDeviceFoundList.get(i).deviceFound(dev);
        }


    }
    public List<OcfDevice> getDevices(){
        return mDevices;
    }
}
