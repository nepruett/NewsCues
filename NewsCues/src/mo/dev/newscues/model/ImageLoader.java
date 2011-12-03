package mo.dev.newscues.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mo.dev.newscues.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

public class ImageLoader implements ViewFactory {
    Context context;
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageSwitcher, List<String>> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageSwitcher, List<String>>());
    private Map<Thread, List<String>> threads =Collections.synchronizedMap(new WeakHashMap<Thread, List<String>>());
    ExecutorService executorService; 
    
    public ImageLoader(Context context){
    	this.context = context;
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    final int stub_id=R.drawable.stub;
    public void DisplayImage(final List<String> urls, final ImageSwitcher imageSwitcher)
    {
    	
    	imageSwitcher.setFactory(this);
    	imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(context,
                    android.R.anim.fade_in));
    	imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context,
                    android.R.anim.fade_out));
        imageViews.put(imageSwitcher, urls);
        Runnable r = new Runnable() {
        	@Override
			public void run() {
        		int i = 0;
        		while (true) {
        			String url = urls.get(i);
        			Bitmap bitmap=memoryCache.get(url);
                    if(bitmap!=null)
                        imageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));
                    else
                    {
                        queuePhoto(url, imageSwitcher);
                        imageSwitcher.setImageResource(stub_id);
                    }
                    i = i++ % urls.size();
                    try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		
        	}
        };
        Thread t = new Thread(r);
        t.start();
        threads.put(t, urls);
        
    }
    
    @Override
    public View makeView() {
          ImageView iView = new ImageView(context);
          iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
          iView.setLayoutParams(new
                      ImageSwitcher.LayoutParams(
                                  LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
          iView.setBackgroundColor(0xFF000000);
          return iView;
    }
        
    private void queuePhoto(String url, ImageSwitcher imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url) 
    {
    	try {
    		File f=fileCache.getFile(url);
        
    		//from SD cache
    		Bitmap b = decodeFile(f);
    		if(b!=null)
    			return b;
        
    		//from web
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageSwitcher imageView;
        public PhotoToLoad(String u, ImageSwitcher i){
            url=u; 
            imageView=i;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        List<String> tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.contains(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        @Override
		public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageDrawable(new BitmapDrawable(bitmap));
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}