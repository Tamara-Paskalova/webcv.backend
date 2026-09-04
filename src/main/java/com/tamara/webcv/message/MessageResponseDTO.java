package com.tamara.webcv.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
