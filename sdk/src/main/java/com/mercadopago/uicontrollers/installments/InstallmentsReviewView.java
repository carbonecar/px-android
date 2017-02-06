package com.mercadopago.uicontrollers.installments;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.CurrenciesUtil;

/**
 * Created by mromar on 2/3/17.
 */

public class InstallmentsReviewView implements InstallmentsView {

    //Local vars
    private Context mContext;
    private PayerCost mPayerCost;
    private String mCurrencyId;

    //Views
    private View mView;
    private MPTextView mInstallmentsAmount;
    private MPTextView mTotalAmount;
    private MPTextView mTeaPercent;
    private MPTextView mCftpercent;
    private FrameLayout mInstallmentsOptionButton;

    public InstallmentsReviewView(Context context, PayerCost payerCost, String currencyId) {
        mContext = context;
        mPayerCost = payerCost;
        mCurrencyId = currencyId;
    }

    @Override
    public void draw() {
        setInstallmentAmountText();
        setTotalAmountWithRateText();
        setTEAPercentText();
        setCFTPercentText();
    }

    private void setInstallmentAmountText() {
        StringBuilder sb = new StringBuilder();

        sb.append(mPayerCost.getInstallments());
        sb.append(" ");
        sb.append(mContext.getString(R.string.mpsdk_installments_by));
        sb.append(" ");

        sb.append(CurrenciesUtil.formatNumber(mPayerCost.getInstallmentAmount(), mCurrencyId));
        Spanned spannedInstallmentsText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getInstallmentAmount(),
                mCurrencyId, sb.toString(), false, true);

        mInstallmentsAmount.setText(spannedInstallmentsText);
    }

    private void setTotalAmountWithRateText() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(CurrenciesUtil.formatNumber(mPayerCost.getTotalAmount(), mCurrencyId));
        sb.append(")");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(mPayerCost.getTotalAmount(),
                mCurrencyId, sb.toString(), false, true);

        mTotalAmount.setText(spannedFullAmountText);
    }

    private void setTEAPercentText() {
        String teaPercenct = mContext.getString(R.string.mpsdk_installments_tea) + " " + mPayerCost.getTEAPercent();
        mTeaPercent.setText(teaPercenct);
    }

    private void setCFTPercentText() {
        String cftPercent = mContext.getString(R.string.mpsdk_installments_cft) + " " + mPayerCost.getCFTPercent();
        mCftpercent.setText(cftPercent);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mInstallmentsOptionButton.setOnClickListener(listener);
    }

    @Override
    public void initializeControls() {
        mInstallmentsOptionButton = (FrameLayout) mView.findViewById(R.id.mpsdkInstallmentsOptionButton);
        mInstallmentsAmount = (MPTextView) mView.findViewById(R.id.mpsdkInstallmentsAmount);
        mTotalAmount = (MPTextView) mView.findViewById(R.id.mpsdkTotalAmount);
        mTeaPercent = (MPTextView) mView.findViewById(R.id.mpsdkTeaPercent);
        mCftpercent = (MPTextView) mView.findViewById(R.id.mpsdkCftpercent);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.mpsdk_installments_review_view, parent, attachToRoot);

        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }
}


