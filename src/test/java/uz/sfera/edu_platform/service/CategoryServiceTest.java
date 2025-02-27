package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.CategoryDTO;
import uz.sfera.edu_platform.repository.CategoryRepository;
import uz.sfera.edu_platform.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Test Description");
        category.setCoursePrice(100.0);
        category.setDuration(30);
        category.setActive(true);

        categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setDescription("Test Description");
        categoryDTO.setPrice(100.0);
        categoryDTO.setDuration(30);
    }

    @Test
    void saveCategory_success() {
        when(categoryRepository.existsByName(categoryDTO.getName())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        ApiResponse response = categoryService.saveCategory(categoryDTO);

        assertNotNull(response);
        assertNull(response.getError());
        assertEquals("Category successfully saved", response.getData());
    }

    @Test
    void saveCategory_alreadyExist() {
        when(categoryRepository.existsByName(categoryDTO.getName())).thenReturn(true);

        ApiResponse response = categoryService.saveCategory(categoryDTO);

        assertNotNull(response.getError());
        assertEquals("Category allaqachon mavjud.", response.getError().getMessage());
    }

    @Test
    void getCategoryById_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        ApiResponse response = categoryService.getCategoryById(1L);

        assertNotNull(response);
        assertNull(response.getError());
        assertNotNull(response.getData());
    }

    @Test
    void getCategoryById_notFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ApiResponse response = categoryService.getCategoryById(1L);

        assertNotNull(response.getError());
        assertEquals("Category topilmadi.", response.getError().getMessage());
    }

    @Test
    void getAllCategories_success() {
        Page<Category> page = new PageImpl<>(Collections.singletonList(category));
        when(categoryRepository.getAllCategory(anyString(), anyString(), any(PageRequest.class))).thenReturn(page);

        ApiResponse response = categoryService.getAllCategories("", "", 0, 10);

        assertNotNull(response);
        assertNull(response.getError());
        assertNotNull(response.getData());
    }

    @Test
    void deleteCategory_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        ApiResponse response = categoryService.deleteCategory(1L);

        assertNotNull(response);
        assertNull(response.getError());
        assertEquals("Category successfully deleted", response.getData());
    }

    @Test
    void deleteCategory_notFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ApiResponse response = categoryService.deleteCategory(1L);

        assertNotNull(response.getError());
        assertEquals("Category topilmadi.", response.getError().getMessage());
    }
}
