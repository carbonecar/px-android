package com.mercadopago.services;

import com.mercadopago.adapters.MPCall;
import com.mercadopago.model.Discount;
import com.mercadopago.model.DiscountIntent;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mromar on 11/29/16.
 */

public interface DiscountService {

    //TODO cambiar url a la de descuentos y los parámetros
    @GET("/discount_campaigns")
    MPCall<Discount> getDiscount(@Query("public_key") String publicKey, @Body DiscountIntent discountIntent);
}
