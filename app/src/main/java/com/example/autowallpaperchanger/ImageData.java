package com.example.autowallpaperchanger;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageData implements Parcelable {
    private static final String TAG = "ImageData";

    public static final String URI_LIST = "URI_LIST";

    private List<Uri> uriList = new ArrayList<>();
    private int queueIndex = 0;


    ImageData() {

    }

    ImageData(List<String> data) {
        for (String uri :
                data) {
            uriList.add(Uri.parse(uri));
        }
    }

    protected ImageData(Parcel in) {
        uriList = in.createTypedArrayList(Uri.CREATOR);
    }

    public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel in) {
            return new ImageData(in);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };

    public List<Uri> getUriList() {
        return uriList;
    }

    public void setUriList(List<Uri> uriList) {
        this.uriList = uriList;
    }

    public void addUri(Uri uri) {
        this.uriList.add(uri);
    }

    public void addUri(List<Uri> uriList) {
        this.uriList.addAll(uriList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(uriList);
    }

    //Queue methods ////////////////////////////////////////////////////////////////////////////////
    public Uri getCurrentUri() {
        Uri uri = uriList.get(queueIndex);
        queueIndex = (queueIndex + 1) % uriList.size();
        return uri;
    }

    public Uri getNextUri() {
        return uriList.get(queueIndex + 1);
    }

    public Uri getRandomUri() {
        ArrayList<Uri> random = new ArrayList<>(uriList);
        random.remove(queueIndex);
        random.remove(queueIndex + 1);
        Collections.shuffle(random);
        return random.get(0);
    }


}
