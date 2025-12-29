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
import java.nio.file.AccessDeniedException;
import java.sql.Wrapper;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LyricsServiceImpl implements ILyricsService{
    private final LyricsMapper lyricsMapper;
    private final PlaylistMapper playlistMapper;
    private final PlaylistTracksMapper playlistTracksMapper;

    @Override
    public Lyrics getLyrics(Long id) {
        QueryWrapper<Lyrics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_id", id);
        Lyrics lyrics = lyricsMapper.selectOne(queryWrapper);
        return lyrics;
    }

    @Override
    public void addLyrics(Long id, MultipartFile file) {
        // 参数校验
        if (file == null) {
            throw new RuntimeException(MessageConstant.FILE_NOT_EXIST);
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith(".lrc")) {
            throw new RuntimeException(MessageConstant.LRC_ONLY);
        }

        // 校验用户是否有权编辑这首歌
        QueryWrapper<PlaylistTracks> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_id", id);
        Long playlistId = playlistTracksMapper.selectOne(queryWrapper).getPlaylistId();
        Long userId = playlistMapper.selectOne(new LambdaQueryWrapper<Playlist>().eq(Playlist::getId, playlistId)).getUserId();
        if (!BaseContext.getCurrentId().equals(userId)) {
            throw new RuntimeException(MessageConstant.NO_AUTH_EDIT);
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
}
