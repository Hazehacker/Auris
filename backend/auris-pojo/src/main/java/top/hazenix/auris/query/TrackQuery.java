package top.hazenix.auris.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackQuery {
    private String title;//标题，必填
    private String artist;//歌手
    private String album;//专辑
    private String coverUrl;//封面路径(可选)
//    private String sourceType;

}
