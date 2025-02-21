package com.example.edu_platform.service;

import com.example.edu_platform.entity.Category;
import com.example.edu_platform.entity.Module;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ModuleDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ModuleRequest;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.repository.CategoryRepository;
import com.example.edu_platform.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;

    public ApiResponse createModule(ModuleRequest moduleRequest){
        Category category = categoryRepository.findById(moduleRequest.getCategoryId()).orElse(null);
        if (category == null){
            return new ApiResponse(ResponseError.NOTFOUND("Kategoriya"));
        } else if (moduleRepository.existsByName(moduleRequest.getName())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Modul"));
        }
        Module module = Module.builder()
                .name(moduleRequest.getName())
                .category(category)
                .deleted(false)
                .build();
        moduleRepository.save(module);
        return new ApiResponse("Modul yaratildi");
    }

    @Transactional
    public ApiResponse getByCategory(Long categoryId, int page, int size) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Kategoriya"));
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Module> modules = moduleRepository.findByCategoryIdAndDeletedFalse(categoryId, pageRequest);

        List<ModuleDTO> groupDTOList = modules.stream().map(module -> ModuleDTO.builder()
                .id(module.getId())
                .name(module.getName())
                .category(module.getCategory().getName())
                .deleted(module.isDeleted())
                .build()).toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(modules.getTotalPages())
                .totalElements(modules.getTotalElements())
                .body(groupDTOList)
                .build();

        return new ApiResponse(resPageable);
    }

    @Transactional
    public ApiResponse searchModule(String name, int size, int page) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Module> modules;

        if (name == null || name.trim().isEmpty()) {
            modules = moduleRepository.findAll(pageRequest);
        } else {
            modules = moduleRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name, pageRequest);
        }

        List<ModuleDTO> moduleDTOList = modules.stream()
                .map(module -> ModuleDTO.builder()
                        .id(module.getId())
                        .name(module.getName())
                        .category(module.getCategory().getName())
                        .deleted(module.isDeleted())
                        .build())
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(modules.getTotalPages())
                .totalElements(modules.getTotalElements())
                .body(moduleDTOList)
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse getModule(Long moduleId){
        Module foundModule = moduleRepository.findByIdAndDeletedFalse(moduleId).orElse(null);
        if (foundModule == null){
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }
        return new ApiResponse(moduleDTO(foundModule));
    }

    public ApiResponse update(Long moduleId,ModuleRequest moduleRequest){
        Module module = moduleRepository.findByIdAndDeletedFalse(moduleId).orElse(null);
        Category category = categoryRepository.findById(moduleRequest.getCategoryId()).orElse(null);
        if (module == null){
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        } else if (moduleRepository.existsByName(moduleRequest.getName())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Modul"));
        } else if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Kategoriya"));
        }
        module.setName(moduleRequest.getName());
        module.setCategory(category);
        moduleRepository.save(module);
        return new ApiResponse("Modul yangilandi");
    }

    public ApiResponse delete(Long moduleId){
        Module module = moduleRepository.findByIdAndDeletedFalse(moduleId).orElse(null);
        if (module == null){
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }
        module.setDeleted(true);
        moduleRepository.save(module);
        return new ApiResponse("Modul o'chirildi");
    }

    private ModuleDTO moduleDTO(Module module){
        return ModuleDTO.builder()
                .id(module.getId())
                .name(module.getName())
                .category(module.getCategory().getName())
                .deleted(module.isDeleted())
                .build();
    }
}
