package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.core.MercadoPago;

/**
 * Created by mromar on 2/1/17.
 */

public class InstallmentsReviewProviderImpl implements InstallmentsReviewProvider {

    private final MercadoPago mercadoPago;

    private Context context;

    public InstallmentsReviewProviderImpl(Context context, String publicKey) {
        this.context = context;
        if (publicKey == null) throw new IllegalStateException("public key not found");
        if (context == null) throw new IllegalStateException("context not found");

        mercadoPago = new MercadoPago.Builder()
                .setContext(context)
                .setKey(publicKey, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
    }
}
