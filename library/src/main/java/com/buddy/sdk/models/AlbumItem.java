package com.buddy.sdk.models;

public class AlbumItem extends ModelBase {

    public enum AlbumItemType {
        Picture,
        Video
    }

    public AlbumItemType itemType;
    public String caption;
    public String itemId;

}
