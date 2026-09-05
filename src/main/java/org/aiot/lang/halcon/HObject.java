package org.aiot.lang.halcon;

public class HObject implements AutoCloseable {
    // 本地对象指针（Halcon对象句柄）
    private long nativeHandle;
    private boolean isClosed = false;

    public HObject(){
        this.nativeHandle = HalconJavaNative.createHObject();
    }

    // 私有构造方法（通过本地方法初始化）
    public HObject(long nativeHandle) {
        this.nativeHandle = nativeHandle;
    }

    // 释放资源（AutoCloseable接口）
    @Override
    public void close() {
        if (!isClosed) {
        	System.out.println("HObject的close方法被调用");
        	HalconJavaNative.clearHObject(nativeHandle);
            isClosed = true;
        }
    }

    // 避免内存泄漏（finalize作为最后保障）
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

	public long getNativeHandle() {
		return nativeHandle;
	}

	public void setNativeHandle(long nativeHandle) {
		this.nativeHandle = nativeHandle;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
}