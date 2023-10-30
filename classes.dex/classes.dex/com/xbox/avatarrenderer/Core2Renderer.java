package com.xbox.avatarrenderer;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.os.Environment;
import android.util.Log;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditor;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorEditEvent;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorFunctionSet;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorScriptingHelper;
import com.xbox.avatarrenderer.Kernel.AvatarManifest;
import com.xbox.avatarrenderer.Kernel.AvatarManifest.AVATAR_BODY_TYPE;
import com.xbox.avatarrenderer.Kernel.AvatarManifestEditor;
import com.xbox.avatarrenderer.Kernel.KernelScriptingHelper;
import com.xbox.avatarrenderer.Kernel.ScriptException;
import com.xbox.avatarrenderer.Kernel.Story;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.Semaphore;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class Core2Renderer {
    private static final boolean DEBUG = false;
    private static String TAG = "Core2Renderer";
    private static boolean m_loadedNativeLibrary;
    private final int MAX_CACHE_SIZE = 10000000;
    private final int NHIST = 6;
    private String cachePath = null;
    private boolean glThreadRunning = true;
    private Semaphore mRendererLock = new Semaphore(1);
    private Semaphore mRendererStateLock = new Semaphore(1);
    private String m_ScreenShotName = null;
    private long[] m_TimeHistory = new long[6];
    private long m_TimeTotal = 0;
    private AssetManager m_assetManager = null;
    private Boolean m_bDisableWebAccess = Boolean.valueOf(false);
    private Boolean m_bFailedToInit = Boolean.valueOf(false);
    private Core2Callbacks[] m_callBackList = null;
    private int m_frameCount = 0;
    private int m_iContextLost = -1;
    private int m_iInstanceID = -1;
    private long m_minFrameTimeMS = 0;
    private long m_startTime = 0;
    private Core2View m_view;
    private Activity mactivity;

    public enum ANIMATION_CHAINING_MODE {
        IMMEDIATE(0),
        INSERT_END(0),
        INSERT_BEGIN(1),
        REPLACE(2),
        CHAINING_MODES(255),
        ANIMATION_FINISHED(AvatarEditorModel.AVATAREDIT_OPTION_GLASSES),
        SEQUENCE_FINISHED(256),
        INTERRUPT(AvatarEditorModel.AVATAREDIT_OPTION_RINGS),
        SEQUENCING_MODES(3840),
        SYNCHRONIZED(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYES),
        SEQUENCING_MODS(61440),
        LAYER_0(0),
        LAYER_1(1),
        LAYER_2(2),
        LAYER_3(3),
        LAYER_4(4),
        LAYER_5(5),
        LAYER_6(6),
        LAYER_7(7),
        LAYER_INDEX(983040),
        BACKGROUND(0),
        FOREGROUND(262144);
        
        private final int val;

        private ANIMATION_CHAINING_MODE(int v) {
            this.val = v;
        }

        public int getInt() {
            return this.val;
        }
    }

    public enum AVATAREDITOR_CAMERA_POSE {
        BODYFIXED,
        BODY,
        HEAD,
        LEFTHEAD,
        RIGHTHEAD,
        LEFTHAND,
        RIGHTHAND,
        LEFTSHOE,
        RIGHTSHOE,
        TORSO,
        PANTS,
        POSESCOUNT
    }

    public enum AVATAR_DYNAMIC_COLOR_TYPE {
        SKIN,
        HAIR,
        MOUTH,
        IRIS,
        EYEBROW,
        EYESHADOW,
        FACIALHAIR,
        SKINFEATURES1,
        SKINFEATURES2
    }

    public enum AvatarEditorEventContext {
        UNDEFINED,
        AVATAR_ATTACHED,
        AVATAR_PRELOADED,
        AVATAR_UPDATED,
        AVATAR_UPDATE_CANCELED,
        AVATAR_PROP_PRELOADED,
        EXCEPTION,
        EXCEPTION_DETACHED,
        NOTIFICATION
    }

    public enum IkLock {
        IKL_INVALID(0),
        IKL_DEFAULT(1),
        IKL_SOFT_NAIL(2),
        IKL_LOCK_CHILDREN_ROTATION(4);
        
        private final int val;

        private IkLock(int v) {
            this.val = v;
        }

        public int getInt() {
            return this.val;
        }
    }

    public enum REFERENCE_CHANNEL {
        RCN_NONE,
        RCN_SCALE_X,
        RCN_SCALE_Y,
        RCN_SCALE_Z
    }

    public enum SEQUENCED_ANIMATION_MODE {
        PLAYONCE(0),
        PLAYONCE_RANDOM(1),
        REPEAT(2),
        REPEAT_RANDOM(3);
        
        private final int val;

        private SEQUENCED_ANIMATION_MODE(int v) {
            this.val = v;
        }

        public int getInt() {
            return this.val;
        }
    }

    public enum SynchronizedAnimationPosition {
        SAP_ABSOULTE,
        SAP_FIRST,
        SAP_POSITION_AVERAGE,
        SAP_POSITION_CENTER
    }

    public enum SynchronizedAnimationScale {
        SAS_NONE,
        SAS_AVERAGE,
        SAS_MIN,
        SAS_MAX,
        SAS_MINMAX,
        SAS_MIN_MAX_LOW,
        SAS_MIN_MAX_HIGH
    }

    public enum SynchronizedAnimationTiming {
        SAT_DEFAULT,
        SAT_MINIMIZE_IDLING,
        SAT_RANDOMIZED
    }

    public enum VARIABLE_SCOPE {
        INVALID(0),
        THIS(1),
        GROUP(2),
        GLOBAL(3),
        CONSTANT(4),
        MAKE_DWORD(Integer.MAX_VALUE);
        
        private final int val;

        private VARIABLE_SCOPE(int v) {
            this.val = v;
        }

        public int getInt() {
            return this.val;
        }
    }

    public enum VARIABLE_TYPE {
        INVALID,
        BOOL,
        INT,
        FLOAT,
        TEXT,
        GUID,
        OBJECT,
        VECTOR,
        PROPERTIES
    }

    private native int[] nativeCaptureScreenShot(int i, int[] iArr);

    private native int nativeCreateAvatarEditor(int i);

    private native int nativeCreateAvatarEditorFunctionSet(int i, int i2, String str);

    private native int nativeCreateAvatarEditorScriptingHelper(int i, int i2);

    private native int nativeCreateKernelScriptingHelper(int i, int i2);

    private native int nativeCreateManifestFromBinary(int i, byte[] bArr);

    private native int nativeCreateManifestFromHex(int i, String str);

    private native void nativeDestroy(int i);

    private native int nativeGetCarryableBoundingBox(int i, String str, float[] fArr);

    private native float nativeGetFrameRate(int i);

    private native int nativeInit(AssetManager assetManager, String str);

    private native void nativePurgeGrapicsContext(int i, int i2);

    private native int nativePurgeScene(int i);

    private native void nativeRender(int i);

    private native void nativeResize(int i, int i2, int i3);

    private native void nativeSetBackgroundColor(int i, int i2);

    private native void nativeSetRenderOptions(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void nativeSetThreadWasPaused(int i);

    public native int nativeCreateAvatarManifestEditor(int i, int i2);

    public native int nativeCreateRandomManifest(int i, int i2);

    static {
        m_loadedNativeLibrary = false;
        try {
            System.loadLibrary("core2main");
            m_loadedNativeLibrary = true;
        } catch (UnsatisfiedLinkError e) {
            m_loadedNativeLibrary = false;
        }
    }

    public int GetInstanceID() {
        return this.m_iInstanceID;
    }

    public void lockRendererState() {
        try {
            this.mRendererStateLock.acquire();
        } catch (InterruptedException e) {
        }
    }

    public void unLockRendererState() {
        this.mRendererStateLock.release();
    }

    public void lockRenderer() {
        try {
            this.mRendererLock.acquire();
        } catch (InterruptedException e) {
        }
    }

    public void unLockRenderer() {
        this.mRendererLock.release();
    }

    public void graphicsContextLost(int iMode) {
        lockRendererState();
        this.m_iContextLost = iMode;
        unLockRendererState();
    }

    public void graphicsContextLost() {
        graphicsContextLost(0);
    }

    public void captureScreen(String screenShootFileName) {
        lockRendererState();
        this.m_ScreenShotName = screenShootFileName;
        unLockRendererState();
    }

    public boolean didNativeLibraryLoad() {
        return m_loadedNativeLibrary;
    }

    public int initialize(AssetManager assetManagerIn, String _cachePath) {
        if (m_loadedNativeLibrary) {
            this.cachePath = _cachePath;
            if (this.cachePath != null) {
                this.cachePath += "/avatars";
                new File(this.cachePath).mkdir();
                clearCacheIfNecessary();
                this.m_assetManager = assetManagerIn;
                this.m_iInstanceID = nativeInit(assetManagerIn, this.cachePath);
            }
        }
        return this.m_iInstanceID;
    }

    public void detachView(GLSurfaceView view) {
        if (this.m_view == view) {
            this.m_view.detachRenderer();
            graphicsContextLost();
            this.m_view = null;
        }
    }

    public void attachView(Core2View view) {
        if (!(this.m_view == null || this.m_view == view)) {
            detachView(this.m_view);
        }
        this.m_view = view;
        if (this.m_view != null) {
            this.m_view.attachRenderer(this);
        }
    }

    private void clearCacheIfNecessary() {
        if (this.cachePath != null && new Random().nextInt(10) <= 2) {
            int totalSize = 0;
            for (File theFile : new File(this.cachePath).listFiles()) {
                totalSize = (int) (((long) totalSize) + theFile.length());
            }
            if (totalSize > 10000000) {
                clearCache();
            }
        }
    }

    public void setBackgroundColor(int rgbaColor) {
        nativeSetBackgroundColor(this.m_iInstanceID, rgbaColor);
    }

    public void setGLThreadRunning(boolean running) {
        if (this.m_view != null) {
            this.glThreadRunning = running;
            this.m_view.setRenderMode(running ? 1 : 0);
            if (!running) {
                nativeSetThreadWasPaused(this.m_iInstanceID);
            }
        }
    }

    public float getFrameRate() {
        return nativeGetFrameRate(this.m_iInstanceID);
    }

    public boolean getGLThreadRunning() {
        return this.glThreadRunning;
    }

    public void DisableWebAccess(Boolean bDisable) {
        this.m_bDisableWebAccess = bDisable;
        clearCache();
    }

    public void setMaxFrameRate(int maxFramesPerSecond) {
        if (maxFramesPerSecond == 0) {
            this.m_minFrameTimeMS = -1;
        } else {
            this.m_minFrameTimeMS = (long) (EDSV2MediaType.MEDIATYPE_MOVIE / maxFramesPerSecond);
        }
    }

    public void setRenderOptions(RenderOptions options) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7 = 0;
        int i8 = this.m_iInstanceID;
        if (options.bUseColorPerVertex.booleanValue()) {
            i = 1;
        } else {
            i = 0;
        }
        int i9 = options.bDoPerVertexShading.booleanValue() ? 1 : 0;
        if (options.bUseRimLighting.booleanValue()) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        if (options.bHarwareSkinning.booleanValue()) {
            i3 = 1;
        } else {
            i3 = 0;
        }
        int i10 = options.bAllowMipmaps.booleanValue() ? 1 : 0;
        if (options.bCullDegenerateTrianglesAndUnusedVertecies.booleanValue()) {
            i4 = 1;
        } else {
            i4 = 0;
        }
        if (options.bUsePackedBoneMatricies.booleanValue()) {
            i5 = 1;
        } else {
            i5 = 0;
        }
        if (options.bCullTextures.booleanValue()) {
            i6 = 1;
        } else {
            i6 = 0;
        }
        if (options.bSingleBoneOptimization.booleanValue()) {
            i7 = 1;
        }
        nativeSetRenderOptions(i8, i, i9, i2, i3, i10, i4, i5, i6, i7);
    }

    protected InputStream getInputStream(String url) {
        InputStream stream = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
            HttpConnectionParams.setSoTimeout(httpParameters, 15000);
            HttpConnectionParams.setSocketBufferSize(httpParameters, AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYES);
            HttpResponse response = new DefaultHttpClient(httpParameters).execute(httpGet);
            if (!(response == null || response.getStatusLine() == null || response.getEntity() == null || response.getStatusLine().getStatusCode() != 200)) {
                stream = response.getEntity().getContent();
            }
        } catch (Exception e) {
        }
        return stream;
    }

    public KernelScriptingHelper createKernelScriptingHelper() {
        return createKernelScriptingHelper(null);
    }

    public KernelScriptingHelper createKernelScriptingHelper(Story story) {
        int iHelperIndex = nativeCreateKernelScriptingHelper(this.m_iInstanceID, story != null ? story.getInstanceID() : -1);
        if (iHelperIndex >= 0) {
            return new KernelScriptingHelper(this, iHelperIndex);
        }
        return null;
    }

    public AvatarEditor createAvatarEditor() {
        int iAE = nativeCreateAvatarEditor(this.m_iInstanceID);
        if (iAE >= 0) {
            return new AvatarEditor(this, iAE);
        }
        return null;
    }

    public AvatarEditorScriptingHelper createAvatarEditorScriptingHelper(KernelScriptingHelper KSH) {
        if (KSH == null) {
            return null;
        }
        int iAESH = nativeCreateAvatarEditorScriptingHelper(this.m_iInstanceID, KSH.getInstanceID());
        if (iAESH >= 0) {
            return new AvatarEditorScriptingHelper(this, iAESH);
        }
        return null;
    }

    public AvatarEditorFunctionSet createAvatarEditorFunctionSet(AvatarEditor avatarEditor, String avatarName) {
        if (avatarEditor == null) {
            return null;
        }
        int iAEFS = nativeCreateAvatarEditorFunctionSet(this.m_iInstanceID, avatarEditor.getInstanceID(), avatarName);
        if (iAEFS >= 0) {
            return new AvatarEditorFunctionSet(this, iAEFS);
        }
        return null;
    }

    public AvatarManifestEditor createAvatarManifestEditor(AvatarManifest avatarManifest) {
        if (avatarManifest == null) {
            return null;
        }
        int iAME = nativeCreateAvatarManifestEditor(this.m_iInstanceID, avatarManifest.getInstanceID());
        if (iAME >= 0) {
            return new AvatarManifestEditor(this, iAME);
        }
        return null;
    }

    public int purgeScene() {
        return nativePurgeScene(this.m_iInstanceID);
    }

    public int getCarryableBoundingBox(String propVariableName, Vector3 boundingBoxMin, Vector3 boundingBoxMax) {
        int hr = -1;
        float[] bBox6 = new float[6];
        if (bBox6 != null) {
            hr = nativeGetCarryableBoundingBox(this.m_iInstanceID, propVariableName, bBox6);
            if (hr >= 0) {
                boundingBoxMin.x = bBox6[0];
                boundingBoxMin.y = bBox6[1];
                boundingBoxMin.z = bBox6[2];
                boundingBoxMax.x = bBox6[3];
                boundingBoxMax.y = bBox6[4];
                boundingBoxMax.z = bBox6[5];
            }
        }
        return hr;
    }

    public AvatarManifest createManifestFromBinary(byte[] manifestBytes) {
        if (manifestBytes == null) {
            return null;
        }
        int iManifest = nativeCreateManifestFromBinary(this.m_iInstanceID, manifestBytes);
        if (iManifest >= 0) {
            return new AvatarManifest(this, iManifest);
        }
        return null;
    }

    public AvatarManifest createManifestFromHex(String manifestHex) {
        if (manifestHex == null) {
            return null;
        }
        int iManifest = nativeCreateManifestFromHex(this.m_iInstanceID, manifestHex);
        if (iManifest >= 0) {
            return new AvatarManifest(this, iManifest);
        }
        return null;
    }

    public int unregisterCallBack(Core2Callbacks cb) {
        int hr = -1;
        int len = this.m_callBackList != null ? this.m_callBackList.length : 0;
        for (int i = 0; i < len; i++) {
            if (this.m_callBackList[i] == cb) {
                this.m_callBackList[i] = null;
                hr = 0;
            }
        }
        return hr;
    }

    public int registerCallBack(Core2Callbacks cb) {
        if (cb == null) {
            return -1;
        }
        int len = this.m_callBackList != null ? this.m_callBackList.length : 0;
        int i = 0;
        while (i < len) {
            if (this.m_callBackList[i] == null || this.m_callBackList[i] == cb) {
                this.m_callBackList[i] = cb;
                return i;
            }
            i++;
        }
        Core2Callbacks[] newcallBackList = new Core2Callbacks[((len * 3) + 3)];
        for (i = 0; i < len; i++) {
            newcallBackList[i] = this.m_callBackList[i];
        }
        this.m_callBackList = newcallBackList;
        this.m_callBackList[len] = cb;
        return len;
    }

    protected Core2Callbacks getCallBack(int iCB) {
        if (this.m_callBackList == null || iCB < 0 || iCB >= this.m_callBackList.length) {
            return null;
        }
        return this.m_callBackList[iCB];
    }

    public void kernelNotifyCallback(int iContext, int iCB) {
        Core2Callbacks notifyTarget = getCallBack(iCB);
        if (notifyTarget != null) {
            notifyTarget.onNotify(iContext);
        }
    }

    public void avatarEditorHandlerInvoke(int iAEEE, int iCB) {
        Core2Callbacks notifyTarget = getCallBack(iCB);
        if (notifyTarget != null) {
            notifyTarget.invokeAvatarEditorEditEvent(new AvatarEditorEditEvent(this, iAEEE));
        }
    }

    public void scriptExceptionInvoke(int iSE, int iCB) {
        Core2Callbacks notifyTarget = getCallBack(iCB);
        if (notifyTarget != null) {
            notifyTarget.invokeScriptException(new ScriptException(this, iSE));
        }
    }

    public int doSyncDownload(String url, String cacheFileName) {
        if (Boolean.valueOf(url.startsWith("http://")).booleanValue()) {
            return doSyncHttpDownload(url, cacheFileName);
        }
        return doSyncAssetGrab(url, cacheFileName);
    }

    private void clearCache() {
        for (File theFile : new File(this.cachePath).listFiles()) {
            theFile.delete();
        }
    }

    private int doSyncAssetGrab(String url, String cacheFileName) {
        int nBytesRead = 0;
        if (!(this.m_bDisableWebAccess.booleanValue() || this.m_assetManager == null || url == null || cacheFileName == null)) {
            try {
                InputStream in = this.m_assetManager.open(url);
                FileOutputStream f = new FileOutputStream(cacheFileName);
                byte[] buffer = new byte[1024];
                while (true) {
                    int len1 = in.read(buffer);
                    if (len1 <= 0) {
                        break;
                    }
                    f.write(buffer, 0, len1);
                    nBytesRead += len1;
                }
                buffer[0] = (byte) (nBytesRead & 255);
                buffer[1] = (byte) ((nBytesRead >> 8) & 255);
                buffer[2] = (byte) ((nBytesRead >> 16) & 255);
                buffer[3] = (byte) 0;
                f.write(buffer, 0, 4);
                f.close();
            } catch (Exception e) {
            }
        }
        return nBytesRead;
    }

    private int doSyncHttpDownload(String url, String cacheFileName) {
        int nBytesRead = 0;
        if (!this.m_bDisableWebAccess.booleanValue()) {
            InputStream stream = getInputStream(url);
            if (stream != null) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream(AvatarEditorModel.AVATAREDIT_OPTION_RINGS);
                    byte[] b = new byte[AvatarEditorModel.AVATAREDIT_OPTION_RINGS];
                    while (true) {
                        int read = stream.read(b);
                        if (read == -1) {
                            break;
                        }
                        out.write(b, 0, read);
                        nBytesRead += read;
                    }
                    b[0] = (byte) (nBytesRead & 255);
                    b[1] = (byte) ((nBytesRead >> 8) & 255);
                    b[2] = (byte) ((nBytesRead >> 16) & 255);
                    b[3] = (byte) 0;
                    out.write(b, 0, 4);
                    out.flush();
                    out.close();
                    byte[] raw = out.toByteArray();
                    if (cacheFileName != null) {
                        FileOutputStream fileOut = new FileOutputStream(cacheFileName);
                        fileOut.write(raw);
                        fileOut.flush();
                        fileOut.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return nBytesRead;
    }

    public AvatarManifest createRandomManifest(AVATAR_BODY_TYPE type) {
        if (type == null) {
            return null;
        }
        int iInstOut = nativeCreateRandomManifest(this.m_iInstanceID, type.getInt());
        if (iInstOut >= 0) {
            return new AvatarManifest(this, iInstOut);
        }
        return null;
    }

    public void encodeImageFile(int[] pixles, int width, int height, int nChannels, String fileNameOut) {
        Bitmap newBmp = Bitmap.createBitmap(pixles, 0, width, width, height, Config.ARGB_8888);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        newBmp.compress(CompressFormat.PNG, 80, os);
        try {
            os.writeTo(new FileOutputStream(new File(fileNameOut)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void doCaptureScreenShot(String screenShotName) {
        int[] imageSizes = new int[3];
        int[] pixels = nativeCaptureScreenShot(this.m_iInstanceID, imageSizes);
        if (pixels != null) {
            String fullPath = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append("/xboxScreenShots").toString();
            String fullPathAndName = new StringBuilder(String.valueOf(fullPath)).append("/").append(screenShotName).toString();
            new File(fullPath).mkdir();
            encodeImageFile(pixels, imageSizes[0], imageSizes[1], imageSizes[2], fullPathAndName);
        }
    }

    private void throttleFrameRate() {
        if (this.m_startTime == 0) {
            this.m_startTime = System.currentTimeMillis();
        }
        long endTime = System.currentTimeMillis();
        long delta = endTime - this.m_startTime;
        this.m_startTime = endTime;
        int index = this.m_frameCount % 6;
        this.m_TimeTotal -= this.m_TimeHistory[index];
        this.m_TimeHistory[index] = delta;
        this.m_TimeTotal += delta;
        if (this.m_frameCount > 40 && this.m_minFrameTimeMS > 0) {
            long deltaAve = this.m_TimeTotal / 6;
            if (deltaAve < this.m_minFrameTimeMS) {
                try {
                    long sleepTime = this.m_minFrameTimeMS - deltaAve;
                    Thread.sleep(sleepTime);
                    this.m_startTime += sleepTime;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private void displayMemoryFootprint() {
        if (this.m_frameCount % 200 == 0) {
            MemoryInfo memoryInfo = new MemoryInfo();
            Debug.getMemoryInfo(memoryInfo);
            long nativeHeap = Debug.getNativeHeapAllocatedSize();
            int usedKb = memoryInfo.getTotalPss();
            int nativeKb = memoryInfo.nativePss;
            Log.e("Core2View", "MEM USAGE: " + nativeHeap + " : " + usedKb + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + nativeKb + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + memoryInfo.otherPss);
        }
    }

    public void forceShutdown() {
        if (this.m_view != null) {
            Log.e("Core2View", "Core2 DESTROY Pre DoPause");
            this.m_view.doPause();
            Log.e("Core2View", "Core2 DESTROY PostDoPause");
        }
    }

    public void destroy() {
        Log.e("Core2View", "Core2 DESTROY TOP");
        lockRenderer();
        Log.e("Core2View", "Core2 DESTROY Calling nativeDestroy from detroy call PRE");
        nativeDestroy(this.m_iInstanceID);
        Log.e("Core2View", "Core2 DESTROY Calling nativeDestroy from detroy call POST");
        unLockRenderer();
    }

    public void nativeRender() {
        this.m_frameCount++;
        throttleFrameRate();
        int iPurge = -1;
        String screenShotName = null;
        lockRendererState();
        if (this.m_iContextLost >= 0) {
            iPurge = this.m_iContextLost;
            this.m_iContextLost = -1;
        }
        if (this.m_ScreenShotName != null) {
            screenShotName = this.m_ScreenShotName;
            this.m_ScreenShotName = null;
        }
        unLockRendererState();
        lockRenderer();
        if (iPurge >= 0) {
            nativePurgeGrapicsContext(this.m_iInstanceID, iPurge);
        }
        nativeRender(this.m_iInstanceID);
        if (screenShotName != null) {
            doCaptureScreenShot(screenShotName);
        }
        unLockRenderer();
    }

    public void nativeResize(int width, int height) {
        nativeResize(this.m_iInstanceID, width, height);
    }
}
