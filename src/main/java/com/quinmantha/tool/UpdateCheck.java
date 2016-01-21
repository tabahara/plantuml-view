package com.quinmantha.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by asuka on 14/12/02.
 */
public class UpdateCheck {
    private File _targetDir;

    protected File getTargetDir(){
        return _targetDir;
    }

    protected boolean bExec;
    protected List<String> registeredFiles;
    protected Map<String,Long>  lastModifiedTimes;

    public UpdateCheck(File targetDir){
        _targetDir = targetDir;
    }

    public void start(){
        bExec = true;
        registeredFiles = new ArrayList<String>();
        lastModifiedTimes = new HashMap<String,Long>();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                while(bExec){
                    try {
                        Thread.sleep(1000L);
                    } catch(InterruptedException e){;}
                    check();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop(){
        bExec = false;
    }

    protected void check(){
        checkRemoved();
        checkNew();
        checkModified();
    }

    protected void checkRemoved(){
        Iterator<String> it = registeredFiles.iterator();
        while(it.hasNext()){
            String filename = it.next();
            File file = new File(_targetDir, filename);
            if(!file.exists()){
                it.remove();
                // System.out.println(filename + " has been deleted.");
            }
        }
    }

    protected void checkNew(){
        String[] files = _targetDir.list();
        for(String file:files){
            if(file.charAt(0) != '.') {
                if (!registeredFiles.contains(file)) {
                    registeredFiles.add(file);
                    // System.out.println("add " + file);
                }
            }
        }
    }

    protected void onModified(File filename){
        // System.out.println("onModified(" + filename + ")");

    }

    protected void checkModified(){
        Iterator<String> it = registeredFiles.iterator();
        while(it.hasNext()){
            String filename = it.next();
            File file = new File(_targetDir, filename);

            long newModified = file.lastModified();
            try {
                long lastModified = lastModifiedTimes.get(filename);
                if(lastModified < newModified ){
                    // System.out.println("update entry(" + filename + ")");
                    lastModifiedTimes.put(filename, new Long(newModified));
                    onModified(file);
                }
            } catch(NullPointerException e){
                // System.out.println("new entry(" + filename + ")");
                lastModifiedTimes.put(filename, new Long(newModified));
                onModified(file);
            }
        }
    }
}
