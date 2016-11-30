package com.mercadopago.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by mromar on 11/30/16.
 */

public class DiscountIntent {

    @SerializedName("public_key")
    private String publicKey;

    @SerializedName("email")
    private String email;

    //TODO validar el tipo de dato
    @SerializedName("transaction_amount")
    private BigDecimal transactionAmount;

    public void setPublicKey(String publicKey){
        this.publicKey = publicKey;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
