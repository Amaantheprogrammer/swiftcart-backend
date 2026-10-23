package com.myProject.config;

import com.myProject.dto.response.ProductResponse;
import com.myProject.entity.Product;
import com.myProject.entity.User;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelMapperConfigTest {

    @Test
    void mapsProductSellerNameAndApprovalStatus() {
        User seller = new User();
        seller.setName("seller");

        Product product = new Product();
        product.setUser(seller);
        product.setApproved(true);

        ModelMapper mapper = new ModelMapperConfig().modelMapper();
        ProductResponse response = mapper.map(product, ProductResponse.class);

        assertEquals("seller", response.getUserName());
        assertTrue(response.isApproved());
    }
}
