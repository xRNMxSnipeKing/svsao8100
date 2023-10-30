package com.microsoft.xbox.toolkit.ui;

import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.MemoryMonitor;
import com.microsoft.xbox.toolkit.MultiMap;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ThreadSafePriorityQueue;
import com.microsoft.xbox.toolkit.TimeMonitor;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEFileCache;
import com.microsoft.xbox.toolkit.XLEFileCacheManager;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEMemoryCache;
import com.microsoft.xbox.toolkit.XLEThread;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.ui.XLEBitmap.XLEBitmapDrawable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.http.client.methods.HttpGet;

public class TextureManager {
    private static final int BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES = 5242880;
    private static final String BMP_FILE_CACHE_DIR_NAME = "texture";
    private static final int BMP_FILE_CACHE_SIZE = 2000;
    private static final int DECODE_THREAD_WAIT_TIMEOUT_MS = 3000;
    private static final int TEXTURE_TIMEOUT_MS = 15000;
    private static final long TIME_TO_RETRY_MS = 300000;
    public static TextureManager instance = new TextureManager();
    private XLEMemoryCache<TextureManagerScaledNetworkBitmapRequest, XLEBitmap> bitmapCache = new XLEMemoryCache(getNetworkBitmapCacheSizeInMB() * 1048576, BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES);
    private XLEFileCache bitmapFileCache = XLEFileCacheManager.createCache(BMP_FILE_CACHE_DIR_NAME, 2000);
    private Thread decodeThread = null;
    private HashSet<TextureManagerScaledNetworkBitmapRequest> inProgress = new HashSet();
    private Object listLock = new Object();
    private HashMap<TextureManagerScaledResourceBitmapRequest, XLEBitmap> resourceBitmapCache = new HashMap();
    private TimeMonitor stopwatch = new TimeMonitor();
    private HashMap<TextureManagerScaledNetworkBitmapRequest, Long> timeToRetryCache = new HashMap();
    private ThreadSafePriorityQueue<TextureManagerDownloadRequest> toDecode = new ThreadSafePriorityQueue();
    private MultiMap<TextureManagerScaledNetworkBitmapRequest, ImageView> waitingForImage = new MultiMap();

    private class TextureManagerDecodeThread implements Runnable {
        private static final int MAX_BITMAP_SIZE = 15728640;

        private TextureManagerDecodeThread() {
        }

