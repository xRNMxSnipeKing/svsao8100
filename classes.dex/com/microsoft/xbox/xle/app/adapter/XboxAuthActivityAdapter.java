package com.microsoft.xbox.xle.app.adapter;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.widget.ImageView;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.avatar.view.AvatarViewEditor;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.toolkit.Build;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.EasingMode;
import com.microsoft.xbox.toolkit.anim.ExponentialInterpolator;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment.Environment;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import com.microsoft.xbox.xle.viewmodel.XboxAuthActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XboxAuthActivityViewModel.LoginAnimationState;
import java.util.ArrayList;
import java.util.Iterator;

public class XboxAuthActivityAdapter extends AdapterBase {
    private ArrayList<Animator> animatorsToCleanup = new ArrayList();
    private AvatarViewEditor avatarViewEditor;
    private XLEButton cancelButton;
    private XLEButton enviromentSelectButton;
    private View errorView;
    private View gradientBackgroundView;
    private View greenBackgroundView;
    private Animator innerRingAnimator;
    private View innerRingView;
    private LoginAnimationState loginAnimationState;
    private XLEButton loginButton;
    private View loginButtonsView;
    private View loginLogoView;
    private TextView loginStatusText;
    private Animator middleRingAnimator;
    private View middleRingView;
    private Animator outerRingAnimator;
    private View outerRingView;
    private Animator showXenonAnimator;
    private AnimatorSet signedInAnimatorSet;
    private AnimatorSet signingInAnimationSet;
    private XboxAuthActivityViewModel viewModel;
    private WebView webView;
    private View welcomeArea;
    private ImageView xenonView;

    private final class EnvironmentSelectorListener implements OnClickListener {
        CharSequence[] enviromentNames = new CharSequence[this.enviroments.length];
        Environment[] enviroments = new Environment[]{Environment.STUB, Environment.VINT, Environment.PARTNERNET, Environment.PROD, Environment.DEV};

        private EnvironmentSelectorListener() {
            for (int i = 0; i < this.enviroments.length; i++) {
                this.enviromentNames[i] = this.enviroments[i].name();
            }
        }

