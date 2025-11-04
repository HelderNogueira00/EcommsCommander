package com.ecomms.commander;

import java.io.File;
import java.io.FileOutputStream;

public class NewItem {

        public int id;
        private long readPos;
        private long length;
        private String path;

        private File f;
        private FileOutputStream fos;

        public NewItem(int _id, long _length, String _path) {

            try {

                this.id = _id;
                this.path = _path;
                this.length = _length;

                this.f = new File(_path);
                this.fos = new FileOutputStream(f);
            }
            catch(Exception _e) { System.out.println(); }
        }       

        public long writeBlock(byte[] _buffer) {

            try {
                fos.write(_buffer);
                readPos += _buffer.length;
            }
            catch(Exception _e) { _e.printStackTrace(); }

            return readPos;
        }
    }