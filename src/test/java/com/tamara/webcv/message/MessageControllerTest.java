package com.tamara.webcv.message;

import com.tamara.webcv.core.security.CaptchaFilter;
import com.tamara.webcv.core.security.JwtAuthenticationFilter;
import com.tamara.webcv.core.security.RateLimitFilter;
import com.tamara.webcv.core.security.SecurityFilterConfig;
import com.tamara.webcv.exception.FileStorageException;
import com.tamara.webcv.validation.SafeTextValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MessageController.class,
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
@ActiveProfiles("test")
@Import(SafeTextValidator.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService service;


    @Test
    void receiveMessage() throws Exception {
        String validJSON = """
                {
                    "email": "abcmail@mail.com",
                    "name": "Some Name",
                    "message": "Hello World"
                }""";

        mockMvc.perform(post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJSON))
                .andExpect(status().isCreated());

        verify(service).saveMessage(any(MessageRequestDTO.class));
    }

    @Test
    void receiveMaliciousMessage() throws Exception {
        String maliciousJSON = """
                {
                    "email": "abcmail@mail.com",
                    "name": "Some Name",
                    "message": "<script></script>"
                }""";

        mockMvc.perform(post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(maliciousJSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).saveMessage(any(MessageRequestDTO.class));
    }

    @Test
    void getMessages() throws Exception {
        MessageResponseDTO message1 = new MessageResponseDTO();
        message1.setName("Some Name 1");

        List<MessageResponseDTO> messages = List.of(message1);
        when(service.getAllMessages()).thenReturn(messages);

        mockMvc.perform(get("/api/admin/message"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Some Name 1")); // проверка массива $[0]

        verify(service).getAllMessages();
    }

    @Test
    void receiveMessageReturnsInternalError() throws Exception {
        doThrow(FileStorageException.class).when(service).saveMessage(any());

        String validJSON = "{\"email\":\"a@b.com\",\"name\":\"N\",\"message\":\"M\"}";

        mockMvc.perform(post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getMessageReturnsNoContent() throws Exception {
        when(service.getAllMessages()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/admin/message"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMessagesReturnsInternalError() throws Exception {
        when(service.getAllMessages()).thenThrow(FileStorageException.class);

        mockMvc.perform(get("/api/admin/message"))
                .andExpect(status().isInternalServerError());
    }
}
