package ocfcontrolpoint.wiklosoft.libocf;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawel Wiklowski on 05.04.16.
 */
public class OcfDevice {
    private String mName;
    private String mDi;
    private List<OcfDeviceVariable> mOcfDeviceVariableList = new ArrayList<>();

    public OcfDevice(String name, String di){
        mName = name;
        mDi = di;
    }

    public void appendVariable(OcfDeviceVariable var){
        mOcfDeviceVariableList.add(var);

    }

    public void test(){
        Log.d("OcfDevice", "Testowa metoda dziala");
    }

}