        public void run() {
            System.out.println("Starting decode thread");
            while (true) {
                TextureManagerDownloadRequest request = (TextureManagerDownloadRequest) TextureManager.this.toDecode.pop();
                XLEBitmap bitmap = null;
                if (request.stream != null) {
                    BackgroundThreadWaitor.getInstance().waitForReady(TextureManager.DECODE_THREAD_WAIT_TIMEOUT_MS);
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        StreamUtil.CopyStreamWithLimit(baos, request.stream, MAX_BITMAP_SIZE);
                        byte[] buffer = baos.toByteArray();
                        Options options = new Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(new ByteArrayInputStream(buffer), null, options);
                        Options scaleoptions = TextureManager.this.computeInSampleSizeOptions(request.key.bindingOption.width, request.key.bindingOption.height, options);
                        int expectedBytes = ((options.outWidth / scaleoptions.inSampleSize) * (options.outHeight / scaleoptions.inSampleSize)) * 4;
                        synchronized (TextureManager.this.listLock) {
                            TextureManager.this.bitmapCache.freeAtLeastNBytes(expectedBytes);
                        }
                        XLEBitmap bitmapsrc = XLEBitmap.decodeStream(new ByteArrayInputStream(buffer), scaleoptions);
                        if (request.key.bindingOption.useFileCache && !TextureManager.this.bitmapFileCache.contains(request.key)) {
                            TextureManager.this.bitmapFileCache.save(request.key, new ByteArrayInputStream(buffer));
                        }
                        bitmap = TextureManager.this.createScaledBitmap(bitmapsrc, request.key.bindingOption.width, request.key.bindingOption.height);
                    } catch (OutOfMemoryError e) {
                        XLELog.Error("TextureManager", "failed to create bitmap");
                        bitmap = null;
                    } catch (Exception e2) {
                        XLELog.Error("TextureManager", "failed to create bitmap");
                        bitmap = null;
                    }
                }
                BackgroundThreadWaitor.getInstance().waitForReady(TextureManager.DECODE_THREAD_WAIT_TIMEOUT_MS);
                synchronized (TextureManager.this.listLock) {
                    if (bitmap != null) {
                        TextureManager.this.bitmapCache.add(request.key, bitmap, bitmap.getByteCount());
                    } else if (request.key.bindingOption.resourceIdForError != -1) {
                        bitmap = TextureManager.this.loadResource(request.key.bindingOption.resourceIdForError);
                        TextureManager.this.timeToRetryCache.put(request.key, Long.valueOf(System.currentTimeMillis() + TextureManager.TIME_TO_RETRY_MS));
                    }
                    TextureManager.this.drainWaitingForImage(request.key, bitmap);
                    TextureManager.this.inProgress.remove(request.key);
                }
            }
        }
    }

    private class TextureManagerDownloadThreadWorker implements Runnable {
        private TextureManagerDownloadRequest request;

        public TextureManagerDownloadThreadWorker(TextureManagerDownloadRequest request) {
            this.request = request;
        }

        public void run() {
            boolean z = (this.request.key == null || this.request.key.url == null) ? false : true;
            XLEAssert.assertTrue(z);
            this.request.stream = null;
            try {
                if (this.request.key.url.startsWith("http")) {
                    if (this.request.key.bindingOption.useFileCache) {
                        this.request.stream = TextureManager.this.bitmapFileCache.getInputStreamForRead(this.request.key);
                        if (this.request.stream == null) {
                            this.request.stream = downloadFromWeb(this.request.key.url);
                        }
                    } else {
                        this.request.stream = downloadFromWeb(this.request.key.url);
                    }
                    synchronized (TextureManager.this.listLock) {
                        TextureManager.this.toDecode.push(this.request);
                    }
                }
                this.request.stream = downloadFromAssets(this.request.key.url);
                synchronized (TextureManager.this.listLock) {
                    TextureManager.this.toDecode.push(this.request);
                }
            } catch (Exception e) {
                XLELog.Warning("TextureManager", e.toString());
            }
        }

        private InputStream downloadFromWeb(String requestUrl) {
            try {
                return HttpClientFactory.textureFactory.getHttpClient(TextureManager.TEXTURE_TIMEOUT_MS).getHttpStatusAndStreamInternal(new HttpGet(URI.create(requestUrl)), false).stream;
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream downloadFromAssets(String requestUrl) {
            try {
                return XboxApplication.AssetManager.open(requestUrl);
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static TextureManager Instance() {
        return instance;
    }

    private int getNetworkBitmapCacheSizeInMB() {
        return (Math.max(0, MemoryMonitor.instance().getMemoryClass() - 64) / 2) + 12;
    }

    public TextureManager() {
        this.stopwatch.start();
        this.decodeThread = new XLEThread(new TextureManagerDecodeThread(), "XLETextureDecodeThread");
        this.decodeThread.setDaemon(true);
        this.decodeThread.setPriority(4);
        this.decodeThread.start();
    }

    private static boolean invalidUrl(String url) {
        return url == null || url.length() == 0;
    }

    private static boolean validResizeDimention(int width, int height) {
        if (width != 0 && height != 0) {
            return width > 0 && height > 0;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void load(TextureManagerScaledNetworkBitmapRequest key) {
        if (!invalidUrl(key.url)) {
            XLEThreadPool.textureThreadPool.run(new TextureManagerDownloadThreadWorker(new TextureManagerDownloadRequest(key)));
        }
    }

    public XLEBitmapDrawable loadScaledResourceDrawable(int resourceId) {
        XLEBitmap bitmap = loadResource(resourceId);
        if (bitmap == null) {
            return null;
        }
        return bitmap.getDrawable();
    }

    private Options computeInSampleSizeOptions(int desiredw, int desiredh, Options options) {
        boolean z = true;
        Options scaleoptions = new Options();
        int scale = 1;
        if (validResizeDimention(desiredw, desiredh) && options.outWidth > desiredw && options.outHeight > desiredh) {
            scale = (int) Math.pow(2.0d, (double) Math.min((int) Math.floor(Math.log((double) (((float) options.outWidth) / ((float) desiredw))) / Math.log(2.0d)), (int) Math.floor(Math.log((double) (((float) options.outHeight) / ((float) desiredh))) / Math.log(2.0d))));
            if (scale < 1) {
                z = false;
            }
            XLEAssert.assertTrue(z);
        }
        scaleoptions.inSampleSize = scale;
        return scaleoptions;
    }

    public XLEBitmap loadResource(int resourceId) {
        TextureManagerScaledResourceBitmapRequest request = new TextureManagerScaledResourceBitmapRequest(resourceId);
        XLEBitmap bitmap = (XLEBitmap) this.resourceBitmapCache.get(request);
        if (bitmap == null) {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(XboxApplication.Resources, request.resourceId, options);
            bitmap = XLEBitmap.decodeResource(XboxApplication.Resources, request.resourceId);
            this.resourceBitmapCache.put(request, bitmap);
        }
        XLEAssert.assertNotNull(bitmap);
        return bitmap;
    }

    public void preload(int resourceId) {
    }

    public void preload(URI uri) {
    }

    public void preloadFromFile(String filePath) {
    }

    public void bindToView(int resourceId, ImageView view, int width, int height) {
        boolean z;
        boolean z2 = true;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        XLEBitmap bitmap = loadResource(resourceId);
        if (bitmap == null) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        if (view instanceof XLEImageView) {
            ((XLEImageView) view).TEST_loadingOrLoadedImageUrl = Integer.toString(resourceId);
        }
        view.setImageBitmap(bitmap.getBitmap());
    }

    public void bindToViewFromFile(String filePath, ImageView view, TextureBindingOption option) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        bindToViewInternal(filePath, view, option);
    }

    public void bindToViewFromFile(String filePath, ImageView view, int width, int height) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (width == 0 || height == 0) {
            throw new UnsupportedOperationException();
        }
        bindToViewInternal(filePath, view, new TextureBindingOption(width, height));
    }

    public void bindToView(URI uri, ImageView view, int width, int height) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (width == 0 || height == 0) {
            throw new UnsupportedOperationException();
        }
        bindToViewInternal(uri == null ? null : uri.toString(), view, new TextureBindingOption(width, height));
    }

    public void bindToView(URI uri, ImageView view, TextureBindingOption option) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        bindToViewInternal(uri == null ? null : uri.toString(), view, option);
    }

    public void clearWaitingForImages() {
        synchronized (this.listLock) {
            this.waitingForImage.clear();
        }
    }

    public void setCachingEnabled(boolean enabled) {
        this.bitmapCache = new XLEMemoryCache(enabled ? getNetworkBitmapCacheSizeInMB() : 0, BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES);
        this.bitmapFileCache = XLEFileCacheManager.createCache(BMP_FILE_CACHE_DIR_NAME, 2000, enabled);
        this.resourceBitmapCache = new HashMap();
    }

    private void bindToViewInternal(String url, ImageView view, TextureBindingOption option) {
        TextureManagerScaledNetworkBitmapRequest key = new TextureManagerScaledNetworkBitmapRequest(url, option);
        XLEBitmap bitmap = null;
        synchronized (this.listLock) {
            boolean needToDownload;
            if (this.waitingForImage.containsValue(view)) {
                this.waitingForImage.removeValue(view);
            }
            if (!invalidUrl(url)) {
                bitmap = (XLEBitmap) this.bitmapCache.get(key);
                if (bitmap != null) {
                    needToDownload = false;
                } else if (this.timeToRetryCache.get(key) == null) {
                    needToDownload = true;
                } else if (((Long) this.timeToRetryCache.get(key)).longValue() < System.currentTimeMillis()) {
                    XLELog.Info("TextureManager", "Timeout over, retrying...");
                    this.timeToRetryCache.remove(key);
                    needToDownload = true;
                } else {
                    XLELog.Info("TextureManager", "Timeout not fulfilled, showing error image");
                    if (option.resourceIdForError != -1) {
                        bitmap = loadResource(option.resourceIdForError);
                    }
                    needToDownload = false;
                }
            } else if (option.resourceIdForError != -1) {
                bitmap = loadResource(option.resourceIdForError);
                needToDownload = false;
                XLEAssert.assertNotNull(bitmap);
            } else {
                needToDownload = false;
            }
            if (needToDownload) {
                if (option.resourceIdForLoading != -1) {
                    bitmap = loadResource(option.resourceIdForLoading);
                    XLEAssert.assertTrue(bitmap != null);
                }
                this.waitingForImage.put(key, view);
                if (!this.inProgress.contains(key)) {
                    this.inProgress.add(key);
                    load(key);
                }
            }
        }
        view.setImageBitmap(bitmap == null ? null : bitmap.getBitmap());
        if (view instanceof XLEImageView) {
            ((XLEImageView) view).TEST_loadingOrLoadedImageUrl = url;
        }
    }

    private XLEBitmap createScaledBitmap(XLEBitmap bitmapsrc, int width, int height) {
        XLEBitmap bitmap = bitmapsrc;
        if (!validResizeDimention(width, height) || bitmapsrc.getBitmap() == null) {
            return bitmap;
        }
        XLELog.Diagnostic("TextureManager", String.format("Valid view width and height. %d x %d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
        XLELog.Diagnostic("TextureManager", String.format("Bitmap width and height. %d x %d", new Object[]{Integer.valueOf(bitmap.getBitmap().getWidth()), Integer.valueOf(bitmap.getBitmap().getHeight())}));
        float bitmapAR = ((float) bitmapsrc.getBitmap().getHeight()) / ((float) bitmapsrc.getBitmap().getWidth());
        if (((float) height) / ((float) width) < bitmapAR) {
            width = Math.max(1, (int) (((float) height) / bitmapAR));
        } else {
            height = Math.max(1, (int) (((float) width) * bitmapAR));
        }
        XLELog.Diagnostic("TextureManager", String.format("Adjusted dimensions based on bitmap AR %d x %d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
        return XLEBitmap.createScaledBitmap8888(bitmapsrc, width, height, true);
    }

    private void drainWaitingForImage(TextureManagerScaledNetworkBitmapRequest key, XLEBitmap bitmap) {
        if (this.waitingForImage.containsKey(key)) {
            Iterator i$ = this.waitingForImage.get(key).iterator();
            while (i$.hasNext()) {
                ImageView view = (ImageView) i$.next();
                if (view != null) {
                    setView(key, view, bitmap);
                }
            }
        }
    }

    private void setView(final TextureManagerScaledNetworkBitmapRequest key, final ImageView view, final XLEBitmap bitmap) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
                synchronized (TextureManager.this.listLock) {
                    boolean stillValid = TextureManager.this.waitingForImage.keyValueMatches(key, view);
                }
                if (stillValid) {
                    view.setImageBitmap(bitmap == null ? null : bitmap.getBitmap());
                    synchronized (TextureManager.this.listLock) {
                        TextureManager.this.waitingForImage.removeValue(view);
                    }
                }
            }
        });
    }

    public void logMemoryUsage() {
    }

    public void purgeResourceBitmapCache() {
        this.resourceBitmapCache.clear();
    }
}
