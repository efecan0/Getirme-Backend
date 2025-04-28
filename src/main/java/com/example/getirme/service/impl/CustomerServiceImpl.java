package com.example.getirme.service.impl;

import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.dto.LocationDto;
import com.example.getirme.dto.RestaurantDto;
import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.model.Customer;
import com.example.getirme.model.User;
import com.example.getirme.repository.CustomerRepository;
import com.example.getirme.service.ICustomerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.getirme.exception.MessageType.UNAUTHORIZED;

@Service
public class CustomerServiceImpl implements ICustomerService {


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;



    @Autowired
    private OpenStreetMapService openStreetMapService;

    @Override
    @Transactional
    public LocationDto getCustomerLocation() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (!user.getUserType().equals("CUSTOMER")) {
            throw new BaseException(new ErrorMessage(UNAUTHORIZED, "Only customers have location data"));
        }

        String locationAddress = user.getLocation();
        String[] coords = openStreetMapService.getCoordinates(locationAddress);

        Double latitude = Double.parseDouble(coords[0]);
        Double longitude = Double.parseDouble(coords[1]);

        return new LocationDto(latitude, longitude);
    }

    @Override
    public void updateCustomer(CustomerDtoIU customerDtoIU) {
        Customer context = (Customer) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDtoIU, customer);
        if(customerDtoIU.getPhoneNumber() != null) {
            customer.setPhoneNumber(customerDtoIU.getPhoneNumber().replaceAll(" " , ""));
        }
        else{
            customer.setPhoneNumber(context.getPhoneNumber());
        }
        if(customerDtoIU.getPassword() != null) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }
        else{
            customer.setPassword(context.getPassword());
        }

        customer.setId(context.getId());
        customerRepository.save(customer);
    }

    @Override
    public void register(CustomerDtoIU customerDtoIU) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDtoIU, customer);
        customer.setPhoneNumber(customerDtoIU.getPhoneNumber().replaceAll(" " , ""));
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
    }




}
