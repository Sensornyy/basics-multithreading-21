package com.artemchep.basics_multithreading.cipher;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Cipher implements Runnable {
    private final Handler mainHandler;
    private final String textToEncrypt;
    private final long initialTime;
    private ICipher cipher;

    public Cipher(String textToEncrypt, long initialTime) {
        this.textToEncrypt = textToEncrypt;
        this.initialTime = initialTime;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setCipher(ICipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public void run() {
        final String cipheredText = CipherUtil.encrypt(textToEncrypt);
        final long resultTime = System.currentTimeMillis() - initialTime;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                cipher.updateUI(cipheredText, resultTime);
            }
        });

        Log.d("Finished Thread", Thread.currentThread().getName() + " is finished");
        Log.d("CipheredText", cipheredText);
    }
}
