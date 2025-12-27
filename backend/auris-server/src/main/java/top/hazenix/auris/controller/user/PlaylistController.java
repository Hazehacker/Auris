package top.hazenix.auris.controller.user;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.query.PlaylistQuery;
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

    /**
     * @description: 创建新歌单
     * @param: playlistQuery
     * @version: 1.0.0
     * @return
     */
    @PostMapping
    public Result createPlaylist(@RequestBody PlaylistQuery playlistQuery){
        log.info("创建新歌单:{}",playlistQuery);
        playlistService.createPlaylist(playlistQuery);
        return Result.success();
    }

    /**
     * @description: 修改歌单信息
     * @param: playlistQuery
     * @version: 1.0.0
     * @return
     */
    @PutMapping
    public Result updatePlaylist(@RequestBody PlaylistQuery playlistQuery){
        log.info("修改歌单信息:{}",playlistQuery);
        playlistService.updatePlaylist(playlistQuery);
        return Result.success();
    }

    /**
     * @description: 删除歌单
     * @param: id
     * @version: 1.0.0
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deletePlaylist(@PathVariable Long id){
        log.info("删除歌单:{}",id);
        playlistService.deletePlaylist(id);
        return Result.success();
    }





}
