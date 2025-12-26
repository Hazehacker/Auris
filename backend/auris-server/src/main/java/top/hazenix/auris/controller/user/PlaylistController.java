package top.hazenix.auris.controller.user;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.result.Result;
import top.hazenix.auris.service.IPlaylistService;

import java.util.List;

/**
 * @description: 歌单管理相关接口
 * @author: Hazenix
 * @version: 1.0.0
 * @date: 2025/12/26
 * @return
 */
@RestController
@RequestMapping("/user/playlist")
@RequiredArgsConstructor
@Api("歌单管理相关接口")
@Slf4j
public class PlaylistController {
    private final IPlaylistService playlistService;

    /**
     * @description: 获取所有歌单列表
     * @param:
     * @version: 1.0.0
     * @return
     */
    @GetMapping
    public Result getAllPlaylist(){
        log.info("获取所有歌单列表");
        List<Playlist> list = playlistService.getAllPlaylist();
        return Result.success(list);
    }





}
