package top.hazenix.auris.service;

import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.entity.Track;
import top.hazenix.auris.query.TrackQuery;

import java.util.List;

public interface ITrackService {
    List<Track> getTrackByPlaylistId(Long id);

    String uploadCover(Long id, MultipartFile file);

    String uploadAudio(Long id, MultipartFile file);

    String addTrack(TrackQuery trackQuery, MultipartFile file);

    void removeTrack(Long id, Long trackId);

    void updateTrackSort(Long id, List<Long> ids);
}
