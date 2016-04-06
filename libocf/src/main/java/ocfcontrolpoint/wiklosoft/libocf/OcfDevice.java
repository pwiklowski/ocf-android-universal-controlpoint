package ocfcontrolpoint.wiklosoft.libocf;

import android.util.Log;

/**
 * Created by Pawel Wiklowski on 05.04.16.
 */
public class OcfDevice {
    private String mName;
    private String mDi;

    public OcfDevice(String name, String di){
        mName = name;
        mDi = di;
    }

    public void test(){
        Log.d("OcfDevice", "Testowa metoda dziala");
    }

}
