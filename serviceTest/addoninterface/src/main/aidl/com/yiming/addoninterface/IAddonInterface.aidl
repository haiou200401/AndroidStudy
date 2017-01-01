// IAddonInterface.aidl
package com.yiming.addoninterface;

import com.yiming.addoninterface.Action;
// Declare any non-default types here with import statements

interface IAddonInterface {
    String getAddonName();
    Action getAction();
//    View getView(Context context);
}
