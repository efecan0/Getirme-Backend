package com.example.getirme.service.impl;

import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.dto.RestaurantDto;
import com.example.getirme.model.Customer;
import com.example.getirme.repository.CustomerRepository;
import com.example.getirme.service.ICustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements ICustomerService {


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public void register(CustomerDtoIU customerDtoIU) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDtoIU, customer);
        customer.setPhoneNumber(customerDtoIU.getPhoneNumber().replaceAll(" " , ""));
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
    }
}
