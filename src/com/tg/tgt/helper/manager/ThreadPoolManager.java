package com.tg.tgt.helper.manager;

public class ThreadPoolManager {
    static ThreadPoolProxy mNormalThreadPoolProxy;

    /**
     * 返回普通线程池代理
     * @return
     */
    public static ThreadPoolProxy getInstance() {
        if (mNormalThreadPoolProxy == null) {
            synchronized (ThreadPoolManager.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(5, 10, 3000);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

}
