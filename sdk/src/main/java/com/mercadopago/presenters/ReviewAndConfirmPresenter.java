package com.mercadopago.presenters;

import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Reviewer;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.providers.ReviewAndConfirmProvider;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.ReviewAndConfirmView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 2/2/17.
 */
public class ReviewAndConfirmPresenter extends MvpPresenter<ReviewAndConfirmView, ReviewAndConfirmProvider> implements Reviewer {
    private PaymentMethod mPaymentMethod;
    private PayerCost mPayerCost;
    private BigDecimal mAmount;
    private Discount mDiscount;
    private Site mSite;
    private List<Item> mItems;
    private CardInfo mCardInfo;
    private String mExtraPaymentMethodInfo;
    private DecorationPreference mDecorationPreference;

    public void initialize() {
        try {
            validate();
            showReviewAndConfirm();
        } catch (IllegalStateException exception) {
            getView().showError(exception.getMessage());
        }
    }

    private void validate() throws IllegalStateException {
        if (this.mPaymentMethod == null) {
            throw new IllegalStateException("payment method is null");
        }
        if (this.mItems == null) {
            throw new IllegalStateException("items not set");
        }
        if (this.mSite == null) {
            throw new IllegalStateException("site not set");
        }
        if (this.mAmount == null) {
            throw new IllegalStateException("amount not set");
        }
        if (MercadoPagoUtil.isCard(mPaymentMethod.getPaymentTypeId())) {
            if (this.mPayerCost == null) {
                throw new IllegalStateException("payer cost is null");
            }
            if (this.mCardInfo == null) {
                throw new IllegalStateException("card info is null");
            }
        }
    }

    private void showReviewAndConfirm() {
        Reviewable summary = getSummary();
        summary.setReviewer(this);

        Reviewable itemsReview = getItemsReview();
        summary.setReviewer(this);

        Reviewable reviewable = getPaymentMethodReview();
        summary.setReviewer(this);

        List<Reviewable> customReviewables = retrieveCustomReviewables();

        List<Reviewable> reviewables = new ArrayList<>();

        reviewables.add(summary);
        reviewables.add(itemsReview);
        reviewables.add(reviewable);
        reviewables.addAll(customReviewables);

        getView().showReviewables(reviewables);
    }

    private List<Reviewable> retrieveCustomReviewables() {
        List<Reviewable> customReviewables = CustomReviewablesHandler.getInstance().getReviewables();

        for (Reviewable reviewable : customReviewables) {
            reviewable.setReviewer(this);
        }

        return customReviewables;
    }

    private Reviewable getSummary() {
        Reviewable summary = getResourcesProvider().getSummaryReviewable(mPaymentMethod, mPayerCost, mAmount, mDiscount, mSite, mDecorationPreference, new OnConfirmPaymentCallback() {
            @Override
            public void confirmPayment() {
                getView().confirmPayment();
            }
        });
        return summary;
    }

    private Reviewable getItemsReview() {
        Reviewable itemsReview = getResourcesProvider().getItemsReviewable(mSite.getCurrencyId(), mItems);
        return itemsReview;
    }

    private Reviewable getPaymentMethodReview() {
        Reviewable paymentMethodReview;
        if (MercadoPagoUtil.isCard(mPaymentMethod.getPaymentTypeId())) {
            paymentMethodReview = getPaymentMethodOnReview();
        } else {
            paymentMethodReview = getPaymentMethodOffReview();
        }
        return paymentMethodReview;
    }

    private Reviewable getPaymentMethodOffReview() {
        BigDecimal finalAmount;
        if (mDiscount == null) {
            finalAmount = mAmount;
        } else {
            finalAmount = mAmount.subtract(mDiscount.getCouponAmount());
        }
        return getResourcesProvider().getPaymentMethodOffReviewable(mPaymentMethod, mExtraPaymentMethodInfo, finalAmount, mSite, mDecorationPreference, new OnReviewChange() {
            @Override
            public void onChangeSelected() {
                getView().changePaymentMethod();
            }
        });
    }

    private Reviewable getPaymentMethodOnReview() {
        return getResourcesProvider().getPaymentMethodOnReviewable(mPaymentMethod, mPayerCost, mCardInfo, mSite, mDecorationPreference, new OnReviewChange() {
            @Override
            public void onChangeSelected() {
                getView().changePaymentMethod();
            }
        });
    }

    public void setAmount(BigDecimal amount) {
        this.mAmount = amount;
    }

    public void setDiscount(Discount discount) {
        this.mDiscount = discount;
    }

    public void setPayerCost(PayerCost mPayerCost) {
        this.mPayerCost = mPayerCost;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.mCardInfo = cardInfo;
    }

    public void setPaymentMethod(PaymentMethod mPaymentMethod) {
        this.mPaymentMethod = mPaymentMethod;
    }

    public void setSite(Site site) {
        this.mSite = site;
    }

    public void setItems(List<Item> items) {
        this.mItems = items;
    }

    public void setExtraPaymentMethodInfo(String extraPaymentMethodInfo) {
        this.mExtraPaymentMethodInfo = extraPaymentMethodInfo;
    }

    public void setDecorationPreference(DecorationPreference decorationPreference) {
        this.mDecorationPreference = decorationPreference;
    }

    @Override
    public void changeRequired() {
        getView().cancelPayment();
    }
}
