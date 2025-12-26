package top.hazenix.auris.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lyrics {
    private Long id;
    private Long trackId;
    private String language;
    private String content;
    private LocalDateTime createTime;
}
