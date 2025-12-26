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
public class Playlist {
    private Long id;
    private String name;
    private Integer sort;
    private String slug;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long userId;
}
