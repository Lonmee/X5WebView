package com.example.x5webview.utils.shortcut;

import android.util.Log;

import java.util.ArrayList;

public class X5Pool {
    private static final String TAG = "X5Pool";
    private static X5Pool instance;
    private static int POOL_LEN = 5;
    private ArrayList sIdPool;
    private ArrayList orderPool;

    public X5Pool() {
        super();
        sIdPool = new ArrayList(X5Pool.POOL_LEN);
        orderPool = new ArrayList(X5Pool.POOL_LEN);
        for (int i = 0; i < X5Pool.POOL_LEN; i++) {
            sIdPool.add(0);
            orderPool.add(i);
        }
    }

    public static X5Pool getInstance() {
        return X5Pool.instance == null ? (X5Pool.instance = new X5Pool()) : X5Pool.instance;
    }

    public String getNextActivity(int sId) {
        return "com.example.x5webview.utils.x5.X5Activity" + (sId == 0 ? "Temp" : refreshOrder(sId));
    }

    public void retrieveActivity(int sId) {
        int index;
        if ((index = this.sIdPool.indexOf(sId)) >= 0) {
            //回收时反插入，维持活性为低，便于优先使用
            orderPool.add(0, orderPool.remove(orderPool.indexOf(index)));
            sIdPool.set(index, 0);
        } else {
            Log.i(X5Pool.TAG, "the sId retrieved is no exist");
        }
    }

    /**
     * 维护x5活性，将活性最低者靠前待启用；
     * x5在resume时调用该方法调整活性;
     *
     * @param sId
     * @return
     */
    public int refreshOrder(int sId) {
        int index;
        if ((index = this.sIdPool.indexOf(sId)) < 0) {
            orderPool.add(index = (int) orderPool.remove(0));
            sIdPool.set(index, sId);
        } else {
            orderPool.add(orderPool.remove(index));
        }
        return index;
    }

}
