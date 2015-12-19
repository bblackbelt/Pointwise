package de.pointwise.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emanuele on 18.12.15.
 */
public class Job implements Parcelable {

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };


    public final String mMessage;
    public final int mPriority;

    public Job(final String message, final int priority) {
        mMessage = message;
        mPriority = priority;
    }

    protected Job(Parcel in) {
        mMessage = in.readString();
        mPriority = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mMessage);
        parcel.writeInt(mPriority);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Message: ")
                .append(mMessage)
                .append("' ")
                .append("priority ")
                .append(mPriority)
                .append("\n").toString();
    }
}
