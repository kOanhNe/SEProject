package ecommerce.shoestore.admin.product;

import ecommerce.shoestore.admin.product.dto.AdminShoesDetailDto;
import ecommerce.shoestore.admin.product.dto.AdminShoesListItemDto;
import ecommerce.shoestore.admin.product.dto.CreateShoesRequest;
import ecommerce.shoestore.admin.product.dto.UpdateShoesRequest;
import ecommerce.shoestore.category.Category;
import ecommerce.shoestore.category.CategoryRepository;
import ecommerce.shoestore.common.NotFoundException;
import ecommerce.shoestore.shoes.Shoes;
import ecommerce.shoestore.shoes.ShoesRepository;
import ecommerce.shoestore.shoesimage.ShoesImage;
import ecommerce.shoestore.shoesvariant.ShoesVariant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {

    private final ShoesRepository shoesRepository;
    private final CategoryRepository categoryRepository;

    /* =========================
       ADMIN – LIST PRODUCT
       ========================= */
    @Transactional(readOnly = true)
    public Page<AdminShoesListItemDto> getAdminProductList(
            int page,
            int size,
            String keyword,
            Long categoryId,
            String brand,
            String status
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("shoeId").descending());

        // ✅ SỬ DỤNG QUERY TÌM KIẾM CƠ BẢN (không cần deleted/status)
        Page<Shoes> shoesPage = shoesRepository.searchForAdminBasic(
                keyword,
                categoryId,
                brand,
                pageable
        );

        return shoesPage.map(shoes ->
                new AdminShoesListItemDto(
                        shoes.getShoeId(),
                        shoes.getName(),
                        shoes.getBrand(),
                        shoes.getCategory() != null
                                ? shoes.getCategory().getName()
                                : null,
                        shoes.getBasePrice(),
                        "ĐANG BÁN" // TODO: Thay bằng shoes.getStatus() khi DB có column
                )
        );
    }

    /* =========================
       ADMIN – GET DETAIL FOR EDIT
       ========================= */
    @Transactional(readOnly = true)
    public AdminShoesDetailDto getAdminShoesDetail(Long shoeId) {
        // TODO: Uncomment khi DB có column deleted
        /*
        Shoes shoes = shoesRepository.findByIdForAdmin(shoeId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm ID: " + shoeId));
        */
        
        // TẠM THỜI dùng findById
        Shoes shoes = shoesRepository.findById(shoeId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm ID: " + shoeId));

        List<AdminShoesDetailDto.ImageDto> imageDtos = shoes.getImages() != null
                ? shoes.getImages().stream()
                .map(img -> AdminShoesDetailDto.ImageDto.builder()
                        .imageId(img.getImageId())
                        .url(img.getUrl())
                        .isThumbnail(img.isThumbnail())
                        .build())
                .collect(Collectors.toList())
                : new ArrayList<>();

        List<AdminShoesDetailDto.VariantDto> variantDtos = shoes.getVariants() != null
                ? shoes.getVariants().stream()
                .map(v -> AdminShoesDetailDto.VariantDto.builder()
                        .variantId(v.getVariantId())
                        .color(v.getColor())
                        .size(v.getSize())
                        .stock(v.getStock())
                        .build())
                .collect(Collectors.toList())
                : new ArrayList<>();

        return AdminShoesDetailDto.builder()
                .shoeId(shoes.getShoeId())
                .name(shoes.getName())
                .brand(shoes.getBrand())
                .type(shoes.getType())
                .basePrice(shoes.getBasePrice())
                .description(shoes.getDescription())
                .collection(shoes.getCollection())
                .categoryId(shoes.getCategory() != null ? shoes.getCategory().getCategoryId() : null)
                .categoryName(shoes.getCategory() != null ? shoes.getCategory().getName() : null)
                .status("ĐANG BÁN") // TODO: shoes.getStatus() khi DB có column status
                .deleted(false) // TODO: shoes.isDeleted() khi DB có column deleted
                .images(imageDtos)
                .variants(variantDtos)
                .build();
    }

    /* =========================
       ADMIN – CREATE PRODUCT
       ========================= */
    @Transactional
    public Long createShoes(CreateShoesRequest request) {
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục ID: " + request.getCategoryId()));

        // Create Shoes entity
        Shoes shoes = Shoes.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .type(request.getType())
                .basePrice(request.getBasePrice())
                .description(request.getDescription())
                .collection(request.getCollection())
                .category(category)
                // TODO: Uncomment khi DB có columns deleted, status
                // .deleted(false)
                // .status("ĐANG BÁN")
                .build();

        // Save shoes first to get ID
        shoes = shoesRepository.save(shoes);

        // Create images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            Set<ShoesImage> images = new HashSet<>();
            for (CreateShoesRequest.ImageDto imgDto : request.getImages()) {
                ShoesImage image = ShoesImage.builder()
                        .url(imgDto.getUrl())
                        .isThumbnail(imgDto.isThumbnail())
                        .shoes(shoes)
                        .build();
                images.add(image);
            }
            shoes.setImages(images);
        }

        // Create variants
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            // Validate unique color-size combinations
            Set<String> variantKeys = new HashSet<>();
            for (CreateShoesRequest.VariantDto vDto : request.getVariants()) {
                String key = vDto.getColor() + "-" + vDto.getSize();
                if (!variantKeys.add(key)) {
                    throw new IllegalArgumentException("Biến thể " + key + " bị trùng lặp");
                }
            }

            Set<ShoesVariant> variants = new HashSet<>();
            for (CreateShoesRequest.VariantDto vDto : request.getVariants()) {
                ShoesVariant variant = ShoesVariant.builder()
                        .color(vDto.getColor())
                        .size(vDto.getSize())
                        .stock(vDto.getStock())
                        .shoes(shoes)
                        .build();
                variants.add(variant);
            }
            shoes.setVariants(variants);
        }

        shoes = shoesRepository.save(shoes);
        log.info("Admin created product: {} with ID: {}", shoes.getName(), shoes.getShoeId());
        return shoes.getShoeId();
    }

    /* =========================
       ADMIN – UPDATE PRODUCT
       ========================= */
    @Transactional
    public void updateShoes(Long shoeId, UpdateShoesRequest request) {
        // TODO: Uncomment khi DB có column deleted
        /*
        Shoes shoes = shoesRepository.findByIdForAdmin(shoeId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm ID: " + shoeId));
        */
        
        // TẠM THỜI dùng findById
        Shoes shoes = shoesRepository.findById(shoeId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm ID: " + shoeId));

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục ID: " + request.getCategoryId()));

        // Update basic info
        shoes.setName(request.getName());
        shoes.setBrand(request.getBrand());
        shoes.setType(request.getType());
        shoes.setBasePrice(request.getBasePrice());
        shoes.setDescription(request.getDescription());
        shoes.setCollection(request.getCollection());
        shoes.setCategory(category);

        // Update images
        if (request.getImages() != null) {
            // Clear existing images
            if (shoes.getImages() != null) {
                shoes.getImages().clear();
            }

            Set<ShoesImage> newImages = new HashSet<>();
            for (UpdateShoesRequest.ImageDto imgDto : request.getImages()) {
                ShoesImage image;
                if (imgDto.getImageId() != null) {
                    // Existing image - update it
                    image = shoes.getImages().stream()
                            .filter(img -> img.getImageId().equals(imgDto.getImageId()))
                            .findFirst()
                            .orElseGet(() -> ShoesImage.builder().shoes(shoes).build());
                } else {
                    // New image
                    image = ShoesImage.builder().shoes(shoes).build();
                }
                image.setUrl(imgDto.getUrl());
                image.setThumbnail(imgDto.isThumbnail());
                newImages.add(image);
            }
            shoes.setImages(newImages);
        }

        // Update variants
        if (request.getVariants() != null) {
            // Validate unique color-size combinations
            Set<String> variantKeys = new HashSet<>();
            for (UpdateShoesRequest.VariantDto vDto : request.getVariants()) {
                String key = vDto.getColor() + "-" + vDto.getSize();
                if (!variantKeys.add(key)) {
                    throw new IllegalArgumentException("Biến thể " + key + " bị trùng lặp");
                }
            }

            // Clear existing variants
            if (shoes.getVariants() != null) {
                shoes.getVariants().clear();
            }

            Set<ShoesVariant> newVariants = new HashSet<>();
            for (UpdateShoesRequest.VariantDto vDto : request.getVariants()) {
                ShoesVariant variant;
                if (vDto.getVariantId() != null) {
                    // Existing variant - update it
                    variant = shoes.getVariants().stream()
                            .filter(v -> v.getVariantId().equals(vDto.getVariantId()))
                            .findFirst()
                            .orElseGet(() -> ShoesVariant.builder().shoes(shoes).build());
                } else {
                    // New variant
                    variant = ShoesVariant.builder().shoes(shoes).build();
                }
                variant.setColor(vDto.getColor());
                variant.setSize(vDto.getSize());
                variant.setStock(vDto.getStock());
                newVariants.add(variant);
            }
            shoes.setVariants(newVariants);
        }

        shoesRepository.save(shoes);
        log.info("Admin updated product ID: {}", shoeId);
    }

    /* =========================
       ADMIN – DISABLE PRODUCT
       ========================= */
    @Transactional
    public void disableShoes(Long shoeId) {
        // TODO: Uncomment khi DB có column deleted và status
        /*
        Shoes shoes = shoesRepository.findByIdForAdmin(shoeId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm ID: " + shoeId));

        shoes.setStatus("NGỪNG BÁN");
        shoesRepository.save(shoes);
        log.info("Admin disabled product ID: {}", shoeId);
        */
        
        // TẠM THỜI chỉ log
        log.warn("Admin disable product ID: {} - CHƯA IMPLEMENT (chờ DB có column status)", shoeId);
        throw new UnsupportedOperationException("Chức năng ngừng bán chưa khả dụng. Vui lòng chờ DB được cập nhật.");
    }

    /* =========================
       ADMIN – DELETE PRODUCT (SOFT DELETE)
       ========================= */
    @Transactional
    public void deleteShoes(Long shoeId) {
        // TODO: Uncomment khi DB có column deleted và status
        /*
        Shoes shoes = shoesRepository.findByIdForAdmin(shoeId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm ID: " + shoeId));

        shoes.setDeleted(true);
        shoes.setStatus("ĐÃ XÓA");
        shoesRepository.save(shoes);
        log.info("Admin soft deleted product ID: {}", shoeId);
        */
        
        // TẠM THỜI chỉ log
        log.warn("Admin delete product ID: {} - CHƯA IMPLEMENT (chờ DB có columns deleted, status)", shoeId);
        throw new UnsupportedOperationException("Chức năng xóa sản phẩm chưa khả dụng. Vui lòng chờ DB được cập nhật.");
    }
}
