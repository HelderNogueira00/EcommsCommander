package com.ecomms.commander;
import java.io.File;

 public class DownloadItem {

    public int id;
    public long length;
    public int readPos;
    public String path;
    public int blockSize;

    public DownloadItem(int _id) {

        this.id = _id;
    }

    public void assign(String _path) {

        File f = new File(_path);
        length = f.length();
        readPos = 0;
        blockSize = 512000;
    }

    public void clean() {

        path = "";
        length = 0;
        readPos = 0;
        blockSize = 0;
    }

    public boolean isAvailable() { return path.equals(""); }
}   