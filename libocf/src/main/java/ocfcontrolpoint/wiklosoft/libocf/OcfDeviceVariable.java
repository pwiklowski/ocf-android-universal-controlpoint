package ocfcontrolpoint.wiklosoft.libocf;

import android.util.Log;

/**
 * Created by Pawel Wiklowski on 05.04.16.
 */
public class OcfDeviceVariable {
    private String miff;
    private String mResourceType;
    private String mHref;
    private String mValue;

    public OcfDeviceVariable(String iff, String href, String rt){
        miff= iff;
        mResourceType = rt;
        mHref = href;
    }

    public String getHref(){
        return mHref;
    }

    public String getResourceType(){
        return mResourceType;
    }

    public String getValue(){
        return mValue;
    }

    public void setValue(String value){
        mValue = value;
    }

}
