package org.example.crmsystem.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CachedBodyServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream cachedBodyInputStream;
    private ReadListener readListener;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public int read() throws IOException {
        return cachedBodyInputStream.read();
    }

    @Override
    public boolean isFinished() {
        return cachedBodyInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
        if (readListener != null) {
            try {
                if (!isFinished()) {
                    readListener.onDataAvailable();
                } else {
                    readListener.onAllDataRead();
                }
            } catch (IOException e) {
                readListener.onError(e);
            }
        }
    }
}
