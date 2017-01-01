package com.yiming.addoninterface;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/1/1.
 */

public class Action implements Parcelable{
    public static final int ACTION_NONE = 0;
    public static final int ACTION_SHOW = 1;
    public static final int ACTION_HIDDEN = 2;

    final static Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() {
        public Action createFromParcel(Parcel source) {
            int action = source.readInt();
            return new Action(action);
        }

        public Action[] newArray(int size) {
            return new Action[size];
        }
    };

    protected int mAction;
    public Action(int action) {
        mAction = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mAction);
    }
}
