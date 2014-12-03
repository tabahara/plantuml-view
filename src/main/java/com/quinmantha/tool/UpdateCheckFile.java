package com.quinmantha.tool;

import java.io.File;

/**
 * Created by tukahara on 14/12/03.
 */
public class UpdateCheckFile {
    private File _targetFile;
    private long _lastModifiedTime;

    public UpdateCheckFile(File targetFile){
        _targetFile = targetFile;
        _lastModifiedTime = 0;
    }

    private boolean bExec = false;
    public void start(){
        _lastModifiedTime = 0;
        bExec = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (bExec) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        ;
                    }
                    check();
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        bExec = false;
    }

    protected void check(){
        checkModified();
    }

    protected void checkModified(){
        if(_targetFile.exists()){
            long newModified = _targetFile.lastModified();
            if( _lastModifiedTime < newModified ){
                _lastModifiedTime = newModified;
                onModified(_targetFile);
            }
        } else if(_lastModifiedTime != 0){
            onDeleted(_targetFile);
            _lastModifiedTime = 0;
        }
    }

    protected void onModified(File targetFile){;}
    protected void onDeleted(File targetFile){;}
}
