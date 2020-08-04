package com.example.autowallpaperchanger;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ImageData {
    private static final String TAG = "ImageData";

    public static final String IMAGE_DATA = "IMAGE_DATA";
    public static final String URI_LIST = "URI_LIST";
    public static final String CURRENT_URI = "CURRENT_URI";

    private List<Uri> uriList = new ArrayList<>();
    private int queueIndex;

    private static ImageData imageData = null;


    private ImageData() {
    }

    public static ImageData getInstance()
    {
        if (imageData == null)
            imageData = new ImageData();

        return imageData;
    }

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

    public void removeUri(int index){
        uriList.remove(index);
    }

    public int getQueueIndex() {
        return queueIndex;
    }

    public void setQueueIndex(int queueIndex) {
        this.queueIndex = queueIndex;
    }

    //Queue methods ////////////////////////////////////////////////////////////////////////////////
    public Uri getCurrentUri() {  // Dequeue from uriList
        Uri uri = uriList.get(queueIndex);
        queueIndex = (queueIndex + 1) % uriList.size();
        return uri;
    }

    public Uri getNextUri() {
        return uriList.get((queueIndex + 1) % uriList.size());
    }

    public Uri getRandomUri() {
        ArrayList<Uri> random = new ArrayList<>(uriList);
        random.remove(queueIndex);
        random.remove(queueIndex + 1);
        Collections.shuffle(random);
        return random.get(0);
    }

}
