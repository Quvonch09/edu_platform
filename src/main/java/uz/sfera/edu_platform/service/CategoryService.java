package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.Module;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.CategoryDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.CategoryRepository;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.ModuleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileRepository fileRepository;
    private final GroupRepository groupRepository;
    private final ModuleRepository moduleRepository;

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
                .deleted(false)
                .build();
        categoryRepository.save(category);
        return new ApiResponse("Category successfully saved");
    }


    public ApiResponse getAllCategories(String name, String description, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Category> pages = categoryRepository.getAllCategory(name, description, pageRequest);
        if(pages.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(pages.getTotalElements())
                .totalPage(pages.getTotalPages())
                .body(pages.getContent().stream().map(this::convertCategoryToCategoryDTO).toList())
                .build();
        return new ApiResponse(resPageable);
    }



    public ApiResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> new ApiResponse(convertCategoryToCategoryDTO(category)))
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Category")));
    }


    public ApiResponse getAllList(){
        List<Category> categories = categoryRepository.findAll();
        return new ApiResponse(categories.stream().map(this::convertCategoryToCategoryDTO).toList());
    }


    public ApiResponse updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        boolean b = categoryRepository.existsByNameAndIdNot(category.getName(),category.getId());
        if (b) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Category"));
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

        for (Group group : groupRepository.findAllByCategoryId(category.getId())) {
            group.setCategory(null);
            groupRepository.save(group);
        }

        for (Module module : moduleRepository.findAllByCategoryIdAndDeletedFalse(category.getId())) {
            module.setCategory(null);
            moduleRepository.save(module);
        }

        category.setDeleted(true);
        categoryRepository.save(category);
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
                .active(category.isActive())
                .fileId(Optional.ofNullable(category.getFile()).map(File::getId).orElse(null))
                .build();
    }
}
