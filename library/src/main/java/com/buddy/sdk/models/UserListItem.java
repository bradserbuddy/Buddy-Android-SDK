package com.buddy.sdk.models;

public class UserListItem {
    public enum UserListItemType {
        User,
        UserList

    }

    public String id;
    public UserListItemType itemType;
}
