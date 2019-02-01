package com.eebbk.bfc.uploadsdk.module.httpuploader;

import android.support.annotation.NonNull;

import org.apache.http.entity.mime.MultipartEntity;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CustomMultipartEntity extends MultipartEntity{

    private final ProgressListener mListener;

    public CustomMultipartEntity(final ProgressListener listener){
        super();
        this.mListener = listener;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException{
        super.writeTo(new CountingOutputStream(outstream, this.mListener));
    }

    public interface ProgressListener{
        void transferred(long num);
    }

    public static class CountingOutputStream extends FilterOutputStream{
        private final static int TRANSFERR_WAITING_TIME = 1;
        private final ProgressListener mListener;

        public CountingOutputStream(final OutputStream out,
                                    final ProgressListener listener){
            super(out);
            this.mListener = listener;
        }

        public void write(@NonNull byte[] b, int off, int len) throws IOException{
            out.write(b, off, len);
            if(this.mListener != null){
                this.mListener.transferred(len);
                try{
                    Thread.sleep(TRANSFERR_WAITING_TIME);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

        public void write(int b) throws IOException{
            out.write(b);
            if(this.mListener != null){
                this.mListener.transferred(1);
            }
        }
    }

}
