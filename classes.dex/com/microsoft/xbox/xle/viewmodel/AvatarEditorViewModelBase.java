package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.activity.MainPivotActivity;

public abstract class AvatarEditorViewModelBase extends ViewModelBase {
    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        boolean kickOutOfEditor = false;
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case AvatarEditorLoadedAsset:
                if (asyncResult.getException() != null && asyncResult.getException().getErrorCode() == XLEErrorCode.FAILED_UNEXPECTED_CORE2_RUNTIME_ERROR) {
                    kickOutOfEditor = true;
                    break;
                }
        }
        if (kickOutOfEditor) {
            XLEGlobalData.getInstance().setAvatarEditorCrashed(true);
            try {
                NavigationManager.getInstance().GotoScreenWithPop(MainPivotActivity.class);
            } catch (XLEException e) {
                XLELog.Error("Failed to navigate", e.toString());
            }
        }
    }
}
