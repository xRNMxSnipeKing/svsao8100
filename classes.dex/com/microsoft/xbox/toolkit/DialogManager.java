package com.microsoft.xbox.toolkit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.microsoft.xbox.toolkit.ui.BlockingScreen;
import com.microsoft.xbox.toolkit.ui.CancellableBlockingScreen;
import com.microsoft.xbox.toolkit.ui.appbar.ExpandedAppBar;
import java.util.Stack;

public class DialogManager {
    private ExpandedAppBar appBarMenu;
    private BlockingScreen blockingSpinner;
    private CancellableBlockingScreen cancelableBlockingDialog;
    private Stack<XLEDialog> dialogStack;
    private boolean isEnabled;
    private Toast visibleToast;

    private static class DialogManagerHolder {
        public static final DialogManager instance = new DialogManager();

        private DialogManagerHolder() {
        }
    }

    private enum DialogType {
        FATAL,
        NON_FATAL,
        NORMAL
    }

    private class XLEDialog extends AlertDialog {
        private DialogType dialogType = DialogType.NORMAL;

        protected XLEDialog(Context context) {
            super(context);
        }

        protected void setDialogType(DialogType type) {
            this.dialogType = type;
        }

        protected DialogType getDialogType() {
            return this.dialogType;
        }

        public void dismiss() {
            DialogManager.this.dialogStack.remove(this);
            super.dismiss();
        }
    }

    private DialogManager() {
        this.dialogStack = new Stack();
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
    }

    public static DialogManager getInstance() {
        return DialogManagerHolder.instance;
    }

    public AlertDialog getVisibleDialog() {
        return null;
    }

    public boolean getIsBlocking() {
        return (this.blockingSpinner != null && this.blockingSpinner.isShowing()) || (this.cancelableBlockingDialog != null && this.cancelableBlockingDialog.isShowing());
    }

    public void setEnabled(boolean value) {
        if (this.isEnabled != value) {
            this.isEnabled = value;
        }
    }

    public void showFatalAlertDialog(String title, String promptText, String okText, Runnable okHandler) {
        forceDismissAll();
        if (this.isEnabled) {
            XLEDialog dialog = buildDialog(title, promptText, okText, okHandler, null, null);
            dialog.setDialogType(DialogType.FATAL);
            this.dialogStack.push(dialog);
            dialog.show();
        }
    }

    public void showNonFatalAlertDialog(String title, String promptText, String okText, Runnable okHandler) {
        if (this.dialogStack.size() > 0) {
            XLELog.Error("DialogManager", "there are visible dialog at this point");
        }
        if (this.isEnabled) {
            XLEDialog dialog = buildDialog(title, promptText, okText, okHandler, null, null);
            dialog.setDialogType(DialogType.NON_FATAL);
            this.dialogStack.push(dialog);
            dialog.show();
        }
    }

    public void showOkCancelDialog(String title, String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        XLEAssert.assertNotNull("You must supply cancel text if this is not a must-act dialog.", cancelText);
        if (this.dialogStack.size() > 0) {
            XLELog.Error("ViewModelBase", "there are visible dialog at this point, dismiss it first!");
        } else if (this.isEnabled) {
            XLEDialog dialog = buildDialog(title, promptText, okText, okHandler, cancelText, cancelHandler);
            dialog.setDialogType(DialogType.NORMAL);
            this.dialogStack.push(dialog);
            dialog.show();
        }
    }

    public void showToast(int contentResId) {
        dismissToast();
        if (this.isEnabled) {
            this.visibleToast = Toast.makeText(XboxApplication.MainActivity, contentResId, 1);
            this.visibleToast.show();
        }
    }

    public void setBlocking(boolean visible, String statusText) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!this.isEnabled) {
            return;
        }
        if (visible) {
            if (this.blockingSpinner == null) {
                XLELog.Diagnostic("DialogManager", "blocking spinner null, create new one");
                this.blockingSpinner = new BlockingScreen(XboxApplication.MainActivity);
            }
            this.blockingSpinner.show(XboxApplication.MainActivity, statusText);
        } else if (this.blockingSpinner != null) {
            this.blockingSpinner.dismiss();
            this.blockingSpinner = null;
        }
    }

    public void setCancelableBlocking(boolean visible, String statusText, final Runnable cancelRunnable) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!this.isEnabled) {
            return;
        }
        if (visible) {
            if (this.cancelableBlockingDialog == null) {
                XLELog.Diagnostic("DialogManager", "cancelable blocking dialog null, create new one");
                this.cancelableBlockingDialog = new CancellableBlockingScreen(XboxApplication.MainActivity);
                this.cancelableBlockingDialog.setCancelButtonAction(new OnClickListener() {
                    public void onClick(View v) {
                        DialogManager.this.cancelableBlockingDialog.dismiss();
                        DialogManager.this.cancelableBlockingDialog = null;
                        cancelRunnable.run();
                    }
                });
            }
            this.cancelableBlockingDialog.show(XboxApplication.MainActivity, statusText);
        } else if (this.cancelableBlockingDialog != null) {
            this.cancelableBlockingDialog.dismiss();
            this.cancelableBlockingDialog = null;
        }
    }

    public void showAppBarMenu(ExpandedAppBar appBar) {
        if (this.isEnabled) {
            this.appBarMenu = appBar;
            this.appBarMenu.show();
        }
    }

    public ExpandedAppBar getAppBarMenu() {
        return this.appBarMenu;
    }

    public void onAppBarDismissed() {
        this.appBarMenu = null;
    }

    public void forceDismissAll() {
        dismissToast();
        forceDismissAlerts();
        dismissBlocking();
        dismissAppBar();
    }

    public void dismissToast() {
        if (this.visibleToast != null) {
            this.visibleToast.cancel();
            this.visibleToast = null;
        }
    }

    public void forceDismissAlerts() {
        while (this.dialogStack.size() > 0) {
            ((XLEDialog) this.dialogStack.pop()).dismiss();
        }
    }

    public void dismissTopNonFatalAlert() {
        if (this.dialogStack.size() > 0 && ((XLEDialog) this.dialogStack.peek()).getDialogType() != DialogType.FATAL) {
            ((XLEDialog) this.dialogStack.pop()).dismiss();
        }
    }

    public void dismissBlocking() {
        if (this.blockingSpinner != null) {
            this.blockingSpinner.dismiss();
            this.blockingSpinner = null;
        }
        if (this.cancelableBlockingDialog != null) {
            this.cancelableBlockingDialog.dismiss();
            this.cancelableBlockingDialog = null;
        }
    }

    public void dismissAppBar() {
        if (this.appBarMenu != null) {
            this.appBarMenu.dismiss();
            this.appBarMenu = null;
        }
    }

    private XLEDialog buildDialog(String title, String promptText, String okText, final Runnable okHandler, String cancelText, final Runnable cancelHandler) {
        XLEDialog dialog = new XLEDialog(XboxApplication.MainActivity);
        dialog.setTitle(title);
        dialog.setMessage(promptText);
        dialog.setButton(-1, okText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                ThreadManager.UIThreadPost(okHandler);
            }
        });
        dialog.setButton(-2, cancelText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                ThreadManager.UIThreadPost(cancelHandler);
            }
        });
        if (cancelText == null || cancelText.length() == 0) {
            dialog.setCancelable(false);
        } else {
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    ThreadManager.UIThreadPost(cancelHandler);
                }
            });
        }
        return dialog;
    }
}
