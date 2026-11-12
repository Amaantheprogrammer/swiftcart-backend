package com.swiftcart.config;

import com.swiftcart.dto.response.ProductResponse;
import com.swiftcart.entity.Product;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.*;

@Configuration 
public class ModelMapperConfig {
    @Bean 
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        // Map Product.user.name -> ProductResponse.userName
        mapper.addMappings(new PropertyMap<Product, ProductResponse>() {
            @Override
            protected void configure() {
                map(source.getUser().getName(), destination.getUserName());
                map(source.isApproved(), destination.isApproved());
            }
        });
        return mapper;
    }
}
