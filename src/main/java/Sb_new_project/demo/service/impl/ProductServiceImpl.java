    package Sb_new_project.demo.service.impl;

    import Sb_new_project.demo.dto.LoggedInUserDTO;
    import Sb_new_project.demo.dto.ProductRequestDTO;
    import Sb_new_project.demo.dto.ProductResponseDTO;
    import Sb_new_project.demo.dto.ProductUpdateDTO;
    import Sb_new_project.demo.entity.Product;
    import Sb_new_project.demo.exception.ResourceNotFoundException;
    import Sb_new_project.demo.repository.ProductRepository;
    import Sb_new_project.demo.service.ProductService;
    import Sb_new_project.demo.util.Constant;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.access.AccessDeniedException;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.util.StringUtils;

    import java.util.ArrayList;
    import java.util.List;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class ProductServiceImpl implements ProductService {

        private final ProductRepository productRepository;
        private final LoggedInUserServiceImpl loggedInUserServiceImpl;

        @Override
        @Transactional
        public ProductResponseDTO addProduct(ProductRequestDTO requestDTO) {

            log.info("Adding product: {}", requestDTO.getName());

            if (productRepository.existsByName(requestDTO.getName())) {
                throw new IllegalArgumentException(Constant.PRODUCT_ALREADY_EXISTS + requestDTO.getName());
            }

            //Entity Mapping
            Product product = new Product();
            product.setName(requestDTO.getName());
            product.setDescription(requestDTO.getDescription());
            product.setPrice(requestDTO.getPrice());
            product.setQuantity(requestDTO.getQuantity());

            LoggedInUserDTO user = loggedInUserServiceImpl.getCurrentUser();
            product.setCreatedBy(user.getUsername());

            Product savedProduct = productRepository.save(product);
//           throw new RuntimeException("Transaction fail");

           return mapToResponse(savedProduct);
        }

        @Override
        public List<ProductResponseDTO> getAllProducts() {
            log.info("Fetching all products");

            List<Product> productList = productRepository.findAll();
            List<ProductResponseDTO> responseList = new ArrayList<>();

            for (Product product : productList) {
                responseList.add(mapToResponse(product));
            }

            return responseList;
        }

        @Override
        public ProductResponseDTO getProductByName(String productName) {
            log.info("Fetching product by name: {}", productName);

            Product product = productRepository.findByName(productName)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + productName));

            return mapToResponse(product);
        }

        @Override
        @Transactional
        public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO dto) {

            log.info("Updating product with id: {}", id);

            Product product = productRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + id));

            LoggedInUserDTO user = loggedInUserServiceImpl.getCurrentUser();

            if (!user.getRoles().contains(Constant.ROLE_ADMIN)) {
                throw new AccessDeniedException(Constant.ONLY_ADMIN_ALLOWED);
            }


            if (!StringUtils.isEmpty(dto.getName())) {

                if (!product.getName().equals(dto.getName()) &&
                        productRepository.existsByName(dto.getName())) {

                    throw new IllegalArgumentException(Constant.PRODUCT_ALREADY_EXISTS + dto.getName());
                }

                product.setName(dto.getName().trim());
            }

            if (!StringUtils.isEmpty(dto.getDescription())) {
                product.setDescription(dto.getDescription().trim());
            }

            if (dto.getPrice() != null) {
                product.setPrice(dto.getPrice());
            }

            if (dto.getQuantity() != null) {
                product.setQuantity(dto.getQuantity());
            }
            //admin only can update product
            product.setUpdatedBy(user.getUsername());


            Product updatedProduct = productRepository.save(product);

            return mapToResponse(updatedProduct);
        }

        @Override
        @Transactional
        public void deleteProductById(Long id) {

            log.info("Deleting product with id: {}", id);

            Product product = productRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + id));

            productRepository.delete(product);
        }

        private ProductResponseDTO mapToResponse(Product product) {

            ProductResponseDTO responseDTO = new ProductResponseDTO();

            responseDTO.setProductId(product.getProductId());
            responseDTO.setName(product.getName());
            responseDTO.setDescription(product.getDescription());
            responseDTO.setPrice(product.getPrice());
            responseDTO.setQuantity(product.getQuantity());
            responseDTO.setCreatedDate(product.getCreatedDate());
            responseDTO.setUpdatedBy(product.getUpdatedBy());

            return responseDTO;
        }
    }