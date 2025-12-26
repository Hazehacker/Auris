package top.hazenix.auris.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.hazenix.auris.entity.Playlist;

import java.util.List;

@Mapper
public interface PlaylistMapper {
    List<Playlist> getAllPlaylist(Long userId);
}
