package mo.dev.newscues.model;

import java.io.File;
import java.io.IOException;

import mo.dev.newscues.NewsCuesActivity;
import mo.dev.newscues.NewsCuesApplication;

import android.content.Context;
import android.util.Log;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"NewsCues");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists()) {
            boolean result = cacheDir.mkdirs();
            Log.d(NewsCuesApplication.TAG, "created temp dir - " + result);
        }
    }
    
    public File getFile(String url) throws IOException{
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        if (!f.exists()) {
        	f.createNewFile();
        }
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

}