package com.tamara.webcv.message;

import com.tamara.webcv.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "CONTACT_MESSAGES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Message extends BaseEntity {

    @Column(name = "SENDER_NAME", nullable = false)
    private String name;

    @Column(name = "SENDER_EMAIL", nullable = false)
    private String email;

    @Column(name = "SENDER_MESSAGE", columnDefinition = "TEXT", nullable = false)
    private String messageText;

    @Column(name = "IS_READ")
    private boolean isRead = false;
}
