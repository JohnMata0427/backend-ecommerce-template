package org.e_commerce.backend_template.service;

import java.util.List;
import java.util.UUID;

import org.e_commerce.backend_template.dto.LaptopModelRequestDto;
import org.e_commerce.backend_template.dto.LaptopModelResponseDto;
import org.e_commerce.backend_template.entity.LaptopModel;
import org.e_commerce.backend_template.exception.AppException;
import org.e_commerce.backend_template.mapper.LaptopModelMapper;
import org.e_commerce.backend_template.repository.LaptopModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class LaptopModelService {

    private static final String MODEL_NOT_FOUND = "Modelo de laptop no encontrado con id: %s";
    private static final String DUPLICATE_MODEL = "Ya existe un registro para %s %s %s";

    private final LaptopModelRepository laptopModelRepository;
    private final LaptopModelMapper laptopModelMapper;

    @Transactional
    public LaptopModelResponseDto createLaptopModel(final LaptopModelRequestDto requestDto) {
        if (laptopModelRepository.existsByBrandNameIgnoreCaseAndSeriesIgnoreCaseAndModelNumberIgnoreCase(
                requestDto.brandName(), requestDto.series(), requestDto.modelNumber())) {
            throw new AppException(
                DUPLICATE_MODEL.formatted(requestDto.brandName(), requestDto.series(), requestDto.modelNumber()),
                HttpStatus.CONFLICT
            );
        }

        final LaptopModel model = laptopModelMapper.toEntity(requestDto);
        final LaptopModel savedModel = laptopModelRepository.save(model);
        log.info("Modelo de laptop registrado: id={}, marca={}, modelo={}", savedModel.getId(), savedModel.getBrandName(), savedModel.getModelNumber());
        return laptopModelMapper.toResponseDto(savedModel);
    }

    public LaptopModelResponseDto getLaptopModelById(final UUID id) {
        final LaptopModel model = laptopModelRepository.findById(id)
                .orElseThrow(() -> new AppException(MODEL_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
        return laptopModelMapper.toResponseDto(model);
    }

    public Page<LaptopModelResponseDto> getAllLaptopModels(final Pageable pageable) {
        return laptopModelRepository.findAll(pageable)
                .map(laptopModelMapper::toResponseDto);
    }

    public Page<LaptopModelResponseDto> getLaptopModelsByBrand(final String brandName, final Pageable pageable) {
        return laptopModelRepository.findByBrandNameIgnoreCase(brandName, pageable)
                .map(laptopModelMapper::toResponseDto);
    }

    public List<LaptopModelResponseDto> getListByBrand(final String brandName) {
        return laptopModelRepository.findByBrandNameIgnoreCaseOrderByModelNumberAsc(brandName)
                .stream()
                .map(laptopModelMapper::toResponseDto)
                .toList();
    }

    public Page<LaptopModelResponseDto> searchByModelNumber(final String modelNumber, final Pageable pageable) {
        return laptopModelRepository.findByModelNumberContainingIgnoreCase(modelNumber, pageable)
                .map(laptopModelMapper::toResponseDto);
    }

    @Transactional
    public LaptopModelResponseDto updateLaptopModel(final UUID id, final LaptopModelRequestDto requestDto) {
        final LaptopModel existing = laptopModelRepository.findById(id)
                .orElseThrow(() -> new AppException(MODEL_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

        laptopModelMapper.updateEntityFromDto(requestDto, existing);
        final LaptopModel saved = laptopModelRepository.save(existing);
        log.info("Modelo de laptop actualizado: id={}", saved.getId());
        return laptopModelMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteLaptopModel(final UUID id) {
        final LaptopModel existing = laptopModelRepository.findById(id)
                .orElseThrow(() -> new AppException(MODEL_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
        laptopModelRepository.delete(existing);
        log.info("Modelo de laptop eliminado: id={}", id);
    }
}
