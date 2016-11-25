package com.mercadopago;

/**
 * Created by vaserber on 11/25/16.
 */

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentType;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentTypesActivityTest {

    @Rule
    public ActivityTestRule<PaymentTypesActivity> mTestRule = new ActivityTestRule<>(PaymentTypesActivity.class, true, false);
    private Intent validStartIntent;
    private FakeAPI mFakeAPI;
    private boolean mIntentsActive;
    private String mMerchantPublicKey;


    @Before
    public void setValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_TEST_MX_PUBLIC_KEY;

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
    }

    @Before
    public void startFakeAPI() {
        mFakeAPI = new FakeAPI();
        mFakeAPI.start();
    }

    @Before
    public void initIntentsRecording() {
        Intents.init();
        mIntentsActive = true;
    }

    @After
    public void stopFakeAPI() {
        mFakeAPI.stop();
    }

    @After
    public void releaseIntents() {
        if (mIntentsActive) {
            mIntentsActive = false;
            Intents.release();
        }
    }

    private List<PaymentMethod> addPaymentMethodList() {
        String paymentMethods = StaticMock.getPaymentMethodListMaster();
        Type listType = new TypeToken<List<PaymentMethod>>() {
        }.getType();
        List<PaymentMethod> paymentMethodList = JsonUtil.getInstance().getGson().fromJson(paymentMethods, listType);
        validStartIntent.putExtra("paymentMethods", JsonUtil.getInstance().toJson(paymentMethodList));
        return paymentMethodList;
    }

    private List<PaymentType> addPaymentTypesList() {
        String paymentTypes = StaticMock.getPaymentTypesListMaster();
        Type listType2 = new TypeToken<List<PaymentType>>() {
        }.getType();
        List<PaymentType> paymentTypesList = JsonUtil.getInstance().getGson().fromJson(paymentTypes, listType2);
        validStartIntent.putExtra("paymentTypes", JsonUtil.getInstance().toJson(paymentTypesList));
        return paymentTypesList;
    }

    @Test
    public void getActivityParametersOnCreateIsValid() {
        List<PaymentMethod> paymentMethodList = addPaymentMethodList();
        List<PaymentType> paymentTypeList = addPaymentTypesList();

        PaymentTypesActivity activity = mTestRule.launchActivity(validStartIntent);
        assertEquals(mMerchantPublicKey, activity.mPresenter.getPublicKey());
        assertNotNull(activity.mPresenter.getPaymentMethodList());
        assertEquals(activity.mPresenter.getPaymentMethodList().size(), paymentMethodList.size());
        assertNotNull(activity.mPresenter.getPaymentTypeList());
        assertEquals(activity.mPresenter.getPaymentTypeList().size(), paymentTypeList.size());
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithoutPublicKeyIsInvalid() {
        Intent invalidStartIntent = new Intent();
        addPaymentMethodList();
        addPaymentTypesList();

        mTestRule.launchActivity(invalidStartIntent);
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithoutPaymentMethodsIsInvalid() {
        addPaymentTypesList();

        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void initializeWithoutPaymentTypesIsInvalid() {
        addPaymentMethodList();

        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void showPaymentTypesInRecyclerView() {
        addPaymentMethodList();
        List<PaymentType> paymentTypeList = addPaymentTypesList();

        mTestRule.launchActivity(validStartIntent);

        // Validate view
        RecyclerView list = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkActivityPaymentTypesRecyclerView);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() == paymentTypeList.size());
        } else {
            fail("No items found");
        }
    }
}
