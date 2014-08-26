package com.buddy.sdk.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shawn on 7/7/14.
 */
public class Picture extends BinaryModelBase {

    public class SizeInfo {
        public int w;
        public int h;
    }

    @SerializedName("title")
    public String title;
    public String caption;

    @SerializedName("size")
    public SizeInfo Size;
}
