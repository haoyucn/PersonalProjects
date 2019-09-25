package com.obfuscation.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.Object;
import java.sql.Blob;


/**
 * Created by jalton on 12/5/18.
 */

public class BLOBSerializer {

    private static BLOBSerializer instance = null;

    private BLOBSerializer() {
    }

    public static BLOBSerializer getInstance() {
        if (instance == null) {
            instance = new BLOBSerializer();
        }
        return instance;
    }

    public Blob serialize(Object obj) {

        Blob blob = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            byte[] byteArray = bos.toByteArray();

            blob = new javax.sql.rowset.serial.SerialBlob(byteArray);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                // ignore
            }
        }

        return blob;
    }

    public Object deserialize(Blob blob) {

        ObjectInput in = null;
        Object o = null;

        try {
            byte[] byteArray = blob.getBytes(0, (int) blob.length());
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            in = new ObjectInputStream(bis);
            o = in.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore
            }
        }

        return o;
    }
}
