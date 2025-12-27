package top.hazenix.auris.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.hazenix.auris.context.BaseContext;
import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.mapper.PlaylistMapper;
import top.hazenix.auris.query.PlaylistQuery;
import top.hazenix.auris.service.IPlaylistService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements IPlaylistService {
    private final PlaylistMapper playlistMapper;
    @Override
    public List<Playlist> getAllPlaylist() {
        Long userId = BaseContext.getCurrentId();
        return playlistMapper.getAllPlaylist(userId);
    }

    @Override
    public void createPlaylist(PlaylistQuery playlistQuery) {

    }

    @Override
    public void deletePlaylist(Long id) {

    }

    @Override
    public void updatePlaylist(PlaylistQuery playlistQuery) {

    }
}