        public void onClick(View v) {
            int currentSelection = -1;
            for (int i = 0; i < this.enviroments.length; i++) {
                if (this.enviroments[i] == XboxLiveEnvironment.Instance().getEnvironment()) {
                    currentSelection = i;
                }
            }
            Builder builder = new Builder(XboxApplication.MainActivity);
            builder.setTitle("Select an enviroment");
            builder.setSingleChoiceItems(this.enviromentNames, currentSelection, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (EnvironmentSelectorListener.this.enviroments[item] == Environment.STUB) {
                        XboxLiveEnvironment.Instance().setStub(true);
                    } else {
                        XboxLiveEnvironment.Instance().setStub(false);
                        if (EnvironmentSelectorListener.this.enviroments[item] == Environment.DEV) {
                            XboxLiveEnvironment.Instance().setEnvironment(Environment.VINT);
                            Build.useDevEndpointForXboxActivity = true;
                            CanvasView.IsSmartGlassStudioRunning = true;
                        } else {
                            XboxLiveEnvironment.Instance().setEnvironment(EnvironmentSelectorListener.this.enviroments[item]);
                        }
                    }
                    XboxAuthActivityAdapter.this.enviromentSelectButton.setText(XboxLiveEnvironment.Instance().getEnvironment().name());
                    XboxAuthActivityAdapter.this.viewModel.cancelLogin(true);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    public XboxAuthActivityAdapter(XboxAuthActivityViewModel vm) {
        this.viewModel = vm;
        this.webView = (WebView) findViewById(R.id.login_webview);
        this.webView.getSettings().setSaveFormData(false);
        this.webView.getSettings().setSavePassword(false);
        this.webView.requestFocus();
        this.welcomeArea = findViewById(R.id.welcomearea);
        this.errorView = findViewById(R.id.welcome_error);
        this.loginButton = (XLEButton) findViewById(R.id.welcome_login);
        this.cancelButton = (XLEButton) findViewById(R.id.welcome_cancel);
        this.loginStatusText = (TextView) findViewById(R.id.login_status_text);
        this.avatarViewEditor = (AvatarViewEditor) findViewById(R.id.login_dummy_gl_view);
        this.loginButton.setVisibility(0);
        this.cancelButton.setVisibility(4);
        this.loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XboxAuthActivityAdapter.this.viewModel.beginLogin();
            }
        });
        this.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XboxAuthActivityAdapter.this.viewModel.cancelLogin(true);
            }
        });
        this.gradientBackgroundView = findViewById(R.id.gradient_background);
        this.greenBackgroundView = findViewById(R.id.green_background);
        this.loginLogoView = findViewById(R.id.login_logo);
        this.loginButtonsView = findViewById(R.id.login_buttons);
        this.xenonView = (ImageView) findViewById(R.id.xenonStart);
        this.outerRingView = findViewById(R.id.outerRing);
        this.middleRingView = findViewById(R.id.middleRing);
        this.innerRingView = findViewById(R.id.innerRing);
        preloadAnimation();
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void updateViewOverride() {
        int i;
        this.loginStatusText.setText(this.viewModel.getLoadingText());
        View view = this.errorView;
        if (this.viewModel.getShowLoginError()) {
            i = 0;
        } else {
            i = 4;
        }
        view.setVisibility(i);
        if (this.viewModel.getWebviewVisible()) {
            this.webView.setVisibility(0);
            this.webView.requestFocus();
            this.welcomeArea.setVisibility(4);
        } else {
            this.webView.setVisibility(4);
            this.welcomeArea.setVisibility(0);
        }
        int newOrientation = this.viewModel.getWebviewVisible() ? 4 : 7;
        if (newOrientation != XLEApplication.MainActivity.getRequestedOrientation()) {
            XboxApplication.MainActivity.setRequestedOrientation(newOrientation);
        }
        this.avatarViewEditor.startDummyViewGLThread();
        updateLoginState(this.viewModel.getLoginAnimationState());
    }

    private void updateLoginState(LoginAnimationState loginState) {
        if (this.loginAnimationState != this.viewModel.getLoginAnimationState()) {
            this.loginAnimationState = this.viewModel.getLoginAnimationState();
            switch (this.loginAnimationState) {
                case SigningIn:
                    startSigningInAnimation();
                    return;
                case SignedIn:
                    startSignedInAnimation();
                    return;
                default:
                    return;
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        XLELog.Diagnostic("XboxAuthActivityAdapter", "onDestroy");
        this.loginButton.setOnClickListener(null);
        this.cancelButton.setOnClickListener(null);
        if (this.webView != null) {
            this.webView.stopLoading();
            this.webView.removeAllViews();
            this.webView.setWebChromeClient(null);
            this.webView.setWebViewClient(null);
            this.webView.destroy();
            this.webView = null;
        }
        WebViewDatabase webviewDatabase = WebViewDatabase.getInstance(XboxApplication.MainActivity);
        if (webviewDatabase != null) {
            try {
                webviewDatabase.clearUsernamePassword();
            } catch (Exception ex) {
                XLELog.Diagnostic("XboxAuthActivityAdapter", "can't clear the webivew database " + ex.toString());
            }
        }
        cleanupAnimator();
    }

    public void onPause() {
        super.onPause();
        if (this.avatarViewEditor != null) {
            this.avatarViewEditor.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.avatarViewEditor != null) {
            this.avatarViewEditor.onResume();
        }
    }

    public void onSetActive() {
        super.onSetActive();
        ThreadManager.UIThreadPostDelayed(new Runnable() {
            public void run() {
                if (!Build.IsAutoLoginDisabled && !XLEGlobalData.getInstance().getAutoLoginStarted()) {
                    XLEGlobalData.getInstance().setAutoLoginStarted(true);
                    XboxAuthActivityAdapter.this.viewModel.beginLogin();
                }
            }
        }, 300);
        AvatarRendererModel.getInstance().setGLThreadRunningScreen(true);
    }

    public void onSetInactive() {
        super.onSetInactive();
        AvatarRendererModel.getInstance().setGLThreadRunningScreen(false);
    }

    private void preloadAnimation() {
        ObjectAnimator.setFrameDelay(10);
        this.outerRingAnimator = AnimatorInflater.loadAnimator(XLEApplication.MainActivity, R.animator.outer_ring_rotation);
        this.middleRingAnimator = AnimatorInflater.loadAnimator(XLEApplication.MainActivity, R.animator.middle_ring_rotation);
        this.innerRingAnimator = AnimatorInflater.loadAnimator(XLEApplication.MainActivity, R.animator.inner_ring_rotation);
        this.showXenonAnimator = AnimatorInflater.loadAnimator(XLEApplication.MainActivity, R.animator.show_xenon);
        this.animatorsToCleanup.add(this.outerRingAnimator);
        this.animatorsToCleanup.add(this.middleRingAnimator);
        this.animatorsToCleanup.add(this.innerRingAnimator);
        this.animatorsToCleanup.add(this.showXenonAnimator);
        this.loginLogoView.setLayerType(2, null);
        this.loginButtonsView.setLayerType(2, null);
        this.outerRingView.setLayerType(2, null);
        this.middleRingView.setLayerType(2, null);
        this.innerRingView.setLayerType(2, null);
        this.greenBackgroundView.setLayerType(2, null);
        this.gradientBackgroundView.setLayerType(2, null);
        this.xenonView.setLayerType(2, null);
    }

    private void startSigningInAnimation() {
        this.loginButton.setVisibility(4);
        this.cancelButton.setVisibility(0);
        XLEAssert.assertNotNull(this.outerRingView);
        XLEAssert.assertNotNull(this.middleRingView);
        XLEAssert.assertNotNull(this.innerRingView);
        XLEAssert.assertNotNull(this.xenonView);
        this.outerRingAnimator.setTarget(this.outerRingView);
        this.middleRingAnimator.setTarget(this.middleRingView);
        this.innerRingAnimator.setTarget(this.innerRingView);
        this.showXenonAnimator.setTarget(this.xenonView);
        ObjectAnimator logoExpandOut = ObjectAnimator.ofFloat(this.loginLogoView, "y", new float[]{this.loginLogoView.getY(), this.loginLogoView.getY() - 500.0f}).setDuration(300);
        logoExpandOut.setInterpolator(new ExponentialInterpolator(4.0f, EasingMode.EaseIn));
        ObjectAnimator loginButtonExpandOut = ObjectAnimator.ofFloat(this.loginButtonsView, "y", new float[]{this.loginButtonsView.getY(), this.loginButtonsView.getY() + ((float) this.loginButtonsView.getHeight())}).setDuration(300);
        logoExpandOut.setInterpolator(new ExponentialInterpolator(4.0f, EasingMode.EaseIn));
        ObjectAnimator logoFade = ObjectAnimator.ofFloat(this.loginLogoView, "alpha", new float[]{1.0f, 0.0f}).setDuration(300);
        this.animatorsToCleanup.add(logoExpandOut);
        this.animatorsToCleanup.add(loginButtonExpandOut);
        this.animatorsToCleanup.add(logoFade);
        ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(this.greenBackgroundView, "alpha", new float[]{0.0f, 1.0f}).setDuration(1000);
        this.animatorsToCleanup.add(backgroundAnimator);
        this.signingInAnimationSet = new AnimatorSet();
        this.signingInAnimationSet.playTogether(new Animator[]{logoFade, logoExpandOut, loginButtonExpandOut, this.outerRingAnimator, this.middleRingAnimator, this.innerRingAnimator, this.showXenonAnimator, backgroundAnimator});
        XLEAssert.assertNotNull(logoFade.getTarget());
        XLEAssert.assertNotNull(logoExpandOut.getTarget());
        XLEAssert.assertNotNull(loginButtonExpandOut.getTarget());
        XLEAssert.assertNotNull(backgroundAnimator.getTarget());
        this.signingInAnimationSet.start();
        this.xenonView.setBackgroundResource(R.drawable.xenon_start);
        ((AnimationDrawable) this.xenonView.getBackground()).start();
    }

    private void cleanupAnimator() {
        if (this.signedInAnimatorSet != null) {
            this.signedInAnimatorSet.removeAllListeners();
        }
        if (this.signingInAnimationSet != null) {
            this.signingInAnimationSet.removeAllListeners();
        }
        Iterator i$ = this.animatorsToCleanup.iterator();
        while (i$.hasNext()) {
            Animator a = (Animator) i$.next();
            a.removeAllListeners();
            a.cancel();
            a.setTarget(null);
        }
        this.animatorsToCleanup.clear();
        this.loginLogoView.setLayerType(0, null);
        this.loginButtonsView.setLayerType(0, null);
        this.outerRingView.setLayerType(0, null);
        this.middleRingView.setLayerType(0, null);
        this.innerRingView.setLayerType(0, null);
        this.greenBackgroundView.setLayerType(0, null);
        this.gradientBackgroundView.setLayerType(0, null);
        this.xenonView.setLayerType(0, null);
    }

    private void startSignedInAnimation() {
        this.cancelButton.setVisibility(4);
        this.signingInAnimationSet.cancel();
        ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(this.gradientBackgroundView, "alpha", new float[]{1.0f, 0.0f}).setDuration(300);
        this.animatorsToCleanup.add(backgroundAnimator);
        backgroundAnimator.setStartDelay(100);
        ObjectAnimator outerRingStopAnimator = ObjectAnimator.ofFloat(this.outerRingView, "rotation", new float[]{getRotationEndPosition(this.outerRingView.getRotation())}).setDuration(1300);
        ObjectAnimator middleRingStopAnimator = ObjectAnimator.ofFloat(this.middleRingView, "rotation", new float[]{getRotationEndPosition(this.middleRingView.getRotation())}).setDuration(1300);
        ObjectAnimator innerRingStopAnimator = ObjectAnimator.ofFloat(this.innerRingView, "rotation", new float[]{getRotationEndPosition(this.innerRingView.getRotation())}).setDuration(1300);
        this.animatorsToCleanup.add(backgroundAnimator);
        this.animatorsToCleanup.add(outerRingStopAnimator);
        this.animatorsToCleanup.add(middleRingStopAnimator);
        this.animatorsToCleanup.add(innerRingStopAnimator);
        this.signedInAnimatorSet = new AnimatorSet();
        this.signedInAnimatorSet.playTogether(new Animator[]{outerRingStopAnimator, middleRingStopAnimator, innerRingStopAnimator, backgroundAnimator});
        this.signedInAnimatorSet.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                XboxAuthActivityAdapter.this.viewModel.onSignedInAnimationComplete();
            }

            public void onAnimationCancel(Animator animation) {
            }
        });
        XLEAssert.assertNotNull(outerRingStopAnimator.getTarget());
        XLEAssert.assertNotNull(middleRingStopAnimator.getTarget());
        XLEAssert.assertNotNull(innerRingStopAnimator.getTarget());
        XLEAssert.assertNotNull(backgroundAnimator.getTarget());
        this.signedInAnimatorSet.start();
        TransitionDrawable middleRingTransition = (TransitionDrawable) this.middleRingView.getBackground();
        TransitionDrawable innerRingTransition = (TransitionDrawable) this.innerRingView.getBackground();
        ((TransitionDrawable) this.outerRingView.getBackground()).startTransition(300);
        middleRingTransition.startTransition(300);
        innerRingTransition.startTransition(300);
        this.xenonView.setBackgroundResource(R.drawable.xenon_end);
        ((AnimationDrawable) this.xenonView.getBackground()).start();
    }

    private static float getRotationEndPosition(float currentRotation) {
        if (currentRotation < 180.0f) {
            return -360.0f;
        }
        return 0.0f;
    }
}
