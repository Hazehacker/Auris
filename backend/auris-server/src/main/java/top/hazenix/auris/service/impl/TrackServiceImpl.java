package top.hazenix.auris.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.constant.MessageConstant;
import top.hazenix.auris.entity.PlaylistTracks;
import top.hazenix.auris.entity.Track;
import top.hazenix.auris.mapper.PlaylistTracksMapper;
import top.hazenix.auris.mapper.TrackMapper;
import top.hazenix.auris.query.TrackQuery;
import top.hazenix.auris.service.ITrackService;
import top.hazenix.auris.utils.AliOssUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackServiceImpl implements ITrackService {
    private final AliOssUtil aliOssUtil;
    private final TrackMapper trackMapper;
    private final PlaylistTracksMapper playlistTracksMapper;

    @Override
    public List<Track> getTrackByPlaylistId(Long id) {
        List<Track> list = playlistTracksMapper.listTracksByPlaylistId(id);
        return list;
    }

    @Override
    public String uploadCover(Long id, MultipartFile file) {
        //参数校验-健壮性
        if (file == null) {
            throw new RuntimeException(MessageConstant.FILE_NOT_EXIST);
        }
        Track track = trackMapper.selectById(id);
        if(track == null){
            throw new RuntimeException(MessageConstant.TRACK_NOT_EXIST);
        }
        // 校验file是否是图片
        if (!file.getContentType().startsWith("image")) {
            throw new RuntimeException(MessageConstant.FILE_NOT_IMAGE);
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            List<String> imageExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
            if (!imageExtensions.contains(extension.toLowerCase())) {
                throw new RuntimeException(MessageConstant.FILE_NOT_IMAGE);
            }
        }

        //上传封面，更新属性
        String url = null;
        url = fileUpload(file);
        track.setCoverUrl(url);
        trackMapper.updateById(track);
        return url;
    }

    @Override
    public String uploadAudio(Long id, MultipartFile file) {
        // 参数校验-健壮性
        if (file == null) {
            throw new RuntimeException(MessageConstant.FILE_NOT_EXIST);
        }
        Track track = trackMapper.selectById(id);
        if(track == null){
            throw new RuntimeException(MessageConstant.TRACK_NOT_EXIST);
        }
        // 校验file是否为音频
        if (!file.getContentType().startsWith("audio")) {
            throw new RuntimeException(MessageConstant.FILE_NOT_AUDIO);
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            List<String> audioExtensions = Arrays.asList(".mp3", ".wav", ".flac", ".aac", ".ogg", ".m4a");
            if (!audioExtensions.contains(extension.toLowerCase())) {
                throw new RuntimeException(MessageConstant.FILE_NOT_AUDIO);
            }
        }

        //上传音频，更新属性
        String url = null;
        url = fileUpload(file);
        track.setFilePath(url);
        trackMapper.updateById(track);
        return url;
    }

    /**
     * @description: 添加歌曲
     * @param: trackQuery, file
     * @version: 1.0.0
     * @return
     */
    @Override
    @Transactional
    public String addTrack(TrackQuery trackQuery, MultipartFile file) {
        Track track = Track.builder()
                .title(trackQuery.getTitle())
                .artist(trackQuery.getArtist())
                .album(trackQuery.getAlbum())
//                .duration(trackQuery.getDuration())//TODO 考虑添加，前端多传一个参数
                .coverUrl(trackQuery.getCoverUrl())
                .build();
        String url = null;
        try {
            if(file != null){
                url = fileUpload(file);
            }
        } finally {
            track.setFilePath(url);
            track.setCreateTime(LocalDateTime.now());
            trackMapper.insert(track);
            PlaylistTracks playlistTracks = PlaylistTracks.builder()
                    .playlistId(trackQuery.getPlaylistId())
                    .trackId(track.getId())
                    .orderIndex(trackQuery.getOrderIndex())
                            .build();
            playlistTracksMapper.insert(playlistTracks);
            return url;
        }
    }

    String fileUpload(MultipartFile file){
        log.info("文件上传：{}",file.getOriginalFilename());
        //将文件交给OSS存储管理
        String url;
        try {
            url = aliOssUtil.upload(file.getBytes(),file.getOriginalFilename());
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(MessageConstant.UPLOAD_FAILED);
        }
    }

    @Override
    @Transactional
    public void removeTrack(Long id, Long trackId) {
        // 硬删除
        QueryWrapper<PlaylistTracks> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id",id);
        queryWrapper.eq("track_id",trackId);
        playlistTracksMapper.delete(queryWrapper);
        trackMapper.deleteById(trackId);
    }

    @Override
    public void updateTrackSort(List<Long> ids) {

    }
}
