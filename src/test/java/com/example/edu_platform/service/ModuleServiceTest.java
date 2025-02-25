package com.example.edu_platform.service;

import com.example.edu_platform.entity.Category;
import com.example.edu_platform.entity.Module;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ModuleDTO;
import com.example.edu_platform.payload.req.ModuleRequest;
import com.example.edu_platform.repository.CategoryRepository;
import com.example.edu_platform.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ModuleService moduleService;

    private Module module;
    private ModuleDTO moduleDTO;
    private Category category;
    private ModuleRequest moduleRequest;

    @BeforeEach
    void setUp(){
        moduleRequest = new ModuleRequest();
        moduleRequest.setName("Test Module");
        moduleRequest.setCategoryId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Test Description");
        category.setCoursePrice(100.0);
        category.setDuration(30);
        category.setActive(true);

        module = new Module();
        module.setId(1L);
        module.setName("Test Module");
        module.setCategory(category);
        module.setDeleted(false);

        moduleDTO = new ModuleDTO();
        moduleDTO.setId(1L);
        moduleDTO.setName("Test Module");
        moduleDTO.setCategory("Test Category");
        moduleDTO.setDeleted(false);
    }

    @Test
    void saveModule_success(){
        when(moduleRepository.existsByName(moduleRequest.getName())).thenReturn(false);
        when(categoryRepository.findById(moduleRequest.getCategoryId())).thenReturn(Optional.ofNullable(category));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        ApiResponse response = moduleService.createModule(moduleRequest);

        assertNotNull(response);
        assertNull(response.getError());
        assertEquals("Modul yaratildi", response.getData());
    }

    @Test
    void saveModule_alreadyExist(){
        when(moduleRepository.existsByName(moduleRequest.getName())).thenReturn(true);
        when(categoryRepository.findById(moduleRequest.getCategoryId())).thenReturn(Optional.ofNullable(category));

        ApiResponse response = moduleService.createModule(moduleRequest);

        assertNotNull(response.getError());
        assertEquals("Modul allaqachon mavjud.",response.getError().getMessage());
    }

    @Test
    void saveModule_categoryNotFound(){
        when(categoryRepository.findById(moduleRequest.getCategoryId())).thenReturn(Optional.empty());

        ApiResponse response = moduleService.createModule(moduleRequest);

        assertNotNull(response.getError());
        assertEquals("Kategoriya topilmadi.",response.getError().getMessage());
    }

    @Test

}
