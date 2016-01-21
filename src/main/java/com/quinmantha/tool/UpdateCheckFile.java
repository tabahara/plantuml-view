package com.quinmantha.tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.sun.nio.file.SensitivityWatchEventModifier;

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
                String dir = _targetFile.getParent();
                if(dir == null){
                    dir = ".";
                }
                String name = _targetFile.getName();
                // System.out.println(String.format("dir:%s name:%s", dir,name));
                Path path = FileSystems.getDefault().getPath(dir);
                try {
                    WatchService ws = path.getFileSystem().newWatchService();
                    path.register(ws, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY},
											SensitivityWatchEventModifier.HIGH);
                    while (bExec) {
                        try {
                            WatchKey wk = ws.take();

                            for(final WatchEvent<?> ev : wk.pollEvents()){
                                WatchEvent.Kind<?> kind = ev.kind();
                                if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)){
                                    Path p = (Path)(ev.context());
                                    String s = p.getFileName().toString();
                                    if(name.equals(s)) {
                                        System.out.println("check-0!");
                                        check();
                                    } else {
                                        System.out.println("check-1!");
                                    }
                                }
                            }

                            if(!wk.reset()){
                                wk.cancel();
                                ws.close();
                                bExec = false;
                            }
                        } catch (InterruptedException e) {
                            ;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
