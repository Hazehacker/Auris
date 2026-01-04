package top.hazenix.auris.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 批量插入歌曲到歌单
     * @param playlistTracksList
     */
    void batchInsert(List<PlaylistTracks> playlistTracksList);

    /**
     * 检查歌曲是否已在歌单中
     * @param playlistId
     * @param trackId
     * @return
     */
    PlaylistTracks findByPlaylistIdAndTrackId(@Param("playlistId") Long playlistId, @Param("trackId") Long trackId);

    /**
     * 获取歌单中最大的orderIndex
     * @param playlistId
     * @return
     */
    Integer getMaxOrderIndexByPlaylistId(@Param("playlistId") Long playlistId);

    /**
     * 根据用户ID获取该用户的所有歌曲（去重）
     * @param userId
     * @return
     */
    List<Track> listTracksByUserId(@Param("userId") Long userId);
}
