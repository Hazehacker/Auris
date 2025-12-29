package top.hazenix.auris.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hazenix.auris.entity.PlaylistTracks;
import top.hazenix.auris.entity.Track;

import java.util.List;

@Mapper
public interface PlaylistTracksMapper extends BaseMapper<PlaylistTracks> {

    /**
     * 根据歌单id获取歌曲列表
     * @param id
     * @return
     */
    List<Track> listTracksByPlaylistId(Long id);
}
