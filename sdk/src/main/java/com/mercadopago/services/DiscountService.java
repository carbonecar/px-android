package com.mercadopago.services;

import com.mercadopago.adapters.MPCall;
import com.mercadopago.model.Discount;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mromar on 11/29/16.
 */

public interface DiscountService {

    //TODO cambiar url a la de descuentos y los parámetros
    //TODO volar el objeto discountIntent, no sirve
    //TODO cambiar access_token por public_key, está así para probar el servicio nada más
    @GET("/discount_campaigns")
    MPCall<Discount> getDirectDiscount(@Query("access_token") String publicKey, @Query("transaction_amount") String transactionAmount, @Query("email") String payerEmail);

    //TODO cambiar url a la de descuentos y los parámetros
    //TODO volar el objeto discountIntent, no sirve
    //TODO cambiar access_token por public_key, está así para probar el servicio nada más
    @GET("/discount_campaigns")
    MPCall<Discount> getCodeDiscount(@Query("access_token") String publicKey, @Query("transaction_amount") String transactionAmount, @Query("email") String payerEmail, @Query("coupon_code") String couponCode);
}
