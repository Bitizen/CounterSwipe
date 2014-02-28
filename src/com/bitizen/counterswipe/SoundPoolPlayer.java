package com.bitizen.counterswipe;

import java.util.HashMap;

import com.bitizen.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPoolPlayer {
    private SoundPool mShortPlayer= null;
    private HashMap mSounds = new HashMap();

    public SoundPoolPlayer(Context pContext) {
        // setup Soundpool
        this.mShortPlayer = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

        mSounds.put(R.raw.gameover, this.mShortPlayer.load(pContext, R.raw.gameover, 1));
        mSounds.put(R.raw.gethit, this.mShortPlayer.load(pContext, R.raw.gethit, 1));
        mSounds.put(R.raw.hit, this.mShortPlayer.load(pContext, R.raw.hit, 1));
        mSounds.put(R.raw.miss, this.mShortPlayer.load(pContext, R.raw.miss, 1));
        mSounds.put(R.raw.noammo, this.mShortPlayer.load(pContext, R.raw.noammo, 1));
        mSounds.put(R.raw.reload, this.mShortPlayer.load(pContext, R.raw.reload, 1));
        mSounds.put(R.raw.stillarmed, this.mShortPlayer.load(pContext, R.raw.stillarmed, 1));
    }

    public void playShortResource(int piResource) {
        int iSoundId = (Integer) mSounds.get(piResource);
        this.mShortPlayer.play(iSoundId, 0.99f, 0.99f, 0, 0, 1);
    }

    public void release() {
        this.mShortPlayer.release();
        this.mShortPlayer = null;
    }
}