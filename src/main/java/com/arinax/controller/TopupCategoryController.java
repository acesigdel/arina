package com.arinax.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arinax.entities.TopupCategory;
import com.arinax.exceptions.ApiException;
import com.arinax.playloads.ApiResponse;
import com.arinax.playloads.TopupCategoryDto;
import com.arinax.repositories.TopupCategoryRepo;

@RestController
@RequestMapping("/api/v1/topupcategory")
public class TopupCategoryController {

	@Autowired
    private TopupCategoryRepo topupCategoryRepo;


    // Create TopupCategory
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createTopupCategory(@RequestBody TopupCategoryDto dto) {
        TopupCategory category = new TopupCategory();
        category.setTitle(dto.getTitle());
        topupCategoryRepo.save(category);
        return ResponseEntity.ok(new ApiResponse("Topup Category created",true));
    }

    // Delete TopupCategory by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteTopupCategory(@PathVariable Integer id) {
        if (!topupCategoryRepo.existsById(id)) {
            throw new ApiException("Topup Category not found");
        }
        topupCategoryRepo.deleteById(id);
        return ResponseEntity.ok(new ApiResponse("Topup Category deleted",true));
    }
}
