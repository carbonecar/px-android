package com.mercadopago.providers;

import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.ResourcesProvider;
import com.mercadopago.preferences.DecorationPreference;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 2/2/17.
 */
public interface ReviewAndConfirmProvider extends ResourcesProvider {
    Reviewable getSummaryReviewable(PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, Site site, DecorationPreference decorationPreference, OnConfirmPaymentCallback onConfirmPaymentCallback);

    Reviewable getItemsReviewable(String currency, List<Item> items);

    Reviewable getPaymentMethodOnReviewable(PaymentMethod paymentMethod, PayerCost payerCost, CardInfo cardInfo, Site site, DecorationPreference decorationPreference, OnReviewChange reviewChange);

    Reviewable getPaymentMethodOffReviewable(PaymentMethod paymentMethod, String extraPaymentMethodInfo, BigDecimal amount, Site site, DecorationPreference decorationPreference, OnReviewChange reviewChange);
}
