package com.tamara.webcv.message;

import com.tamara.webcv.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Message extends BaseEntity {

    @Column(name = "sender_name", nullable = false)
    private String name;

    @Column(name = "sender_email", nullable = false)
    private String email;

    @Column(name = "sender_message", columnDefinition = "TEXT", nullable = false)
    private String messageText;

    @Column(name = "is_read")
    private boolean isRead = false;
}
