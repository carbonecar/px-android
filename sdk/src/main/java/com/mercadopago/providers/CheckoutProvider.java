package com.mercadopago.providers;

import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

/**
 * Created by vaserber on 2/2/17.
 */

public interface CheckoutProvider extends ResourcesProvider {
    void getPaymentMethodSearch(OnResourcesRetrievedCallback<PaymentMethodSearch> resourcesRetrievedCallback);
    void getCustomer(OnResourcesRetrievedCallback<Customer> resourcesRetrievedCallback);

}
