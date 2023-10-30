package com.microsoft.xbox.xle.viewmodel;

import android.graphics.Point;
import android.view.Display;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.toolkit.ApplicationSettingManager;
import com.microsoft.xbox.toolkit.MemoryMonitor;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xbox.xle.app.activity.XboxAuthActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import com.xbox.avatarrenderer.Core2Renderer;
import java.util.ArrayList;
import java.util.Iterator;

public class SystemCheckActivityViewModel extends ViewModelBase {
    public static final int MIN_DALVIK_MEMORY_CLASS_MB = 64;
    private static final int MIN_SCREEN_HEIGHT = 800;
    private static final int MIN_SCREEN_WIDTH = 480;
    private static final int MIN_SDK_INT = 14;
    private static final int PREMIUM_SDK_INT = 9;
    private boolean allPassed;
    private SystemCheckTest currentTest;
    private boolean isRunningTest;
    private ArrayList<SystemCheckTest> tests;
    private boolean timerFired;
    private ArrayList<SystemCheckTest> warningTests;

    private class SystemCheckTest {
        private TestAction action;
        private boolean hasRun;
        private boolean lastRunResult;
        private String name;
        private int resourceId;

        public SystemCheckTest(String name, int resourceId, TestAction action) {
            this.name = name;
            this.resourceId = resourceId;
            this.action = action;
        }

        public String getTestName() {
            return this.name;
        }

        public boolean getTestResult() {
            return this.lastRunResult;
        }

        public int getResourceId() {
            return this.resourceId;
        }

        public boolean run() {
            this.hasRun = true;
            this.lastRunResult = false;
            try {
                if (this.action != null) {
                    this.lastRunResult = this.action.invoke();
                }
            } catch (Exception e) {
            }
            return this.lastRunResult;
        }
    }

    private abstract class TestAction {
        public abstract boolean invoke();
    }

    public SystemCheckActivityViewModel() {
        this.allPassed = true;
        this.isRunningTest = false;
        this.timerFired = false;
        this.adapter = AdapterFactory.getInstance().getSystemCheckAdapter(this);
        final boolean bootedAvatar = AvatarRendererModel.getInstance().initializeOOBE();
        this.tests = new ArrayList();
        this.warningTests = new ArrayList();
        this.warningTests.add(new SystemCheckTest("Check System Version for premium experience", 0, new TestAction() {
            public boolean invoke() {
                if (SystemUtil.getSdkInt() < 9) {
                    return false;
                }
                return true;
            }
        }));
        this.tests.add(new SystemCheckTest("Check System Version", 0, new TestAction() {
            public boolean invoke() {
                if (SystemUtil.getSdkInt() < 14) {
                    return false;
                }
                return true;
            }
        }));
        this.tests.add(new SystemCheckTest("Check Memory Class", 0, new TestAction() {
            public boolean invoke() {
                XLELog.Diagnostic("SystemCheck", String.format("Current memory class is %d;  minimum required is %d", new Object[]{Integer.valueOf(MemoryMonitor.instance().getMemoryClass()), Integer.valueOf(64)}));
                if (MemoryMonitor.instance().getMemoryClass() >= 64) {
                    return true;
                }
                return false;
            }
        }));
        this.tests.add(new SystemCheckTest("Check Screen Width/Height", 0, new TestAction() {
            public boolean invoke() {
                Display display = XboxApplication.MainActivity.getWindowManager().getDefaultDisplay();
                Point displaySize = new Point();
                display.getSize(displaySize);
                XLELog.Diagnostic("SystemCheck", String.format("Current screen size in pixels: width = %d height = %d", new Object[]{Integer.valueOf(displaySize.x), Integer.valueOf(displaySize.y)}));
                if (displaySize.y < SystemCheckActivityViewModel.MIN_SCREEN_HEIGHT || displaySize.x < SystemCheckActivityViewModel.MIN_SCREEN_WIDTH) {
                    return false;
                }
                return true;
            }
        }));
        this.tests.add(new SystemCheckTest("Check Avatar Performance", 0, new TestAction() {
            public boolean invoke() {
                return bootedAvatar;
            }
        }));
    }

    public void onRehydrate() {
    }

