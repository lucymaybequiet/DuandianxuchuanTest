package com.example.retranapplication;

import android.util.Log;
import android.os.Handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AManger {
    private File rootFile;
    private File file;
    private long downLoadSize;
    private final ThreadPoolExecutor executor;
    private boolean isDown = false;
    private String name;
    private String path;
    private RandomAccessFile raf;
    private long totalSize = 0;
    private Handler handler;
    private MyThread thread;
    private Iprogress iprogress;


    public AManger(String path,Iprogress iprogress){
        this.path = path;
        this.iprogress = iprogress;
        this.handler = new Handler();
        this.name = path.substring(path.lastIndexOf("/")+1);
        rootFile = FileUtils.getRootFile();
        executor = new ThreadPoolExecutor(5,5,50, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(3000));
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            downLoad();
        }
    }
    private void downLoad(){
        try {
            if (file == null){
                file = new File(rootFile,name);
                raf = new RandomAccessFile(file,"rw");
            } else {
                downLoadSize = file.length();
                if (raf == null){
                    raf = new RandomAccessFile(file,"rw");
                }
                raf.seek(downLoadSize);
            }
            totalSize = getContentLength(path);
            if (downLoadSize == totalSize) {
                return;
            }

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(path).addHeader("Range","bytes="+downLoadSize+"-"+totalSize).build();
            Response response = client.newCall(request).execute();
            InputStream ins = response.body().byteStream();

            int len = 0;
            byte[] bytes = new byte[1024];
            long endTime = System.currentTimeMillis();
            while ((len = ins.read(bytes)) != -1){
                raf.write(bytes,0,len);
                downLoadSize += len;
                if (System.currentTimeMillis() - endTime >1000){
                    final double dd = downLoadSize/(totalSize*1.0);
                    DecimalFormat format = new DecimalFormat("#0.00");
                    String value = format.format((dd*100))+"%";
                    Log.i("===================",value);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            iprogress.onProgress((int)(dd*100));
                        }
                    });
                }
            }
            response.close();
            }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public long getContentLength(String url)throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        long length = response.body().contentLength();
        response.close();
        return length;
    }
    public void start(){
        if (thread == null){
            thread = new MyThread();
            isDown = true;
            executor.execute(thread);
        }
    }
    public void stop(){
        if (thread != null){
            isDown = false;
            executor.remove(thread);
            thread = null;
        }
    }
    public interface Iprogress{
        void onProgress(int progress);
    }


}
