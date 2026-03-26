//package Sb_new_project.demo.controller;
//
//import Sb_new_project.demo.dto.ProductRequestDTO;
//import Sb_new_project.demo.dto.ProductResponseDTO;
//import Sb_new_project.demo.service.ProductService;
//import Sb_new_project.demo.util.Constant;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductControllerTest {
//
//    @Mock
//    private ProductService productService;
//
//    @InjectMocks
//    private ProductController productController;
//
//    private static final String PRODUCT_NAME = "Laptop";
//    private static final String UPDATED_NAME = "Updated Laptop";
//    private static final String USERNAME = "kavita";
//
//    @Test
//    void testAddProduct() {
//
//        ProductRequestDTO dto = new ProductRequestDTO();
//        dto.setName("Test Product");
//
//        ProductResponseDTO responseDTO = new ProductResponseDTO();
//        responseDTO.setName("Test Product");
//
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(USERNAME);
//
//        when(productService.addProduct(dto)).thenReturn(responseDTO);
//
//        ResponseEntity<ProductResponseDTO> response =
//                productController.addProduct(dto, auth);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Test Product", response.getBody().getName());
//
//        verify(productService).addProduct(dto);
//    }
//
//    @Test
//    void testGetAllProducts() {
//
//        ProductResponseDTO product = new ProductResponseDTO();
//        product.setName(PRODUCT_NAME);
//
//        when(productService.getAllProducts()).thenReturn(List.of(product));
//
//        ResponseEntity<List<ProductResponseDTO>> response =
//                productController.getAllProducts();
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertFalse(response.getBody().isEmpty());
//
//        verify(productService).getAllProducts();
//    }
//
//    @Test
//    void testGetProductByName() {
//
//        ProductResponseDTO product = new ProductResponseDTO();
//        product.setName(PRODUCT_NAME);
//
//        when(productService.getProductByName(PRODUCT_NAME)).thenReturn(product);
//
//        ResponseEntity<ProductResponseDTO> response =
//                productController.getProductByName(PRODUCT_NAME);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(PRODUCT_NAME, response.getBody().getName());
//
//        verify(productService).getProductByName(PRODUCT_NAME);
//    }
//
//    @Test
//    void testGetProductByName_Exception() {
//
//        when(productService.getProductByName(PRODUCT_NAME))
//                .thenThrow(new RuntimeException(Constant.PRODUCT_NOT_FOUND));
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () ->
//                productController.getProductByName(PRODUCT_NAME));
//
//        assertEquals(Constant.PRODUCT_NOT_FOUND, ex.getMessage());
//
//        verify(productService).getProductByName(PRODUCT_NAME);
//    }
//
//    @Test
//    void testUpdateProduct() {
//
//        ProductRequestDTO request = new ProductRequestDTO();
//        request.setName(UPDATED_NAME);
//
//        ProductResponseDTO responseDTO = new ProductResponseDTO();
//        responseDTO.setName(UPDATED_NAME);
//
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(USERNAME);
//
//        when(productService.updateProductByName(eq(PRODUCT_NAME), any(), eq(USERNAME)))
//                .thenReturn(responseDTO);
//
//        ResponseEntity<ProductResponseDTO> response =
//                productController.updateProduct(PRODUCT_NAME, request, auth);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(UPDATED_NAME, response.getBody().getName());
//
//        verify(productService)
//                .updateProductByName(eq(PRODUCT_NAME), any(), eq(USERNAME));
//    }
//
//
//    @Test
//    void testDeleteProduct() {
//
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(USERNAME);
//
//        doNothing().when(productService).deleteProductByName(PRODUCT_NAME);
//
//        ResponseEntity<String> response =
//                productController.deleteProduct(PRODUCT_NAME, auth);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(Constant.PRODUCT_DELETED_SUCCESS + PRODUCT_NAME, response.getBody());
//
//        verify(productService).deleteProductByName(PRODUCT_NAME);
//    }
//}