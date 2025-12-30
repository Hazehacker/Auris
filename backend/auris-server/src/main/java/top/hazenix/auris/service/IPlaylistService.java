package top.hazenix.auris.service;

import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.query.PlaylistQuery;

import java.util.List;

public interface IPlaylistService {
    List<Playlist> getAllPlaylist();

    void createPlaylist(PlaylistQuery playlistQuery);

    void deletePlaylist(Long id);

    void updatePlaylist(PlaylistQuery playlistQuery);
}
