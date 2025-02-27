package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.Module;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ModuleDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ModuleRequest;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.CategoryRepository;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;

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
    public ApiResponse getByCategory(Long categoryId, User user, int page, int size) {
        Category category;
        if (user.getRole().equals(Role.ROLE_STUDENT)){
            Group group = groupRepository.findGroup(user.getId());
            category = categoryRepository.findById(group != null ? group.getCategory().getId() : null).orElse(null);
        } else {
            if (categoryId == null){
                return new ApiResponse(ResponseError.DEFAULT_ERROR("CategoryId kiritish kerak"));
            }
            category = categoryRepository.findById(categoryId).orElse(null);
        }
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Kategoriya"));
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Module> modules = moduleRepository.findByCategoryIdAndDeletedFalse(categoryId, pageRequest);

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(modules.getTotalPages())
                .totalElements(modules.getTotalElements())
                .body(moduleDTOList(modules))
                .build();

        return new ApiResponse(resPageable);
    }

    @Transactional
    public ApiResponse searchModule(String name, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Module> modules;

        if (name == null || name.trim().isEmpty()) {
            modules = moduleRepository.findAll(pageRequest);
        } else {
            modules = moduleRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name, pageRequest);
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(modules.getTotalPages())
                .totalElements(modules.getTotalElements())
                .body(moduleDTOList(modules))
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse getModule(Long moduleId){
        Module foundModule = moduleRepository.findByIdAndDeletedFalse(moduleId).orElse(null);
        if (foundModule == null){
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }

        if (foundModule.getCategory() == null){
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Bu modulning categoriyasi uchirilgan"));
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
                .build();
    }

    private List<ModuleDTO> moduleDTOList(Page<Module> modules){
        return modules.stream()
                .filter(module -> module.getCategory() != null)
                .map(module -> ModuleDTO.builder()
                        .id(module.getId())
                        .name(module.getName())
                        .category(module.getCategory().getName())
                        .build())
                .toList();
    }
}
