package org.example.crmsystem.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CachedBodyServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream cachedBodyInputStream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public int read() {
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
        if (readListener != null) {
            try {
                if (!isFinished())
                    readListener.onDataAvailable();
                else
                    readListener.onAllDataRead();

            } catch (IOException e) {
                readListener.onError(e);
            }
        }
    }
}
