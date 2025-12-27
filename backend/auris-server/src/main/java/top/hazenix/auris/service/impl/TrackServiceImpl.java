package top.hazenix.auris.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.constant.MessageConstant;
import top.hazenix.auris.entity.Track;
import top.hazenix.auris.query.TrackQuery;
import top.hazenix.auris.service.ITrackService;
import top.hazenix.auris.utils.AliOssUtil;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements ITrackService {
    private final AliOssUtil aliOssUtil;


    @Override
    public List<Track> getTrackByPlaylistId(Long id) {

        return null;
    }

    @Override
    public String uploadCover(Long id, MultipartFile file) {
        //TODO 参数校验-健壮性

        String url = null;
        try {
            url = aliOssUtil.upload(file.getBytes(),file.getOriginalFilename());
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(MessageConstant.UPLOAD_FAILED);
        }
    }

    @Override
    public String uploadAudio(Long id, MultipartFile file) {
        return "";
    }

    @Override
    public void addTrack(TrackQuery trackQuery, MultipartFile file) {

    }
}
