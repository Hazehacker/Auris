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
public class Track {
    private Long id;
    private String title;
    private String artist;
    private String album;
    private Integer duration;
    private String filePath;
    private String coverUrl;
    private String sourceType;
    private LocalDateTime createTime;
}
