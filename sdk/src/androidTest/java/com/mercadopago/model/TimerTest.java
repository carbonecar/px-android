package com.mercadopago.model;

import android.os.Looper;

import com.mercadopago.controllers.CheckoutTimer;

import junit.framework.TestCase;

/**
 * Created by mromar on 11/23/16.
 */

public class TimerTest extends TestCase {

    public void testStart() {

        Looper.prepare();

        CheckoutTimer.getInstance().start(2);
        CheckoutTimer.getInstance().setOnFinishListener(new CheckoutTimer.FinishListener() {
            @Override
            public void onFinish() {
                assertTrue(CheckoutTimer.getInstance().getCurrentTime().equals("00:00"));
                Thread.currentThread().interrupt();
            }
        });
    }

    public void testStop() {

        Looper.prepare();

        CheckoutTimer.getInstance().start(2);
        CheckoutTimer.getInstance().stop();

        assertFalse(CheckoutTimer.getInstance().isTimerEnabled());

        Thread.currentThread().interrupt();
    }

    public void testReset() {

        Looper.prepare();

        CheckoutTimer.getInstance().start(2);
        CheckoutTimer.getInstance().setOnFinishListener(new CheckoutTimer.FinishListener() {
            @Override
            public void onFinish() {
                CheckoutTimer.getInstance().start(2);
                assertFalse(CheckoutTimer.getInstance().isTimerEnabled());
            }
        });
    }
}
