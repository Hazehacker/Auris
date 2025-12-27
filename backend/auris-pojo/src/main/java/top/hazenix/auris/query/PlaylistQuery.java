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
public class PlaylistQuery {

    private String name;
    private Integer sort;//默认为1
    private Boolean status;//默认为true(正常) false(删除)

}
