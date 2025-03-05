package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.Module;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ModuleDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ModuleRequest;
import uz.sfera.edu_platform.payload.res.ResModule;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.CategoryRepository;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.ModuleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;

    public ApiResponse createModule(ModuleRequest moduleRequest) {
        if (!categoryRepository.existsById(moduleRequest.getCategoryId())) {
            return new ApiResponse(ResponseError.NOTFOUND("Kategoriya"));
        }

        if (moduleRepository.existsByName(moduleRequest.getName())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Modul"));
        }

        Category category = categoryRepository.findById(moduleRequest.getCategoryId()).orElseThrow();

        Module module = Module.builder()
                .name(moduleRequest.getName())
                .category(category)
                .deleted((byte) 0)
                .build();

        moduleRepository.save(module);

        return new ApiResponse("Modul yaratildi");
    }

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



    public ApiResponse searchModule(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Module> modules = (name == null || name.isBlank())
                ? moduleRepository.findAll(pageable)
                : moduleRepository.findByNameContainingIgnoreCaseAndDeleted(name, (byte) 0, pageable);

        return new ApiResponse(
                ResPageable.builder()
                        .page(page)
                        .size(size)
                        .totalPage(modules.getTotalPages())
                        .totalElements(modules.getTotalElements())
                        .body(moduleDTOList(modules))
                        .build()
        );
    }


    public ApiResponse getModule(Long moduleId) {
        return moduleRepository.findByIdAndDeleted(moduleId, (byte) 0)
                .filter(module -> module.getCategory() != null)  // Kategoriyasi o‘chirilmaganligini tekshiramiz
                .map(module -> new ApiResponse(moduleDTO(module)))
                .orElseGet(() -> new ApiResponse(ResponseError.DEFAULT_ERROR("Bu modul topilmadi yoki kategoriyasi o‘chirilgan")));
    }


    public ApiResponse update(Long moduleId, ModuleRequest moduleRequest) {
        Optional<Module> optionalModule = moduleRepository.findByIdAndDeleted(moduleId, (byte) 0);
        Optional<Category> optionalCategory = categoryRepository.findById(moduleRequest.getCategoryId());

        if (optionalModule.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }
        if (optionalCategory.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Kategoriya"));
        }

        Module module = optionalModule.get();
        Category category = optionalCategory.get();

        // Modul nomi o‘zgarayotgan bo‘lsa va u nom allaqachon mavjud bo‘lsa, xatolik qaytaramiz
        if (!module.getName().equals(moduleRequest.getName()) && moduleRepository.existsByName(moduleRequest.getName())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Modul"));
        }

        module.setName(moduleRequest.getName());
        module.setCategory(category);
        moduleRepository.save(module);

        return new ApiResponse("Modul yangilandi");
    }


    @Transactional
    public ApiResponse delete(Long moduleId) {
        int updatedRows = moduleRepository.softDeleteById(moduleId);
        return updatedRows > 0
                ? new ApiResponse("Modul o‘chirildi")
                : new ApiResponse(ResponseError.NOTFOUND("Modul"));
    }


    public ApiResponse getOpenModuleByStudent(User user) {
        Group group = groupRepository.findByStudentId(user.getId()).orElse(null);

        List<Module> all = moduleRepository.findAll();

        List<ResModule> resModules = all.stream()
                .map(module -> {
                    boolean isOpen = moduleRepository.checkOpenModulesByStudent(group != null ? group.getId() : null, module.getId());
                    return isOpen ? ResModule.builder()
                            .id(module.getId())
                            .name(module.getName())
                            .categoryId(module.getCategory() != null ? module.getCategory().getId() : null)
                            .isOpen(true)
                            .build() : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new ApiResponse(resModules);
    }


    private ModuleDTO moduleDTO(Module module) {
        return ModuleDTO.builder()
                .id(module.getId())
                .name(module.getName())
                .category(Optional.ofNullable(module.getCategory()).map(Category::getName).orElse("No Category"))
                .build();
    }

    private List<ModuleDTO> moduleDTOList(Page<Module> modules) {
        return modules.getContent().stream()
                .map(this::moduleDTO)
                .toList();
    }
}