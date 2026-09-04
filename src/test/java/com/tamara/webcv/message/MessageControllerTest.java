package com.tamara.webcv.message;

import com.tamara.webcv.core.ClientIpResolver;
import com.tamara.webcv.core.security.RateLimitFilter;
import com.tamara.webcv.core.service.TurnstileService;
import com.tamara.webcv.exception.FileStorageException;
import com.tamara.webcv.validation.SafeTextValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;

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
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                SecurityAutoConfiguration.class
        }
)
@Import(SafeTextValidator.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService service;

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private ClientIpResolver clientIpResolver;

    @MockitoBean
    private TurnstileService turnstileService;

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
        }).when(rateLimitFilter).doFilter(any(), any(), any());
    }

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
