package org.example.projeto.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErroResposta {
    private int status;
    private String mensagem;
    private LocalDateTime timestamp;
    private String path;
}