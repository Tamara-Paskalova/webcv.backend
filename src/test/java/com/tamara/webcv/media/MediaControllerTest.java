package com.tamara.webcv.media;

import com.tamara.webcv.core.security.CaptchaFilter;
import com.tamara.webcv.core.security.JwtAuthenticationFilter;
import com.tamara.webcv.core.security.RateLimitFilter;
import com.tamara.webcv.core.security.SecurityFilterConfig;
import com.tamara.webcv.media.document.DocumentResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MediaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        SecurityFilterConfig.class,
                        RateLimitFilter.class,
                        CaptchaFilter.class,
                        JwtAuthenticationFilter.class
                }
        )
)
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaServiceFactory mediaServiceFactory;

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
