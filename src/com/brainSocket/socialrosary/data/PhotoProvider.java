package com.brainSocket.socialrosary.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.brainSocket.socialrosary.R;
import com.brainSocket.socialrosary.RosaryApp;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

public class PhotoProvider
{
	
	public static long CACHE_DURATION = 7*24*3600*1000; // 1 week
	
	private static PhotoProvider photoProvider = null;
	// Image loader library objects
	private static ImageLoader imageLoader = null;
	private DisplayImageOptions optionsFade, optionsNormal, optionsProfilePicture;
	// round images
	HashMap<String, Bitmap> mapRoundImages = new HashMap<String, Bitmap>();
	
	private PhotoProvider()
	{
		initImageLoader();
	}
	
	public static PhotoProvider getInstance()
	{
		if(photoProvider == null) {
			photoProvider = new PhotoProvider();
		}
		return photoProvider;
	}
	
	/**
	 * Initializes the Image Loader singleton, and sets up the display
	 * options used in the app
	 */
	void initImageLoader()
	{
		try {
			File cacheDir = RosaryApp.getAppContext().getExternalCacheDir();
			if (cacheDir == null){//cache dir not available
				cacheDir = RosaryApp.getAppContext().getCacheDir();
			}
			File thisCacheDir = new File(cacheDir.getPath(), "IPM");
			if ((!thisCacheDir.isDirectory()) || (thisCacheDir.isFile())){
				thisCacheDir.delete();
				thisCacheDir.mkdir();
			}
			 
			try {
			    File file = new File(thisCacheDir, ".nomedia");	
				file.createNewFile();
			} 
			catch (IOException e) {}
			
			
			ImageLoaderConfiguration.Builder builder = new Builder(RosaryApp.getAppContext());
			builder.denyCacheImageMultipleSizesInMemory();
			int limit = (int) (Runtime.getRuntime().maxMemory()/10);
			builder.memoryCache(new UsingFreqLimitedMemoryCache(limit));
//			builder.memoryCache(new UsingFreqLimitedMemoryCache(2000000));
			builder.discCache(new UnlimitedDiscCache(thisCacheDir));
			try {L.disableLogging();} catch (Exception e) {}

			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);
			
			checkLastCacheClearTimestamp();
			
			optionsFade = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.shape_transparent)
			.showImageForEmptyUri(R.drawable.shape_transparent)
			.showImageOnFail(R.drawable.shape_transparent)
			.considerExifParams(true)
			.delayBeforeLoading(100) 
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();
			
			optionsNormal = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.shape_transparent)
			.showImageForEmptyUri(R.drawable.shape_transparent)
			.showImageOnFail(R.drawable.shape_transparent)
			.considerExifParams(true)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.build();
			
			optionsProfilePicture = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.shape_transparent)
			.showImageForEmptyUri(R.drawable.img_contact)
			.showImageOnFail(R.drawable.img_contact)
			.displayer(new FadeInBitmapDisplayer(300))
			.considerExifParams(true)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.build();
		}
		catch (Exception e) {}
	}
	
	/**
	 * Loads the photo and displays it in the ImageView passed, 
	 * using the Fade options
	 * @param photoUrl
	 * The url of the photo to be displayed
	 * @param iv
	 * The ImageView holding the photo
	 */
	public void displayPhotoFade(final String photoUrl, final ImageView iv)
	{
		try {
			if(imageLoader == null) {
				initImageLoader();
			}

			imageLoader.displayImage(photoUrl, iv, optionsFade);
		} 
		catch (Exception e) {}
	}
	
	/**
	 * Loads the photo and displays it in the ImageView passed, 
	 * using the Normal options
	 * @param photoUrl
	 * The url of the photo to be displayed
	 * @param iv
	 * The ImageView holding the photo
	 */
	public void displayPhotoNormal(final String photoUrl, final ImageView iv)
	{
		try {
			if(imageLoader == null) {
				initImageLoader();
			}

			imageLoader.displayImage(photoUrl, iv, optionsNormal);
		}
		catch (Exception e) {}
	}
	
	public void displayProfilePicture(final String photoUrl, final ImageView iv)
	{
		try {
			if(imageLoader == null) {
				initImageLoader();
			}

			imageLoader.displayImage(photoUrl, iv, optionsProfilePicture);
		}
		catch (Exception e) {}
	}
	
	/**
	 * Loads the Facebook Photo and displays it in the given {@link ImageView}, 
	 * using the Normal display options
	 * @param id : the id of the Facebook user
	 * @param iv : the {@link ImageView} holding the photo
	 * @param dimen : the dimension x*y of square photo to be displayed
	 */
	public void displayFacebookThumbnailPhoto(final String id, final ImageView iv, final int dimen)
	{
		try {
			String photoUrl = "https://graph.facebook.com/" + id + "/picture?width=" + dimen + "&height=" + dimen;
			displayPhotoNormal(photoUrl, iv);
		}
		catch (Exception e) {}
	}
	
	
	/**
	 * It will check the date of the last time the cache was cleared. If it 
	 * is more than a week, a full cache clear will run.
	 */
	private void checkLastCacheClearTimestamp()
	{
		try {
			long timestamp = DataCacheProvider.getInstance().getStoredPhotoClearedCacheTimestamp();
			long timenow = RosaryApp.getTimestampNow();
			// compare times
			if(timenow > timestamp + CACHE_DURATION) {
				// clear cache
				clearCache();
				// store new timetsamp
				DataCacheProvider.getInstance().storePhotoClearedCacheTimestamp(timenow);
			}
		}
		catch (Exception e) {}
		
	}
	
	/**
	 * Called to run a full clear of cache (memory and disc)
	 */
	public void clearCache()
	{
		try {
			if(imageLoader == null) {
				initImageLoader();
			}

			imageLoader.clearDiscCache();
			imageLoader.clearMemoryCache();
			mapRoundImages.clear();
		}
		catch (Exception e) {}
	}
	
	public void clearMappedImages()
	{
		mapRoundImages.clear();
	}
	
	/**
	 * Downloads the images with the urls found in the given array. 
	 * It will generate a map (url-bitmap) if the downloads were successful. 
	 * Otherwise, it will clear the map and 
	 * @param arrayUrls
	 * @return
	 */
	public boolean downloadAndMapImages(ArrayList<String> arrayUrls)
	{
		boolean res = false;
		try {
			// clear old images
			mapRoundImages.clear();
			// loop ids and download images
			for(String url: arrayUrls) {
				Bitmap bm = PhotoProvider.getInstance().downloadImage(url);
				if(bm != null) {
					mapRoundImages.put(url, bm);
				}
				else {
					break;
				}
			}
			// check if all went well
			if(mapRoundImages.size() == arrayUrls.size()) {
				res = true;
			}
		}
		catch (Exception e) {
			mapRoundImages.clear();
			res = false;
		}
		return res;
	}
	
	public Bitmap getMappedImage(String url)
	{
		if(url != null) {
			return mapRoundImages.get(url);
		}
		else {
			return  null;
		}
	}
	
	/**
	 * Downloads the image with the given url and returns a Bitmap
	 */
	public Bitmap downloadImage(String url)
	{
		Bitmap bm = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();		        
			HttpGet request = new HttpGet(url);
		    HttpResponse response = httpClient.execute(request);
		    InputStream inputStream =  response.getEntity().getContent();
//		    Drawable drawable = Drawable.createFromStream(inputStream, "src");
		    bm = BitmapFactory.decodeStream(inputStream);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}
}
