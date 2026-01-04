package top.hazenix.auris.dto;

import lombok.Data;

/**
 * @description: 更新歌曲信息的DTO
 * @author: Hazenix
 * @version: 1.0.0
 * @date: 2025/01/01
 */
@Data
public class UpdateTrackDTO {
    private String title;
    private String artist;
    private String album;
    private Integer duration;
    private String filePath;
    private String coverUrl;
    private String sourceType;
}

