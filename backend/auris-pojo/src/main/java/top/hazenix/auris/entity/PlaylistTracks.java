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
public class PlaylistTracks {
    private Long id;
    private Long playlistId;
    private Long trackId;
    private Integer orderIndex;//倒序排序
    private LocalDateTime createdTime;
}
