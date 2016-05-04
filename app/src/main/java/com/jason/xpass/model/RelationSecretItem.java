package com.jason.xpass.model;

/**
 * Description:
 * <p/>
 * Created by js.lee on 5/5/16.
 */
public class RelationSecretItem {

    private int secretId;

    private int itemId;

    private String itemContent;

    public int getSecretId() {
        return secretId;
    }

    public void setSecretId(int secretId) {
        this.secretId = secretId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemContent() {
        return itemContent;
    }

    public void setItemContent(String itemContent) {
        this.itemContent = itemContent;
    }
}
