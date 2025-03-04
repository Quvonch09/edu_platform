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
import uz.sfera.edu_platform.payload.res.ResModule;
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
import java.util.Optional;
import java.util.stream.Collectors;

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
                .deleted((byte) 0)
                .build();
        moduleRepository.save(module);
        return new ApiResponse("Modul yaratildi");
    }

    @Transactional
    public ApiResponse getByCategory(Long categoryId, User user, int page, int size) {
        Page<Module> modules = (categoryId != null)
                ? moduleRepository.findByCategoryIdAndDeleted(categoryId, (byte) 0, PageRequest.of(page, size))
                : moduleRepository.findByDeleted((byte) 0, PageRequest.of(page, size)); // Agar categoryId null bo‘lsa, barcha modullar

        return new ApiResponse(ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(modules.getTotalPages())
                .totalElements(modules.getTotalElements())
                .body(moduleDTOList(modules))
                .build());
    }




    @Transactional
    public ApiResponse searchModule(String name, int page, int size) {
        Page<Module> modules = (name == null || name.isBlank())
                ? moduleRepository.findAll(PageRequest.of(page, size))
                : moduleRepository.findByNameContainingIgnoreCaseAndDeleted(name, (byte) 0,PageRequest.of(page, size));

        return new ApiResponse(ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(modules.getTotalPages())
                .totalElements(modules.getTotalElements())
                .body(moduleDTOList(modules))
                .build());
    }



    public ApiResponse getModule(Long moduleId) {
        return moduleRepository.findByIdAndDeleted(moduleId, (byte) 0)
                .map(module -> module.getCategory() != null
                        ? new ApiResponse(moduleDTO(module))
                        : new ApiResponse(ResponseError.DEFAULT_ERROR("Bu modulning kategoriyasi o‘chirilgan")))
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Modul")));
    }

    public ApiResponse update(Long moduleId,ModuleRequest moduleRequest){
        Module module = moduleRepository.findByIdAndDeleted(moduleId, (byte) 0).orElse(null);
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

    public ApiResponse delete(Long moduleId) {
        return moduleRepository.findByIdAndDeleted(moduleId, (byte) 0)
                .map(module -> {
                    module.setDeleted((byte) 1);
                    moduleRepository.save(module);
                    return new ApiResponse("Modul o‘chirildi");
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Modul")));
    }


    public ApiResponse getOpenModuleByStudent(User user){

        Group group = groupRepository.findByStudentId(user.getId()).orElse(null);

        List<Module> all = moduleRepository.findAll();

        List<ResModule> resModules = all.stream()
                .map(module -> ResModule.builder()
                        .id(module.getId())
                        .name(module.getName())
                        .categoryId(module.getCategory() != null ? module.getCategory().getId() : null)
                        .isOpen(moduleRepository.checkOpenModulesByStudent(group != null ? group.getId() : null, module.getId()))
                        .build())
                .collect(Collectors.toList());

        return new ApiResponse(resModules);
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