    public String getTestName() {
        if (this.currentTest != null) {
            return this.currentTest.name;
        }
        return null;
    }

    public Core2Renderer getCore2Renderer() {
        return AvatarRendererModel.getInstance().getCore2Model();
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
    }

    public void onPause() {
        super.onPause();
        if (!this.allPassed || this.isRunningTest) {
            XLELog.Diagnostic("SystemCheckActivity", "onPause, kill the app");
            exitApp();
        }
    }

    public boolean isBusy() {
        return this.isRunningTest;
    }

    public void load(boolean forceRefresh) {
        ThreadManager.UIThreadPostDelayed(new Runnable() {
            public void run() {
                if (SystemCheckActivityViewModel.this.getIsActive()) {
                    SystemCheckActivityViewModel.this.checkTestDoneAndNavigate();
                }
            }
        }, 1000);
        runTests();
    }

    private void exitApp() {
        XboxApplication.MainActivity.finish();
        XboxApplication.Instance.killApp(true);
    }

    private void checkTestDoneAndNavigate() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!this.allPassed || this.isRunningTest) {
            XLELog.Diagnostic("SystemCheckActivityViewModel", "Timer fired, test not done, wait");
        } else {
            XLELog.Diagnostic("SystemCheckActivityViewModel", "Timer fired, all done, navigate away from timer");
            NavigateTo(XboxAuthActivity.class, false);
        }
        this.timerFired = true;
    }

    private void PassTest() {
        this.isRunningTest = false;
        this.allPassed = true;
        if (this.timerFired) {
            XLELog.Diagnostic("SystemCheckActivityViewModel", "all done, timer fired, navigate away");
            NavigateTo(XboxAuthActivity.class, false);
            return;
        }
        XLELog.Diagnostic("SystemCheckActivityViewModel", "wait for timer to fire");
    }

    private void runTests() {
        this.isRunningTest = true;
        Iterator i$ = this.tests.iterator();
        while (i$.hasNext()) {
            this.currentTest = (SystemCheckTest) i$.next();
            XLELog.Diagnostic("SystemCheck", "running test " + this.currentTest.name);
            if (this.currentTest.run()) {
                XLELog.Diagnostic("SystemCheck", "test passed:  " + this.currentTest.name);
            } else {
                XLELog.Diagnostic("SystemCheck", "test failed:  " + this.currentTest.name);
                this.allPassed = false;
            }
            this.adapter.updateView();
        }
        if (!AvatarRendererModel.getInstance().didNativeLibraryLoad()) {
            showMustActDialog(XboxApplication.Resources.getString(R.string.device_specs_fail_title), XboxApplication.Resources.getString(R.string.device_specs_cant_deflate_native_so), XboxApplication.Resources.getString(R.string.OK), new Runnable() {
                public void run() {
                    SystemCheckActivityViewModel.this.exitApp();
                }
            }, true);
        }
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.allPassed) {
            ApplicationSettingManager.getInstance().saveSystemCheckStatus(true);
            i$ = this.warningTests.iterator();
            while (i$.hasNext()) {
                this.currentTest = (SystemCheckTest) i$.next();
                XLELog.Diagnostic("SystemCheck", "running advisory test " + this.currentTest.name);
                if (this.currentTest.run()) {
                    XLELog.Diagnostic("SystemCheck", "advisory test passed:  " + this.currentTest.name);
                } else {
                    this.allPassed = false;
                    XLELog.Diagnostic("SystemCheck", "advisory test failed:  " + this.currentTest.name);
                }
                this.adapter.updateView();
            }
            if (this.allPassed) {
                PassTest();
                return;
            } else {
                showMustActDialog(XboxApplication.Resources.getString(R.string.device_specs_fail_title), XboxApplication.Resources.getString(R.string.device_version_not_optimal), XboxApplication.Resources.getString(R.string.OK), new Runnable() {
                    public void run() {
                        SystemCheckActivityViewModel.this.PassTest();
                    }
                }, true);
                return;
            }
        }
        showMustActDialog(XboxApplication.Resources.getString(R.string.device_specs_fail_title), XboxApplication.Resources.getString(R.string.device_specs_insufficient), XboxApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                SystemCheckActivityViewModel.this.exitApp();
            }
        }, true);
    }
}
