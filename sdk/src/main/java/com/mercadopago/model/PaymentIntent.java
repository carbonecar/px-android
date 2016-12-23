package com.mercadopago.model;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class PaymentIntent {

    private Long transactionId;
    private Integer installments;
    @SerializedName("issuer_id")
    private Long issuerId;
    private String paymentMethodId;
    private String prefId;
    @SerializedName("token")
    private String tokenId;
    private String publicKey;
    private String email;
    private Payer payer;

    //TODO discounts
//    @SerializedName("coupon_amount")
//    private BigDecimal couponAmount;
//    @SerializedName("campaign_id")
//    private Long campaignId;
//    @SerializedName("coupon_code")
//    private String couponCode;

//    //TODO discounts
//    public void setCouponAmount(BigDecimal couponAmount) {
//        this.couponAmount = couponAmount;
//    }
//
//    //TODO discounts
//    public void setCampaignId(Long campaignId) {
//        this.campaignId = campaignId;
//    }
//
//    //TODO discounts
//    public void setCouponCode(String couponCode) {
//        this.couponCode = couponCode;
//    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Long getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Long issuerId) {
        this.issuerId = issuerId;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getPrefId() {
        return prefId;
    }

    public void setPrefId(String prefId) {
        this.prefId = prefId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }
}
