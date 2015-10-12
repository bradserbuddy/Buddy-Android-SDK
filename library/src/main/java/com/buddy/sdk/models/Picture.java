package com.buddy.sdk.models;

public class Picture extends BinaryModelBase {

    public class SizeInfo {
        public int w;
        public int h;
    }

    public String title;
    public String caption;
    public SizeInfo size;
    public String watermark;
}
