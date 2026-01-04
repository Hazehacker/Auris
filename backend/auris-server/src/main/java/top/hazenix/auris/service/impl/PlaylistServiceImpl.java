package top.hazenix.auris.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.hazenix.auris.constant.MessageConstant;
import top.hazenix.auris.context.BaseContext;
import top.hazenix.auris.dto.AddTrackToPlaylistsDTO;
import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.entity.PlaylistTracks;
import top.hazenix.auris.entity.Track;
import top.hazenix.auris.mapper.PlaylistMapper;
import top.hazenix.auris.mapper.PlaylistTracksMapper;
import top.hazenix.auris.mapper.TrackMapper;
import top.hazenix.auris.service.ITrackService;
import top.hazenix.auris.query.PlaylistQuery;
import top.hazenix.auris.service.IPlaylistService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements IPlaylistService {
    private final PlaylistMapper playlistMapper;
    private final ITrackService trackService;
    private final PlaylistTracksMapper playlistTracksMapper;
    private final TrackMapper trackMapper;

    @Override
    public List<Playlist> getAllPlaylist() {
        Long userId = BaseContext.getCurrentId();
        return playlistMapper.getAllPlaylist(userId);
    }

    @Override
    public void createPlaylist(PlaylistQuery playlistQuery) {
        //参数校验
        if (playlistQuery.getSort() < 0) {
            throw new RuntimeException("排序值不能小于0");
        }
        if (playlistQuery.getStatus() == null) {
            playlistQuery.setStatus(true);
        }

        Playlist playList = Playlist.builder()
                .name(playlistQuery.getName())
                .sort(playlistQuery.getSort())
                .status(playlistQuery.getStatus())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .userId(BaseContext.getCurrentId())
                .build();
        playlistMapper.insert(playList);
    }

    @Override
    public void deletePlaylist(Long id) {
        //如果歌单关联了歌曲，不能删除
        if (trackService.getTrackByPlaylistId(id).size() > 0) {
            throw new RuntimeException(MessageConstant.PLAYLIST_BE_RELATED_BY_TRACK);
        }

        Playlist playList = Playlist.builder()
                .id(id)
                .status(false)
                .updateTime(LocalDateTime.now())
                .build();
        UpdateWrapper<Playlist> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.eq("user_id", BaseContext.getCurrentId());//健壮性-只能删除自己的歌单
        playlistMapper.update(playList, updateWrapper);
    }

    @Override
    public void updatePlaylist(PlaylistQuery playlistQuery) {
        //参数校验
        if (playlistQuery.getSort() < 0) {
            throw new RuntimeException("排序值不能小于0");
        }
        if (playlistQuery.getStatus() == null) {
            playlistQuery.setStatus(true);
        }
        Playlist playList = Playlist.builder()
                .name(playlistQuery.getName())
                .sort(playlistQuery.getSort())
                .status(playlistQuery.getStatus())
                .updateTime(LocalDateTime.now())
                .build();
        UpdateWrapper<Playlist> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", playlistQuery.getId());//创建了一个 UpdateWrapper 对象来指定更新条件
        // 使用 eq("id", playlistQuery.getId()) 来指定更新条件，即只更新ID匹配的记录
        updateWrapper.eq("user_id", BaseContext.getCurrentId());//健壮性-只能修改自己的歌单
        playlistMapper.update(playList, updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTrackToPlaylists(AddTrackToPlaylistsDTO addTrackToPlaylistsDTO) {
        Long userId = BaseContext.getCurrentId();
        Long trackId = addTrackToPlaylistsDTO.getTrackId();
        List<Long> playlistIds = addTrackToPlaylistsDTO.getPlaylistIds();

        // 参数校验
        if (trackId == null) {
            throw new RuntimeException("歌曲ID不能为空");
        }
        if (playlistIds == null || playlistIds.isEmpty()) {
            throw new RuntimeException("歌单ID列表不能为空");
        }

        // 检查歌曲是否存在
        Track track = trackMapper.selectById(trackId);
        if (track == null) {
            throw new RuntimeException("歌曲不存在");
        }

        // 检查歌单是否存在且属于当前用户
        List<Playlist> playlists = new ArrayList<>();
        for (Long playlistId : playlistIds) {
            Playlist playlist = playlistMapper.selectById(playlistId);
            if (playlist == null) {
                throw new RuntimeException("歌单不存在，ID: " + playlistId);
            }
            if (!playlist.getUserId().equals(userId)) {
                throw new RuntimeException("无权操作该歌单，ID: " + playlistId);
            }
            if (playlist.getStatus() == null || !playlist.getStatus()) {
                throw new RuntimeException("歌单已删除，ID: " + playlistId);
            }
            playlists.add(playlist);
        }

        // 批量添加歌曲到歌单（跳过已存在的关联）
        List<PlaylistTracks> playlistTracksList = new ArrayList<>();
        for (Playlist playlist : playlists) {
            // 检查歌曲是否已在歌单中
            PlaylistTracks existing = playlistTracksMapper.findByPlaylistIdAndTrackId(playlist.getId(), trackId);
            if (existing != null) {
                // 如果已存在，跳过
                continue;
            }

            // 获取当前歌单的最大orderIndex
            Integer maxOrderIndex = playlistTracksMapper.getMaxOrderIndexByPlaylistId(playlist.getId());
            if (maxOrderIndex == null) {
                maxOrderIndex = 0;
            }

            // 创建新的关联记录
            PlaylistTracks playlistTracks = PlaylistTracks.builder()
                    .playlistId(playlist.getId())
                    .trackId(trackId)
                    .orderIndex(maxOrderIndex + 1) // 新添加的歌曲排在前面（倒序排序）
                    .build();
            playlistTracksList.add(playlistTracks);
        }

        // 批量插入
        if (!playlistTracksList.isEmpty()) {
            playlistTracksMapper.batchInsert(playlistTracksList);
        }
    }
}