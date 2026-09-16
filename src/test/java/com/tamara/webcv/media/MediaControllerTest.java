package com.tamara.webcv.media;

import com.tamara.webcv.core.ClientIpResolver;
import com.tamara.webcv.core.security.RateLimitFilter;
import com.tamara.webcv.media.cv.DocumentResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Импортируйте ваш Капча-фильтр, из-за которого падало приложение
import com.tamara.webcv.core.security.CaptchaFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MediaController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                SecurityAutoConfiguration.class
        }
)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaServiceFactory mediaServiceFactory;

    @MockitoBean
    private ClientIpResolver clientIpResolver;

    @MockitoBean
    private CaptchaFilter captchaFilter;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(captchaFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            ((FilterChain) invocation.getArgument(2)).doFilter(
                    invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(rateLimitFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("1: Positive scenario. Successfully upload a valid media file.")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void uploadMediaReturnsCreatedStatusWhenFileIsValid() throws Exception {
        String mediaType = "document";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "resume.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "test content".getBytes()
        );

        MediaService mockService = mock(MediaService.class);
        MediaResponseDTO expectedResponse = new DocumentResponseDTO(1L, null, "http://url.com", 1);

        when(mediaServiceFactory.get(mediaType)).thenReturn(mockService);
        when(mockService.saveOrUpdate(any(), eq(null))).thenReturn(expectedResponse);

        mockMvc.perform(multipart("/api/admin/media/{type}", mediaType)
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        verify(mediaServiceFactory).get(mediaType);
        verify(mockService).saveOrUpdate(any(), eq(null));
    }
}
