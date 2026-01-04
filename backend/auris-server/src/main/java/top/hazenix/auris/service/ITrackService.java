package top.hazenix.auris.service;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.dto.UpdateTrackDTO;
import top.hazenix.auris.entity.Track;
import top.hazenix.auris.query.TrackQuery;

import java.util.List;
import java.util.Map;

public interface ITrackService {
    List<Track> getTrackByPlaylistId(Long id);

    String uploadCover(Long id, MultipartFile file);

    String uploadAudio(Long id, MultipartFile file);

    String addTrack(TrackQuery trackQuery, MultipartFile file);

    void removeTrack(Long id, Long trackId);

    void updateTrackSort(Long id, List<Long> ids);

    Map<String, String> getTempCredentials() throws ClientException;

    void validateTrackExists(Long id);

    void updateTrack(Long id, UpdateTrackDTO updateTrackDTO);

    /**
     * 获取当前用户的所有歌曲
     * @return
     */
    List<Track> getAllTracksByCurrentUser();

    /**
     * 删除歌曲（删除歌曲本身以及它在所有歌单中的关联）
     * @param trackId 歌曲ID
     */
    void deleteTrackCompletely(Long trackId);
}
