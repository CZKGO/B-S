package com.czk.diabetes.util;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.czk.diabetes.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 陈忠凯 on 2017/4/26.
 */
public class Imageloader {
    ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private static Imageloader mInstance = new Imageloader();
    int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private LruCache drawableCache = new LruCache<String, Bitmap>(maxMemory / 8) {
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
        }

    };


    /**
     * 通过此方法来获取NativeImageLoader的实例
     *
     * @return
     */
    public static Imageloader getInstance() {
        return mInstance;
    }

    public void addBitmapToMemoryCache(String key, Bitmap drawable) {
        if (getBitmapFromMemCache(key) == null) {
            drawableCache.put(key, drawable);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) drawableCache.get(key);
    }


    public void loadImage(final String path, final int defaultResID, final ImageView imageView, final AsynchronousImageCallBack mCallBack) {
        imageView.setTag(path);
        //先获取内存中的Bitmap
        final Bitmap bitmap = getBitmapFromMemCache(path);
        final Handler mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (null != msg.obj && null != imageView && path.equals(imageView.getTag())) {
                    mCallBack.onImageLoader(imageView, path, (Bitmap) msg.obj);
                    imageView.setTag("");
                }

            }
        };
        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (null == bitmap) {
            Bitmap defaultImg = getBitmapFromMemCache(Integer.toString(defaultResID));
            if (null == defaultImg) {
                BitmapDrawable defaultPic = (BitmapDrawable) imageView.getContext().getResources().getDrawable(defaultResID);
                defaultImg = defaultPic.getBitmap();
                addBitmapToMemoryCache(Integer.toString(defaultResID), defaultImg);
            }
            if (null != defaultImg) {
                imageView.setImageBitmap(defaultImg);
            }
            threadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (!path.equals(imageView.getTag())) {
                        return;
                    }
                    Bitmap mBitmap = mCallBack.run(path, bitmap);
                    if (mBitmap != null) {
                        Message msg = mHander.obtainMessage();
                        msg.obj = mBitmap;
                        //将图片加入到内存缓存
                        addBitmapToMemoryCache(path, mBitmap);
                        mHander.sendMessage(msg);
                    }
                }
            });
        } else {
            imageView.setImageBitmap(bitmap);
        }
        return;

    }


    private Bitmap drawableToBitamp(Drawable drawable, int with, int hight) {
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(with, hight, config);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, with, hight);

        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * 加载应用图标
     *
     * @param packageName  包名
     * @param defaultResID 默认图片ID
     * @param imageView
     * @return
     */
    public void loadAppIcon(final String packageName, final int width, int defaultResID, final ImageView imageView) {
        imageView.setTag(packageName);
        //先获取内存中的Bitmap
        final Bitmap bitmap = getBitmapFromMemCache(packageName);
        final Handler mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                if (null != bitmap && null != imageView && packageName.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag("");
                }

            }
        };
        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (null == bitmap) {
            Bitmap defaultImg = getBitmapFromMemCache(Integer.toString(defaultResID));
            if (null == defaultImg) {
                Drawable defaultPic = imageView.getContext().getResources().getDrawable(defaultResID);
                defaultImg = drawableToBitamp(defaultPic, width);
                addBitmapToMemoryCache(Integer.toString(defaultResID), defaultImg);
            }
            if (null != defaultImg) {
                imageView.setImageBitmap(defaultImg);
            }
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    if (!packageName.equals(imageView.getTag())) {
                        return;
                    }
                    Bitmap mBitmap = null;
                    try {
                        PackageManager pm = MyApplication.getInstance().getPackageManager();
                        PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
                        Drawable drawable = info.applicationInfo.loadIcon(pm);
                        if (drawable != null) {
                            mBitmap = drawableToBitamp(drawable, width);
                        }
                    } catch (Exception e) {
                    }
                    if (mBitmap != null) {
                        Message msg = mHander.obtainMessage();
                        msg.obj = mBitmap;
                        //将图片加入到内存缓存
                        addBitmapToMemoryCache(packageName, mBitmap);
                        mHander.sendMessage(msg);
                    }
                }
            });
        } else {
            imageView.setImageBitmap(bitmap);
        }
        return;
    }

    /**
     * 加载应用图标
     *
     * @param packageName  包名
     * @param width
     * @param defaultResID 默认图片
     * @param imageView
     * @param storage
     * @return
     */
    public void loadAppIcon(final String packageName, final int width, int defaultResID, final ImageView imageView, final boolean storage) {
        imageView.setTag(packageName);
        //先获取内存中的Bitmap
        final Bitmap bitmap = getBitmapFromMemCache(packageName);
        final Handler mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                if (null != bitmap && null != imageView && packageName.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag("");
                }

            }
        };
        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (null == bitmap) {
            Bitmap defaultImg = getBitmapFromMemCache(Integer.toString(defaultResID));
            if (null == defaultImg) {
                Drawable defaultPic = imageView.getContext().getResources().getDrawable(defaultResID);
                defaultImg = drawableToBitamp(defaultPic, width);
                addBitmapToMemoryCache(Integer.toString(defaultResID), defaultImg);
            }
            if (null != defaultImg) {
                imageView.setImageBitmap(defaultImg);
            }
            threadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (!packageName.equals(imageView.getTag())) {
                        return;
                    }
                    Bitmap mBitmap = null;
                    if (storage) {
                        try {
                            String path = imageView.getContext().getFilesDir() + File.separator + FileUtil.CACHEPATH + File.separator + packageName;
                            BitmapFactory.Options outOptions = new BitmapFactory.Options();
                            outOptions.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(path, outOptions);
                            if (outOptions.outWidth > width) {
                                outOptions.inSampleSize = Math.round((float) outOptions.outWidth / (float) width);
                                outOptions.outWidth = width;
                                outOptions.outHeight = outOptions.outHeight / outOptions.inSampleSize;
                            }
                            outOptions.inJustDecodeBounds = false;
                            mBitmap = BitmapFactory.decodeFile(path, outOptions);
                        } catch (Exception e) {
                        }
                    }
                    if (null == mBitmap) {
                        try {
                            PackageManager pm = MyApplication.getInstance().getPackageManager();
                            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
                            Drawable drawable = info.applicationInfo.loadIcon(pm);
                            if (drawable != null) {
                                mBitmap = drawableToBitamp(drawable, width);
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (mBitmap != null) {
                        Message msg = mHander.obtainMessage();
                        msg.obj = mBitmap;
                        //将图片加入到内存缓存
                        addBitmapToMemoryCache(packageName, mBitmap);
                        mHander.sendMessage(msg);
                    }
                }
            });
        } else {
            imageView.setImageBitmap(bitmap);
        }
        return;
    }

    /**
     * 加载Apk图标
     *
     * @param apkPath      路径名
     * @param defaultResID 默认图片
     * @param imageView
     * @return
     */
    public void loadApkIcon(final String apkPath, final int width, int defaultResID, final ImageView imageView) {
        imageView.setTag(apkPath);
        //先获取内存中的Bitmap
        final Bitmap bitmap = getBitmapFromMemCache(apkPath);
        final Handler mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                if (null != bitmap && null != imageView && apkPath.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag("");
                }

            }
        };
        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (null == bitmap) {
            Bitmap defaultImg = getBitmapFromMemCache(Integer.toString(defaultResID));
            if (null == defaultImg) {
                Drawable defaultPic = imageView.getContext().getResources().getDrawable(defaultResID);
                defaultImg = drawableToBitamp(defaultPic, width);
                addBitmapToMemoryCache(Integer.toString(defaultResID), defaultImg);
            }
            if (null != defaultImg) {
                imageView.setImageBitmap(defaultImg);
            }
            threadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (!apkPath.equals(imageView.getTag())) {
                        return;
                    }
                    Bitmap mBitmap = null;
                    try {
                        PackageManager pm = MyApplication.getInstance().getPackageManager();

                        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                                PackageManager.GET_ACTIVITIES);
                        ;
                        if (null != info) {
                            ApplicationInfo appInfo = info.applicationInfo;
                            if (Build.VERSION.SDK_INT >= 8) {
                                appInfo.sourceDir = apkPath;
                                appInfo.publicSourceDir = apkPath;
                            }
                            Drawable drawable = appInfo.loadIcon(pm);
                            if (drawable != null) {
                                mBitmap = drawableToBitamp(drawable, width);
                            }
                        }

                    } catch (Exception e) {
                    }
                    Message msg = mHander.obtainMessage();
                    if (mBitmap != null) {
                        msg.obj = mBitmap;
                        //将图片加入到内存缓存
                        addBitmapToMemoryCache(apkPath, mBitmap);
                        mHander.sendMessage(msg);
                    }
                }
            });
        } else {
            imageView.setImageBitmap(bitmap);
        }
        return;
    }

    /**
     * 加载网络图片
     *
     * @param url
     * @param width
     * @param imageView
     * @return
     */
    public void loadImageByUrl(final String url, final int width, final int hight, final ImageView imageView) {
        loadImageByUrl(url, width, hight, -1, imageView, false);
    }

    /**
     * 加载网络图片
     *
     * @param url
     * @param width
     * @param defaultResID 默认图片
     * @param imageView
     * @return
     */
    public void loadImageByUrl(final String url, final int width, final int hight, int defaultResID, final ImageView imageView) {
        loadImageByUrl(url, width, hight, defaultResID, imageView, false);
    }

    /**
     * 加载网络图片
     *
     * @param url
     * @param width
     * @param hight
     * @param defaultResID 默认图片
     * @param imageView
     * @param storage
     * @return
     */
    public void loadImageByUrl(final String url, final int width, final int hight, int defaultResID, final ImageView imageView, final boolean storage) {
        imageView.setTag(url);
        //先获取内存中的Bitmap
        final Bitmap bitmap = getBitmapFromMemCache(url);
        final Handler mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                if (null != bitmap && null != imageView && url.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag("");
                }

            }
        };
        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (null == bitmap) {
            if (-1 != defaultResID) {
                Bitmap defaultImg = getBitmapFromMemCache(Integer.toString(defaultResID));
                if (null == defaultImg) {
                    Drawable defaultPic = imageView.getContext().getResources().getDrawable(defaultResID);
                    defaultImg = drawableToBitamp(defaultPic, width, hight);
                    addBitmapToMemoryCache(Integer.toString(defaultResID), defaultImg);
                }
                if (null != defaultImg) {
                    imageView.setImageBitmap(defaultImg);
                }
            }
            threadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (!url.equals(imageView.getTag())) {
                        return;
                    }
                    Bitmap mBitmap = null;
                    if (storage) {
                        try {
                            String path = imageView.getContext().getFilesDir() + File.separator + FileUtil.CACHEPATH + url;
                            BitmapFactory.Options outOptions = new BitmapFactory.Options();
                            outOptions.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(path, outOptions);
                            if (outOptions.outWidth > width) {
                                int xInSampleSize = Math.round((float) outOptions.outWidth / (float) width);
                                int yInSampleSize = Math.round((float) outOptions.outHeight / (float) hight);
                                if (xInSampleSize >= yInSampleSize) {
                                    outOptions.inSampleSize = xInSampleSize;
                                    outOptions.outWidth = width;
                                    outOptions.outHeight = outOptions.outHeight / outOptions.inSampleSize;
                                } else {
                                    outOptions.inSampleSize = yInSampleSize;
                                    outOptions.outWidth = outOptions.outWidth / outOptions.inSampleSize;
                                    outOptions.outHeight = hight;
                                }
                            }
                            outOptions.inJustDecodeBounds = false;
                            mBitmap = BitmapFactory.decodeFile(path, outOptions);
                        } catch (Exception e) {
                        }
                    }
                    if (null == mBitmap) {
                        try {
                            mBitmap = BitmapFactory.decodeStream(new URL(url).openStream());
                        } catch (Exception e) {
                        }
                    }
                    if (mBitmap != null) {
                        Message msg = mHander.obtainMessage();
                        msg.obj = mBitmap;
                        //将图片加入到内存缓存
                        addBitmapToMemoryCache(url, mBitmap);
                        mHander.sendMessage(msg);
                    }
                }
            });
        } else {
            imageView.setImageBitmap(bitmap);
        }
        return;
    }


    /**
     * 加载视频第一帧图片
     *
     * @param path         视频路径
     * @param defaultResID 默认图片
     * @param imageView
     * @return
     */
    public void loadVideoImage(final String path, int defaultResID, final ImageView imageView) {
        imageView.setTag(path);
        //先获取内存中的Bitmap
        final Bitmap bitmap = getBitmapFromMemCache(path);
        final Handler mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                if (null != bitmap && null != imageView && path.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag("");
                }

            }
        };
        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (null == bitmap) {
            Bitmap defaultImg = getBitmapFromMemCache(Integer.toString(defaultResID));
            if (null == defaultImg) {
                BitmapDrawable defaultPic = (BitmapDrawable) imageView.getContext().getResources().getDrawable(defaultResID);
                defaultImg = defaultPic.getBitmap();
                addBitmapToMemoryCache(Integer.toString(defaultResID), defaultImg);
            }
            if (null != defaultImg) {
                imageView.setImageBitmap(defaultImg);
            }
            threadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (!path.equals(imageView.getTag())) {
                        return;
                    }
                    Bitmap mBitmap = null;
                    try {
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(path);
                        mBitmap = mmr.getFrameAtTime();
                        mmr.release();//释放资源
                    } catch (Exception e) {
                    }
                    if (mBitmap != null) {
                        Message msg = mHander.obtainMessage();
                        msg.obj = mBitmap;
                        //将图片加入到内存缓存
                        addBitmapToMemoryCache(path, mBitmap);
                        mHander.sendMessage(msg);
                    }
                }
            });
        } else {
            imageView.setImageBitmap(bitmap);
        }
        return;
    }

    /**
     * 加载图片
     *
     * @param path         文件路径
     * @param defaultResID 默认图片
     * @param imageView
     * @return
     */
    public void loadFileImage(final String path, final int width, int defaultResID, final ImageView imageView) {
        imageView.setTag(path);
        //先获取内存中的Bitmap
        final Bitmap bitmap = getBitmapFromMemCache(path);
        final Handler mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                if (null != bitmap && null != imageView && path.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag("");
                }

            }
        };
        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (null == bitmap) {
            Bitmap defaultImg = getBitmapFromMemCache(Integer.toString(defaultResID));
            if (null == defaultImg) {
                BitmapDrawable defaultPic = (BitmapDrawable) imageView.getContext().getResources().getDrawable(defaultResID);
                defaultImg = defaultPic.getBitmap();
                addBitmapToMemoryCache(Integer.toString(defaultResID), defaultImg);
            }
            if (null != defaultImg) {
                imageView.setImageBitmap(defaultImg);
            }
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    if (!path.equals(imageView.getTag())) {
                        return;
                    }
                    Bitmap mBitmap = null;
                    try {
                        BitmapFactory.Options outOptions = new BitmapFactory.Options();
                        outOptions.inJustDecodeBounds = true;
                        // 加载获取图片的宽高
                        BitmapFactory.decodeFile(path, outOptions);
                        if (outOptions.outWidth > width) {
                            // 根据宽设置缩放比例
                            outOptions.inSampleSize = Math.round((float) outOptions.outWidth / (float) width);
                            outOptions.outWidth = width;
                            outOptions.outHeight = outOptions.outHeight / outOptions.inSampleSize;
                        }
                        // 重新设置该属性为false，加载图片返回
                        outOptions.inJustDecodeBounds = false;
                        mBitmap = BitmapFactory.decodeFile(path, outOptions);
                    } catch (Exception e) {
                    }
                    if (mBitmap != null) {
                        Message msg = mHander.obtainMessage();
                        msg.obj = mBitmap;
                        //将图片加入到内存缓存
                        addBitmapToMemoryCache(path, mBitmap);
                        mHander.sendMessage(msg);
                    }
                }
            });
        } else {
            imageView.setImageBitmap(bitmap);
        }
        return;
    }

    public Bitmap drawableToBitamp(Drawable drawable, int width) {
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, width, config);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, width);

        drawable.draw(canvas);
        return bitmap;
    }

    public void saveImgToPng(final Context mContext, final Bitmap bitmap, final String bitName) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                File dir = new File(mContext.getFilesDir() + File.separator + FileUtil.CACHEPATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(mContext.getFilesDir() + File.separator + FileUtil.CACHEPATH + File.separator + bitName);
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveImgToJpg(final Context mContext, final Bitmap bitmap, final String bitName) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                File dir = new File(mContext.getFilesDir() + File.separator + FileUtil.CACHEPATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(mContext.getFilesDir() + File.separator + FileUtil.CACHEPATH + File.separator + bitName);
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 加载异步图片的回调接口
     *
     * @author ChenZhongKai
     */
    public interface AsynchronousImageCallBack {
        /**
         * 在此方法中获取bitmp，此方法在线程中执行
         *
         * @param key    获取图片的路径，一般为路径或url
         * @param bitmap 默认图片
         */
        Bitmap run(String key, Bitmap bitmap);

        /**
         * 在此方法中展示图片
         *
         * @param imageView
         * @param key
         * @param bitmap
         */
        void onImageLoader(ImageView imageView, String key, Bitmap bitmap);
    }


}
