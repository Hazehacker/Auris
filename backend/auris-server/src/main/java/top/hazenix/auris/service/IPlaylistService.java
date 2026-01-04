package top.hazenix.auris.service;

import top.hazenix.auris.dto.AddTrackToPlaylistsDTO;
import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.query.PlaylistQuery;

import java.util.List;

public interface IPlaylistService {
    List<Playlist> getAllPlaylist();

    void createPlaylist(PlaylistQuery playlistQuery);

    void deletePlaylist(Long id);

    void updatePlaylist(PlaylistQuery playlistQuery);

    /**
     * 添加歌曲到多个歌单
     * @param addTrackToPlaylistsDTO
     */
    void addTrackToPlaylists(AddTrackToPlaylistsDTO addTrackToPlaylistsDTO);
}
