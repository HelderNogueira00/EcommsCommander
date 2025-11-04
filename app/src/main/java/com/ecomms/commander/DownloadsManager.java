package com.ecomms.commander;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class DownloadsManager {

    private boolean isLoaded;
    private final int maxSize;
    private HashMap<Integer, DownloadItem> mItems;
    private HashMap<Integer, NewItem> mNewItems;

    private static DownloadsManager INSTANCE = null;

    public DownloadsManager(int maxSize) {

        INSTANCE = this;
        this.maxSize = maxSize;
    }

    public void load() {

        mNewItems = new HashMap<>();
        if(!isLoaded) {

            mItems = new HashMap<>();
            for(int n = 0; n < maxSize; n++)
                mItems.put(n, new DownloadItem(n));

            isLoaded = true;
        }
    }

    public DownloadItem createItem(String _path) {

        for(int n = 0; n < maxSize; n++) {

            if(mItems.get(n).isAvailable()) {

                mItems.get(n).assign(_path);
                return mItems.get(n);
            }
        }

        return null;
    }

    public void addItem(NewItem _item) {

        mNewItems.put(_item.id, _item);
    }

    public DownloadItem getItem(int id) {

        return mItems.get(id);
    }

    public NewItem getNewItem(int _id) {

        return mNewItems.get(_id);
    }

    public void removeItem(int id) {

        mItems.get(id).clean();
    }

    public static DownloadsManager getInstance() { return INSTANCE; }
}