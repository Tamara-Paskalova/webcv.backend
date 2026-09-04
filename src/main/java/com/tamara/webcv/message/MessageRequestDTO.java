package com.tamara.webcv.message;

import com.tamara.webcv.validation.SafeText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO {

    @NotBlank(message = "{validation.notblank}")
    @Email(message = "{message.validation.email}")
    @Size(max = 100, message = "{validation.size}")
    private String email;

    @NotBlank(message = "{validation.notblank}")
    @Size(max = 100, message = "{validation.size}")
    @SafeText
    private String name;

    @NotBlank(message = "{validation.notblank}")
    @Size(max = 3000, message = "{validation.size}")
    @SafeText
    private String message;
}
