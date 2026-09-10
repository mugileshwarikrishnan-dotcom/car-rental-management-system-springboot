package com.carrental.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carrental.model.Customer;
import com.carrental.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Save Customer
    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    // Get All Customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Customer Login
    public Customer login(String username, String password) {
        return customerRepository.findByUsernameAndPassword(username, password);
    }
}