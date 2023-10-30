package com.microsoft.xbox.toolkit;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import java.util.ArrayList;
import java.util.HashMap;

public class SoundManager {
    private static final int MAX_STREAM_SIZE = 14;
    private static final int NO_LOOP = 0;
    private AudioManager audioManager;
    private Context context;
    private boolean isEnabled;
    private ArrayList<Integer> recentlyPlayedResourceIds;
    private HashMap<Integer, Integer> resourceSoundIdMap;
    private SoundPool soundPool;

    private static class SoundManagerHolder {
        public static final SoundManager instance = new SoundManager();

        private SoundManagerHolder() {
        }
    }

    private SoundManager() {
        this.resourceSoundIdMap = new HashMap();
        this.recentlyPlayedResourceIds = new ArrayList();
        this.isEnabled = false;
        XLEAssert.assertTrue("You must access sound manager on UI thread.", Thread.currentThread() == ThreadManager.UIThread);
        this.context = XboxApplication.Instance.getApplicationContext();
        this.soundPool = new SoundPool(14, 3, 0);
        XLELog.Diagnostic("SoundManager", "SoundPool created.");
        this.audioManager = (AudioManager) this.context.getSystemService("audio");
    }

    public static SoundManager getInstance() {
        return SoundManagerHolder.instance;
    }

    public void setEnabled(boolean value) {
        if (this.isEnabled != value) {
            this.isEnabled = value;
        }
    }

    public void loadSound(int resId) {
        if (!this.resourceSoundIdMap.containsKey(Integer.valueOf(resId))) {
            this.resourceSoundIdMap.put(Integer.valueOf(resId), Integer.valueOf(this.soundPool.load(this.context, resId, 1)));
        }
    }

    public void playSound(int resId) {
        if (this.isEnabled) {
            int soundId;
            if (this.resourceSoundIdMap.containsKey(Integer.valueOf(resId))) {
                soundId = ((Integer) this.resourceSoundIdMap.get(Integer.valueOf(resId))).intValue();
            } else {
                soundId = this.soundPool.load(this.context, resId, 1);
                XLELog.Warning("SoundManager", "Loading sound right before playing. The sound might not be ready to playback right away.");
                this.resourceSoundIdMap.put(Integer.valueOf(resId), Integer.valueOf(soundId));
            }
            float volume = ((float) this.audioManager.getStreamVolume(3)) / ((float) this.audioManager.getStreamMaxVolume(3));
            XLELog.Diagnostic("SoundManager", String.format("Playing sound id %d with volume %f", new Object[]{Integer.valueOf(soundId), Float.valueOf(volume)}));
            this.soundPool.play(soundId, volume, volume, 1, 0, 1.0f);
        }
    }

    public void clearMostRecentlyPlayedResourceIds() {
    }

    public Integer[] getMostRecentlyPlayedResourceIds() {
        return new Integer[0];
    }
}
