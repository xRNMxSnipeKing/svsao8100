package com.xbox.avatarrenderer;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.opengl.GLSurfaceView.EGLContextFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class Core2View extends GLSurfaceView {
    private static String TAG = "Core2View";
    private ConfigChooser m_chooser = null;
    private Core2Renderer m_core2;
    private int m_iOpenGLESVersion = 0;
    private Boolean m_isPaused = Boolean.valueOf(false);

    private static class ConfigChooser implements EGLConfigChooser {
        private static int EGL_OPENGL_ES1_BIT = 1;
        private static int EGL_OPENGL_ES2_BIT = 4;
        private static int[] s_configAttribs1 = new int[]{12324, 5, 12323, 6, 12322, 5, 12325, 16, 12352, EGL_OPENGL_ES1_BIT, 12344};
        private static int[] s_configAttribs2_NO_aa = new int[]{12324, 5, 12323, 6, 12322, 5, 12325, 16, 12352, EGL_OPENGL_ES2_BIT, 12344};
        private static int[] s_configAttribs2_msaa = new int[]{12324, 5, 12323, 6, 12322, 5, 12325, 16, 12352, EGL_OPENGL_ES2_BIT, 12338, 1, 12337, 2, 12344};
        protected int mAlphaSize;
        protected int mBlueSize;
        protected Boolean mFail = Boolean.valueOf(false);
        protected int mGreenSize;
        protected int mMSAA_Samples;
        protected Boolean mPrintConfigs = Boolean.valueOf(false);
        protected int mRedSize;
        private int[] mValue = new int[1];

        public ConfigChooser(int r, int g, int b, int a, int nMSAA_Samples) {
            this.mRedSize = r;
            this.mGreenSize = g;
            this.mBlueSize = b;
            this.mAlphaSize = a;
            this.mMSAA_Samples = nMSAA_Samples;
        }

        public Boolean failed() {
            return this.mFail;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int numConfigs;
            int[] numConfigOut = new int[1];
            int[] configAttributes = s_configAttribs2_msaa;
            int numConfigs2 = 0;
            if (this.mMSAA_Samples > 0) {
                egl.eglChooseConfig(display, configAttributes, null, 0, numConfigOut);
                numConfigs2 = numConfigOut[0];
            }
            if (numConfigs2 < 1) {
                this.mMSAA_Samples = -1;
                configAttributes = s_configAttribs2_NO_aa;
                egl.eglChooseConfig(display, configAttributes, null, 0, numConfigOut);
                numConfigs2 = numConfigOut[0];
            }
            if (numConfigs2 < 1) {
                configAttributes = s_configAttribs1;
                egl.eglChooseConfig(display, configAttributes, null, 0, numConfigOut);
                numConfigs = numConfigOut[0];
                this.mFail = Boolean.valueOf(true);
            } else {
                numConfigs = numConfigs2;
            }
            if (numConfigs < 1) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, configAttributes, configs, numConfigs, numConfigOut);
            if (this.mPrintConfigs.booleanValue()) {
                printConfigs(egl, display, configs);
            }
            EGLConfig config = chooseConfig(egl, display, configs);
            if (config == null) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            if (this.mPrintConfigs.booleanValue()) {
                Log.w(Core2View.TAG, "Choosen COnfig:::::::");
                printConfig(egl, display, config);
            }
            return config;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                int r = findConfigAttrib(egl, display, config, 12324, 0);
                int g = findConfigAttrib(egl, display, config, 12323, 0);
                int b = findConfigAttrib(egl, display, config, 12322, 0);
                int a = findConfigAttrib(egl, display, config, 12321, 0);
                if (r == this.mRedSize && g == this.mGreenSize && b == this.mBlueSize && a == this.mAlphaSize) {
                    return config;
                }
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(display, config, attribute, this.mValue)) {
                return this.mValue[0];
            }
            return defaultValue;
        }

        private void printConfigs(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            Log.w(Core2View.TAG, String.format("%d configurations", new Object[]{Integer.valueOf(configs.length)}));
            for (EGLConfig printConfig : configs) {
                Log.w(Core2View.TAG, String.format("Configuration %d:\n", new Object[]{Integer.valueOf(i)}));
                printConfig(egl, display, printConfig);
            }
        }

        private void printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] attributes = new int[]{12320, 12321, 12322, 12323, 12324, 12325, 12326, 12327, 12328, 12329, 12330, 12331, 12332, 12333, 12334, 12335, 12336, 12337, 12338, 12339, 12340, 12343, 12342, 12341, 12345, 12346, 12347, 12348, 12349, 12350, 12351, 12352, 12354};
            String[] names = new String[]{"EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES", "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT"};
            int[] value = new int[1];
            for (int i = 0; i < attributes.length; i++) {
                int attribute = attributes[i];
                String name = names[i];
                if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
                    Log.w(Core2View.TAG, String.format("  %s: %d\n", new Object[]{name, Integer.valueOf(value[0])}));
                } else if (egl.eglGetError() != 12288) {
                    Log.w(Core2View.TAG, String.format("  %s: failed\n", new Object[]{name}));
                }
            }
        }
    }

    private static class ContextFactory implements EGLContextFactory {
        private static int EGL_CONTEXT_CLIENT_VERSION = 12440;
        public static int m_iOpenGLESVersion = 2;

        private ContextFactory() {
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            Core2View.checkEglError("Before eglCreateContext", egl);
            EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{EGL_CONTEXT_CLIENT_VERSION, m_iOpenGLESVersion, 12344});
            Core2View.checkEglError("After eglCreateContext", egl);
            return context;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    public class Renderer implements android.opengl.GLSurfaceView.Renderer {
        private Core2View m_glView = null;

        public Renderer(Core2View glView) {
            this.m_glView = glView;
        }

        public void onDrawFrame(GL10 gl) {
            this.m_glView.nativeRender();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            this.m_glView.nativeResize(width, height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }
    }

    public Core2View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Core2View(Context context) {
        super(context);
    }

    public Boolean initialize(Context context, Core2Renderer core2, Boolean bTranslucent, Boolean bMSAA) {
        Boolean bOK = Boolean.valueOf(false);
        this.m_iOpenGLESVersion = ((ActivityManager) context.getSystemService("activity")).getDeviceConfigurationInfo().reqGlEsVersion >= AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES ? 2 : 1;
        init(bTranslucent.booleanValue(), bMSAA.booleanValue());
        if (this.m_iOpenGLESVersion != 2) {
            return bOK;
        }
        bOK = Boolean.valueOf(true);
        this.m_core2 = core2;
        this.m_core2.attachView(this);
        return bOK;
    }

    public void doPause() {
        if (!this.m_isPaused.booleanValue()) {
            onPause();
        }
    }

    public void onResume() {
        this.m_isPaused = Boolean.valueOf(false);
        if (this.m_core2 != null) {
            this.m_core2.setGLThreadRunning(true);
        }
        super.onResume();
    }

    public void onPause() {
        this.m_isPaused = Boolean.valueOf(true);
        Log.e("Core2View", "Core2View : OnPause TOP");
        if (this.m_core2 != null) {
            this.m_core2.setGLThreadRunning(false);
            this.m_core2.graphicsContextLost();
        }
        Log.e("Core2View", "Core2View : OnPause Pre super OnPuaseA");
        super.onPause();
        Log.e("Core2View", "Core2View : OnPause Post Super OnPause");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    public void detachRenderer() {
        this.m_core2 = null;
    }

    public void attachRenderer(Core2Renderer core2) {
        this.m_core2 = core2;
    }

    public void nativeRender() {
        if (this.m_chooser != null) {
            if (this.m_chooser.failed().booleanValue()) {
                detachRenderer();
            }
            this.m_chooser = null;
        }
        if (this.m_core2 != null) {
            this.m_core2.nativeRender();
        }
    }

    public void nativeResize(int width, int height) {
        if (this.m_core2 != null) {
            this.m_core2.nativeResize(width, height);
        }
    }

    private void init(boolean translucent, boolean bMSAA) {
        ConfigChooser configChooser;
        setEGLContextClientVersion(this.m_iOpenGLESVersion);
        int nMSAASamples = bMSAA ? 2 : -1;
        if (translucent) {
            getHolder().setFormat(-3);
            setZOrderOnTop(true);
        }
        ContextFactory.m_iOpenGLESVersion = this.m_iOpenGLESVersion;
        setEGLContextFactory(new ContextFactory());
        if (translucent) {
            configChooser = new ConfigChooser(8, 8, 8, 8, nMSAASamples);
        } else {
            configChooser = new ConfigChooser(5, 6, 5, 0, nMSAASamples);
        }
        this.m_chooser = configChooser;
        setEGLConfigChooser(this.m_chooser);
        setRenderer(new Renderer(this));
    }

    private static void checkEglError(String prompt, EGL10 egl) {
        for (int iTry = 0; iTry < 10 && egl.eglGetError() != 12288; iTry++) {
            Log.e(TAG, String.format("%s: EGL error: 0x%x", new Object[]{prompt, Integer.valueOf(error)}));
        }
    }
}
