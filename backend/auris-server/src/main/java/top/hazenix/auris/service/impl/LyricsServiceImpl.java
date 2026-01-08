package top.hazenix.auris.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.constant.MessageConstant;
import top.hazenix.auris.context.BaseContext;
import top.hazenix.auris.entity.Lyrics;
import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.entity.PlaylistTracks;
import top.hazenix.auris.entity.Track;
import top.hazenix.auris.mapper.LyricsMapper;
import top.hazenix.auris.mapper.PlaylistMapper;
import top.hazenix.auris.mapper.PlaylistTracksMapper;
import top.hazenix.auris.mapper.TrackMapper;
import top.hazenix.auris.service.ILyricsService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LyricsServiceImpl implements ILyricsService{
    private final LyricsMapper lyricsMapper;
    private final PlaylistMapper playlistMapper;
    private final PlaylistTracksMapper playlistTracksMapper;

    @Override
    public Lyrics getLyrics(Long id) {
        // 验证歌曲是否属于当前用户
        validateTrackOwnership(id);
        
        QueryWrapper<Lyrics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_id", id);
        Lyrics lyrics = lyricsMapper.selectOne(queryWrapper);
        return lyrics;
    }

    @Override
    public void addLyrics(Long id, MultipartFile file) {
        // 验证歌曲是否属于当前用户
        validateTrackOwnership(id);
        
        // 参数校验
        if (file == null) {
            throw new RuntimeException(MessageConstant.FILE_NOT_EXIST);
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith(".lrc")) {
            throw new RuntimeException(MessageConstant.LRC_ONLY);
        }

        // file存到数据库
        // 读取文件内容（UTF-8）
        String lyricsContent;
        try {
            lyricsContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(MessageConstant.FILE_READ_ERROR,e);
        }
        Lyrics lyrics = Lyrics.builder()
                .trackId(id)
//                .language("zh")
                .content(lyricsContent)
                .createTime(LocalDateTime.now())
                .build();
        lyricsMapper.insert(lyrics);
    }
    
    /**
     * 验证歌曲是否属于当前用户
     * @param trackId 歌曲ID
     */
    private void validateTrackOwnership(Long trackId) {
        if (trackId == null || trackId <= 0) {
            throw new RuntimeException("歌曲ID不能为空或无效");
        }
        
        // 通过playlist_tracks表检查当前用户是否拥有包含该歌曲的歌单
        QueryWrapper<PlaylistTracks> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_id", trackId);
        List<PlaylistTracks> playlistTracksList = playlistTracksMapper.selectList(queryWrapper);
        
        if (playlistTracksList.isEmpty()) {
            throw new RuntimeException("歌曲不存在或不属于当前用户");
        }
        
        // 检查是否至少有一个歌单属于当前用户
        boolean userOwnsTrack = false;
        Long currentUserId = BaseContext.getCurrentId();
        
        for (PlaylistTracks pt : playlistTracksList) {
            Long playlistId = pt.getPlaylistId();
            Playlist playlist = playlistMapper.selectById(playlistId);
            if (playlist != null && playlist.getUserId().equals(currentUserId)) {
                userOwnsTrack = true;
                break;
            }
        }
        
        if (!userOwnsTrack) {
            throw new RuntimeException("无权访问该歌曲");
        }
    }
}