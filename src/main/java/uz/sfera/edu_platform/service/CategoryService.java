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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileRepository fileRepository;
    private final GroupRepository groupRepository;
    private final ModuleRepository moduleRepository;

    public ApiResponse saveCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByNameAndActiveIsTrue(categoryDTO.getName())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Category"));
        }

        Category category = new Category(
                categoryDTO.getName(),
                categoryDTO.getDescription(),
                categoryDTO.getPrice(),
                categoryDTO.getDuration(),
                (byte) 1,
                categoryDTO.getFileId() > 0 ?
                        fileRepository.findById(categoryDTO.getFileId()).orElse(null) : null
        );

        categoryRepository.save(category);
        return new ApiResponse("Category successfully saved");
    }



    public ApiResponse getAllCategories(String name, String description, int page, int size) {
        Page<Category> pages = categoryRepository.getAllCategory(name, description, PageRequest.of(page, size));

        if (!pages.hasContent()) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        return new ApiResponse(new ResPageable(
                page,
                size,
                pages.getTotalPages(),
                pages.getTotalElements(),
                pages.getContent().stream()
                        .map(this::convertCategoryToCategoryDTO)
                        .collect(Collectors.toList())
        ));
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

        if (categoryRepository.existsByNameAndIdNot(categoryDTO.getName(), categoryId)) {
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

        if (category == null) return new ApiResponse(ResponseError.NOTFOUND("Category"));

        List<Group> groups = groupRepository.findAllByCategoryId(category.getId());
        groups.forEach(group -> group.setCategory(null));
        groupRepository.saveAll(groups);

        List<Module> modules = moduleRepository.findAllByCategoryIdAndDeleted(category.getId(), (byte) 0);
        modules.forEach(module -> module.setCategory(null));
        moduleRepository.saveAll(modules);

        category.setActive((byte) 0);
        categoryRepository.save(category);

        return new ApiResponse("Category successfully deleted");
    }



    public ApiResponse updateActiveCategory(Long categoryId, boolean active) {
        Category category = categoryRepository.findById(categoryId).orElse(null);

        if (category == null) return new ApiResponse(ResponseError.NOTFOUND("Category"));

        category.setActive(active ? (byte) 1 : 0);
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
                .active(category.getActive() == 1)
                .fileId(Optional.ofNullable(category.getFile()).map(File::getId).orElse(null))
                .build();
    }
}
