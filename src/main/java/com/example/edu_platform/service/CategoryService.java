package com.example.edu_platform.service;

import com.example.edu_platform.entity.Category;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.CategoryDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.repository.CategoryRepository;
import com.example.edu_platform.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileRepository fileRepository;

    public ApiResponse saveCategory(CategoryDTO categoryDTO) {
        boolean b = categoryRepository.existsByName(categoryDTO.getName());
        if (b) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Category"));
        }

        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .coursePrice(categoryDTO.getPrice())
                .duration(categoryDTO.getDuration())
                .file(fileRepository.findById(categoryDTO.getFileId()).orElse(null))
                .active(true)
                .build();
        categoryRepository.save(category);
        return new ApiResponse("Category successfully saved");
    }


    public ApiResponse getAllCategories(String name, String description, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Category> allCategory = categoryRepository.getAllCategory(name, description, pageRequest);
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Category category : allCategory.getContent()) {
            categoryDTOList.add(convertCategoryToCategoryDTO(category));
        }
        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(allCategory.getTotalElements())
                .totalPage(allCategory.getTotalPages())
                .body(categoryDTOList)
                .build();
        return new ApiResponse(resPageable);
    }



    public ApiResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        return new ApiResponse(convertCategoryToCategoryDTO(category));
    }



    public ApiResponse updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setDuration(categoryDTO.getDuration());
        category.setCoursePrice(categoryDTO.getPrice());
        category.setFile(fileRepository.findById(categoryDTO.getFileId()).orElse(null));
        categoryRepository.save(category);

        return new ApiResponse("Category successfully updated");
    }


    public ApiResponse deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        categoryRepository.delete(category);
        return new ApiResponse("Category successfully deleted");
    }


    public ApiResponse updateActiveCategory(Long categoryId, boolean active) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }
        category.setActive(active);
        categoryRepository.save(category);
        return new ApiResponse("Category successfully updated");
    }



    private CategoryDTO convertCategoryToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .price(category.getCoursePrice())
                .duration(category.getDuration())
                .active(category.getActive())
                .fileId(category.getFile() != null ? category.getFile().getId() : null)
                .build();
    }
}
