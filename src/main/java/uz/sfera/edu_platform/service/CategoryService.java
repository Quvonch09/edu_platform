package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.CategoryDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.CategoryRepository;
import uz.sfera.edu_platform.repository.FileRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileRepository fileRepository;

    public ApiResponse getAllCategories(String name, String description, int page, int size) {
        Page<Category> pages = categoryRepository.getAllCategory(name, description, PageRequest.of(page, size));

        if (pages.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        List<CategoryDTO> categoryDTOs = pages.map(this::convertCategoryToCategoryDTO).getContent();

        return new ApiResponse(new ResPageable(page, size, pages.getTotalPages(), pages.getTotalElements(), categoryDTOs));
    }


    public ApiResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> new ApiResponse(convertCategoryToCategoryDTO(category)))
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Category")));
    }


    public ApiResponse getAllList() {
        return new ApiResponse(
                categoryRepository.findAllByActive((byte) 1)
                        .stream()
                        .map(this::convertCategoryToCategoryDTO)
                        .toList()
        );
    }


    public ApiResponse saveCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByNameAndActive(categoryDTO.getName(), (byte) 1)) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Category"));
        }

        Category category = new Category();
        save(category, categoryDTO);
        categoryRepository.save(category);

        return new ApiResponse("Category successfully saved");
    }


    public ApiResponse updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    if (categoryRepository.existsByNameAndActive(categoryDTO.getName(), (byte) 1)) {
                        return new ApiResponse(ResponseError.ALREADY_EXIST("Category"));
                    }
                    save(category, categoryDTO);
                    categoryRepository.save(category);
                    return new ApiResponse("Category successfully updated");
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Category")));
    }


    public ApiResponse deleteCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    category.setActive((byte) 0);
                    categoryRepository.save(category);
                    return new ApiResponse("Category successfully deleted");
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Category")));
    }


    public ApiResponse updateActiveCategory(Long categoryId, boolean active) {
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    category.setActive(active ? (byte) 1 : 0);
                    categoryRepository.save(category);
                    return new ApiResponse("Category successfully updated");
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Category")));
    }


    private CategoryDTO convertCategoryToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .price(category.getCoursePrice())
                .duration(category.getDuration())
                .active(category.getActive() == 1)
                .fileId(Optional.ofNullable(category.getFile()).map(File::getId).orElse(null))
                .build();
    }


    public void save(Category category, CategoryDTO categoryDTO)
    {
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setDuration((byte) categoryDTO.getDuration());
        category.setCoursePrice(categoryDTO.getPrice());
        category.setActive((byte) 1);
        category.setFile(categoryDTO.getFileId() != null ? fileRepository.findById(categoryDTO.getFileId()).orElse(null) : null);
    }
}
