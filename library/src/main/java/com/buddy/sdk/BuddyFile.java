package com.buddy.sdk;

import java.io.File;
import java.io.InputStream;

public class BuddyFile {

    File file;
    InputStream stream;
    String contentType;

    public BuddyFile(File file, String contentType) {
        if (file == null || contentType == null) throw new IllegalArgumentException();
        this.contentType = contentType;
        this.file = file;
    }

    public BuddyFile(InputStream stream, String contentType) {
        if (stream == null || contentType == null) throw new IllegalArgumentException();
        this.contentType = contentType;
        this.stream = stream;
    }

    public Object getValue() {
        if (file != null) {
            return file;
        }
        return stream;
    }

    public InputStream getStream() {
        return stream;
    }

    public File getFile() {
        return file;
    }

    public String getContentType() {
        return contentType;
    }

}
