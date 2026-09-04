package com.tamara.webcv.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    private final String email = "abv@mail.com";
    private final String name = "Some Name";
    private final String messageText = "Some Message";

    @Mock
    private MessageRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MessageService service;

    @Test
    void saveMessage() {
        MessageRequestDTO dto = new MessageRequestDTO(email, name, messageText);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);

        when(restTemplate.postForObject(anyString(), any(), any(), any(Object[].class)))
                .thenReturn(new Object());

        when(repository.save(any(Message.class))).thenReturn(new Message());

        service.saveMessage(dto);

        verify(repository).save(captor.capture());
        Message message = captor.getValue();

        assertEquals(message.getEmail(), email);
        assertEquals(message.getName(), name);
        assertEquals(message.getMessageText(), messageText);
    }

    @Test
    void getAllMessages() {
        Message message1 = new Message();
        Message message2 = new Message();
        Message message3 = new Message();

        message1.setMessageText(messageText);
        message2.setName(name);
        message3.setEmail(email);

        List<Message> messages = List.of(message1, message2, message3);

        when(repository.findAll()).thenReturn(messages);

        List<MessageResponseDTO> dtos = service.getAllMessages();

        assertEquals(dtos.get(0).getMessage(), messageText);
        assertEquals(dtos.get(1).getName(), name);
        assertEquals(dtos.get(2).getEmail(), email);
    }
}
