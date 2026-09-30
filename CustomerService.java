package com.boatsafari.service;

import com.boatsafari.model.Customer;

public interface CustomerService {
Customer getCustomerById(Long id);
Customer updateProfile (Long id, Customer updatedData);
}
