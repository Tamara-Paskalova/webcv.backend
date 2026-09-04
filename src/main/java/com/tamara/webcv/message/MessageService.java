package com.tamara.webcv.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Slf4j
@Service
public class MessageService {

    @Value("${telegram.bot.id}")
    private String tgBotId;

    @Value("${telegram.bot.token}")
    private String tgBotToken;

    @Autowired
    private MessageRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public Message saveMessage(MessageRequestDTO messageRequestDTO) {
        Message message = toEntity(messageRequestDTO);
        try {
            sendMessageToChat(messageRequestDTO);
        } catch (Exception e) {
            log.error("Failed to send message to telegram", e);
        }
        return repository.save(message);
    }

    public List<MessageResponseDTO> getAllMessages() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private Message toEntity(MessageRequestDTO dto) {
        Message message = new Message();
        message.setEmail(dto.getEmail());
        message.setName(dto.getName());
        message.setMessageText(dto.getMessage());
        return message;
    }

    private MessageResponseDTO toResponseDTO(Message message) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setName(message.getName());
        dto.setEmail(message.getEmail());
        dto.setMessage(message.getMessageText());
        dto.setRead(message.isRead());
        dto.setCreatedAt(message.getCreatedAt());

        return dto;
    }

    private void sendMessageToChat(MessageRequestDTO dto) {
        String message = buildMessage(dto);

        String url = "https://api.telegram.org/bot%s/sendMessage".formatted(tgBotToken);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("chat_id", tgBotId);
        params.add("text", message);
        params.add("parse_mode", "HTML");

        restTemplate.postForObject(
                url,
                params,
                String.class
        );
    }

    private String buildMessage(MessageRequestDTO dto) {
        String email = HtmlUtils.htmlEscape(dto.getEmail());
        String name = HtmlUtils.htmlEscape(dto.getName());
        String message = HtmlUtils.htmlEscape(dto.getMessage());

        return """
                🔔 <b>New message from WebCV Contact form!</b>
                
                👤 <b>Sender:</b> %s
                📧 <b>Email:</b> <code>%s</code>
                
                📝 <b>Message:</b>
                 %s
                """.formatted(name, email, message);
    }
}
