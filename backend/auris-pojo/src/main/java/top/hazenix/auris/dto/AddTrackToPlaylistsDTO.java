package top.hazenix.auris.dto;

import lombok.Data;

import java.util.List;

/**
 * @description: 添加歌曲到多个歌单的DTO
 * @author: Hazenix
 * @version: 1.0.0
 * @date: 2025/01/04
 */
@Data
public class AddTrackToPlaylistsDTO {
    /**
     * 歌曲ID
     */
    private Long trackId;

    /**
     * 歌单ID列表
     */
    private List<Long> playlistIds;
}

