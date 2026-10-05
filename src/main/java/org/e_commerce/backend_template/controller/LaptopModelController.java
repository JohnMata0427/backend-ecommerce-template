package org.e_commerce.backend_template.controller;

import java.util.List;
import java.util.UUID;

import org.e_commerce.backend_template.dto.LaptopModelRequestDto;
import org.e_commerce.backend_template.dto.LaptopModelResponseDto;
import org.e_commerce.backend_template.service.LaptopModelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/laptop-models")
@RequiredArgsConstructor
public class LaptopModelController {

    private final LaptopModelService laptopModelService;

    @PostMapping
    public ResponseEntity<LaptopModelResponseDto> createLaptopModel(
            @RequestBody @Valid final LaptopModelRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(laptopModelService.createLaptopModel(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaptopModelResponseDto> getLaptopModelById(@PathVariable final UUID id) {
        return ResponseEntity.ok(laptopModelService.getLaptopModelById(id));
    }

    @GetMapping
    public ResponseEntity<Page<LaptopModelResponseDto>> getAllLaptopModels(
            @PageableDefault(size = 20, sort = "brandName") final Pageable pageable) {
        return ResponseEntity.ok(laptopModelService.getAllLaptopModels(pageable));
    }

    @GetMapping("/brand/{brandName}")
    public ResponseEntity<Page<LaptopModelResponseDto>> getLaptopModelsByBrand(
            @PathVariable final String brandName,
            @PageableDefault(size = 20, sort = "modelNumber") final Pageable pageable) {
        return ResponseEntity.ok(laptopModelService.getLaptopModelsByBrand(brandName, pageable));
    }

    @GetMapping("/brand/{brandName}/list")
    public ResponseEntity<List<LaptopModelResponseDto>> getListByBrand(@PathVariable final String brandName) {
        return ResponseEntity.ok(laptopModelService.getListByBrand(brandName));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LaptopModelResponseDto>> searchByModelNumber(
            @RequestParam("model") final String modelNumber,
            @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(laptopModelService.searchByModelNumber(modelNumber, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaptopModelResponseDto> updateLaptopModel(
            @PathVariable final UUID id,
            @RequestBody @Valid final LaptopModelRequestDto requestDto) {
        return ResponseEntity.ok(laptopModelService.updateLaptopModel(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLaptopModel(@PathVariable final UUID id) {
        laptopModelService.deleteLaptopModel(id);
        return ResponseEntity.noContent().build();
    }
}
