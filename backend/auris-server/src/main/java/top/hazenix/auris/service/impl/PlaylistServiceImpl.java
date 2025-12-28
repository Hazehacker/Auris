package top.hazenix.auris.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.hazenix.auris.context.BaseContext;
import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.mapper.PlaylistMapper;
import top.hazenix.auris.query.PlaylistQuery;
import top.hazenix.auris.service.IPlaylistService;

import java.time.LocalDateTime;
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
        playlistMapper.update(playList, updateWrapper);
    }
}