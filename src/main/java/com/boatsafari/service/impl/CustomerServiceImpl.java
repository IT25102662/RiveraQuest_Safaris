package com.boatsafari.service.impl;
 
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.Customer;
import com.boatsafari.repository.CustomerRepository;
import com.boatsafari.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
@Service
public class CustomerServiceImpl implements CustomerService {
 
@Autowired
private CustomerRepository customerRepository;
 
@Override
public Customer getCustomerById(Long id) {
return customerRepository.findById(id)
.orElseThrow(() ->
new ResourceNotFoundException("Customer not found with ID: " + id));
}
 
@Override
public Customer updateProfile(Long id, Customer updatedData) {
Customer existing = getCustomerById(id);
 
if (updatedData.getFirstName() == null ||
updatedData.getFirstName().trim().isEmpty()) {
throw new BusinessRuleException("First name cannot be empty.");
}
 
existing.setFirstName(updatedData.getFirstName().trim());
existing.setLastName(updatedData.getLastName() != null
? updatedData.getLastName().trim()
: "");
existing.setPhoneNumber(updatedData.getPhoneNumber());
existing.setStreet(updatedData.getStreet());
existing.setCity(updatedData.getCity());
existing.setCountry(updatedData.getCountry());
 
return customerRepository.save(existing);
}
}
