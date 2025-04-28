package com.example.getirme.controller.impl;

import com.example.getirme.controller.ICustomerController;
import com.example.getirme.dto.CustomerDto;
import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.dto.LocationDto;
import com.example.getirme.model.Customer;
import com.example.getirme.model.RootEntity;
import com.example.getirme.service.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerControllerImpl extends BaseController implements ICustomerController {

    @Autowired
    ICustomerService customerService;


    @PostMapping("/register")
    @Override
    public ResponseEntity<RootEntity<String>> register(@Valid @RequestBody CustomerDtoIU customerDto) {
        customerService.register(customerDto);
        return ok("Registered successfully.");
    }

    @GetMapping("/location")
    public ResponseEntity<RootEntity<LocationDto>> getCustomerLocation() {
        return ok(customerService.getCustomerLocation());
    }

    @PostMapping("/update")
    @Override
    public ResponseEntity<RootEntity<String>> updateCustomer(@RequestBody CustomerDtoIU customerDtoIU){
            customerService.updateCustomer(customerDtoIU);
            return ok("Updated successfully.");
    }


    @GetMapping("/getUserInfo")
    @Override
    public ResponseEntity<RootEntity<CustomerDto>> getUserInfo(){
        Customer context = (Customer) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return ok(new CustomerDto(context.getName() , context.getSurname() , context.getLocation()));
    }

}
