package top.hazenix.auris.controller.user;

import com.aliyuncs.exceptions.ClientException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.constant.MessageConstant;
import top.hazenix.auris.dto.UpdateTrackDTO;
import top.hazenix.auris.entity.Track;
import top.hazenix.auris.query.ReOrderTracksQuery;
import top.hazenix.auris.query.TrackQuery;
import top.hazenix.auris.result.Result;
import top.hazenix.auris.service.ITrackService;
import top.hazenix.auris.utils.AliOssUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @description: 音乐管理相关接口
 * @author: Hazenix
 * @version: 1.0.0
 * @date: 2025/12/27
 * @return
 */
@RestController
@RequestMapping("/user/track")
@RequiredArgsConstructor
@Api("音乐管理相关接口")
@Slf4j
public class TrackController {
    private final ITrackService trackService;

    /**
     * @description: 根据歌单id获取歌曲列表
     * @param: id
     * @version: 1.0.0
     * @return
     */
    @GetMapping("/playlist/{id}")
    @ApiOperation("根据歌单id获取歌曲列表")
    public Result getTrackByPlaylistId(@PathVariable Long id){
        log.info("根据歌单id获取歌曲列表:{}",id);
        List<Track> list = trackService.getTrackByPlaylistId(id);
        return Result.success(list);
    }


    /**
     * @description: 根据歌单id向歌单添加歌曲
     * @param: trackQuery, file(可选)
     * @version: 1.0.0
     * @return
     */
    @PostMapping(value = "/playlist", consumes = {"multipart/form-data"})
    @ApiOperation("根据歌单id向歌单添加歌曲")
    public Result addTrack(
            @RequestParam("playlistId") Long playlistId,
            @RequestParam(value = "orderIndex", required = false, defaultValue = "0") Integer orderIndex,
            @RequestParam("title") String title,
            @RequestParam(value = "artist") String artist,
            @RequestParam(value = "album", required = false) String album,
            @RequestParam(value = "coverUrl", required = false) String coverUrl,
            @RequestParam(value = "file", required = false) MultipartFile file){
        TrackQuery trackQuery = TrackQuery.builder()
                .playlistId(playlistId)
                .orderIndex(orderIndex)
                .title(title)
                .artist(artist)
                .album(album)
                .coverUrl(coverUrl)
                .build();
        log.info("添加歌曲:{}", trackQuery);
        String url = trackService.addTrack(trackQuery, file);
        return Result.success(url);
    }

    /**
     * @description: 从歌单中移除歌曲
     * @param: id, trackId
     * @version: 1.0.0
     * @return
     */
    @DeleteMapping("/playlist/{id}/{trackId}")
    @ApiOperation("从歌单中移除歌曲")
    public Result removeTrack(@PathVariable Long id, @PathVariable Long trackId){
        log.info("从歌单中移除歌曲:{}, {}",id, trackId);
        trackService.removeTrack(id, trackId);
        return Result.success();
    }

    /**
     * @description: 重新排序歌单内歌曲
     * @param: id, trackId
     * @version: 1.0.0
     * @return
     */
    @PutMapping("/playlist/{id}/reorder")
    @ApiOperation("重新排序歌单内歌曲")
    public Result updateTrackSort(@RequestBody ReOrderTracksQuery reOrderTracksQuery, @PathVariable Long id){
        List<Long> ids = reOrderTracksQuery.getIds();
        log.info("重新排序歌单内歌曲:{}, {}",ids);
        trackService.updateTrackSort(id, ids);
        return Result.success();
    }



    /**
     * @description: 上传封面
     * @param: file
     * @version: 1.0.0
     * @return
     */
    @PostMapping("/{id}/cover")
    @ApiOperation("上传封面")
    public Result uploadCover(@PathVariable Long id,@RequestParam MultipartFile file){
        log.info("文件上传：{}",file.getOriginalFilename());
        //将文件交给OSS存储管理
        String url = trackService.uploadCover(id,file);
        return Result.success(url);
    }

    /**
     * @description: 上传音频（修改/添加当前歌曲音频）
     * @param: file
     * @version: 1.0.0
     * @return
     */
    @PostMapping("/{id}/audio")
    @ApiOperation("上传音频")
    public Result uploadAudio(@PathVariable Long id, @RequestParam MultipartFile file){
        log.info("音频上传：{}",file.getOriginalFilename());
        //将文件交给OSS存储管理
        String url = trackService.uploadAudio(id,file);
        return Result.success(url);
    }

    @GetMapping("/upload/credentials")
    @ApiOperation("获取STS临时凭证")
    public Result getTempCredentials() throws ClientException {
        log.info("获取STS临时凭证");
        Map<String,String> map = trackService.getTempCredentials();
        return Result.success(map);
    }

    /**
     * @description: 获取上传封面的临时凭证
     * @param: id
     * @version: 1.0.0
     * @return
     */
    @GetMapping("/{id}/cover/v2")
    @ApiOperation("获取上传封面的临时凭证")
    public Result getCoverUploadCredentials(@PathVariable Long id) throws ClientException {
        log.info("获取上传封面的临时凭证，歌曲ID：{}", id);
        // 参数校验：验证歌曲是否存在
        trackService.validateTrackExists(id);
        Map<String,String> map = trackService.getTempCredentials();
        return Result.success(map);
    }

    /**
     * @description: 获取上传音频的临时凭证
     * @param: id
     * @version: 1.0.0
     * @return
     */
    @GetMapping("/{id}/audio/v2")
    @ApiOperation("获取上传音频的临时凭证")
    public Result getAudioUploadCredentials(@PathVariable Long id) throws ClientException {
        log.info("获取上传音频的临时凭证，歌曲ID：{}", id);
        // 参数校验：验证歌曲是否存在
        trackService.validateTrackExists(id);
        Map<String,String> map = trackService.getTempCredentials();
        return Result.success(map);
    }

    /**
     * @description: 修改歌曲信息
     * @param: id, updateTrackDTO
     * @version: 1.0.0
     * @return
     */
    @PutMapping("/{id}")
    @ApiOperation("修改歌曲信息")
    public Result updateTrack(@PathVariable Long id, @RequestBody UpdateTrackDTO updateTrackDTO) {
        log.info("修改歌曲信息，歌曲ID：{}，更新内容：{}", id, updateTrackDTO);
        trackService.updateTrack(id, updateTrackDTO);
        return Result.success();
    }

    /**
     * @description: 获取当前用户的所有歌曲（单曲集合）
     * @version: 1.0.0
     * @return
     */
    @GetMapping("/all")
    @ApiOperation("获取当前用户的所有歌曲")
    public Result getAllTracks() {
        log.info("获取当前用户的所有歌曲");
        List<Track> list = trackService.getAllTracksByCurrentUser();
        return Result.success(list);
    }

    /**
     * @description: 删除歌曲（删除歌曲本身以及它在所有歌单中的关联）
     * @param: trackId 歌曲ID
     * @version: 1.0.0
     * @return
     */
    @DeleteMapping("/{trackId}")
    @ApiOperation("删除歌曲（删除歌曲本身以及它在所有歌单中的关联）")
    public Result deleteTrackCompletely(@PathVariable Long trackId) {
        log.info("删除歌曲（完全删除），歌曲ID：{}", trackId);
        trackService.deleteTrackCompletely(trackId);
        return Result.success();
    }

}