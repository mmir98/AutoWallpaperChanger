package com.example.autowallpaperchanger;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImageData implements Parcelable {
    private static final String TAG = "ImageData";

    public static final String URI_LIST = "URI_LIST";
    public static final String FILE_NAME = "UriList";

    private List<Uri> uriList = new ArrayList<>();


    ImageData(){

    }

//    ImageData(String string){
//        try {
//            JSONObject jsonObject = new JSONObject(string);
//            Object temp = jsonObject.get(URI_LIST);
//            if (uriList.getClass() == temp.getClass()){
//                uriList = ((List<Uri>) temp);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    ImageData(List<Uri> data){
        uriList = data;
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

    public void addUri(Uri uri){
        this.uriList.add(uri);
    }

    public void addUri(List<Uri> uriList){
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

    public String createJsonFile(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(URI_LIST, uriList);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createJsonFile: JSON FAILED TO WRITE");
        }
        return jsonObject.toString();
    }
}
