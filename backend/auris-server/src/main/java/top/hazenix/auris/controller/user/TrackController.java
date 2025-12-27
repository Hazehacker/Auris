package top.hazenix.auris.controller.user;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.constant.MessageConstant;
import top.hazenix.auris.entity.Track;
import top.hazenix.auris.query.ReOrderTracksQuery;
import top.hazenix.auris.query.TrackQuery;
import top.hazenix.auris.result.Result;
import top.hazenix.auris.service.ITrackService;
import top.hazenix.auris.utils.AliOssUtil;

import java.io.IOException;
import java.util.List;

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
    public Result getTrackByPlaylistId(@PathVariable Long id){
        log.info("根据歌单id获取歌曲列表:{}",id);
        List<Track> list = trackService.getTrackByPlaylistId(id);
        return Result.success(list);
    }


    /**
     * @description: 根据歌单id向歌单添加歌曲
     * @param: id, trackQuery, file(可选)
     * @version: 1.0.0
     * @return
     */
    @PostMapping("/playlist/{id}")
    public Result addTrack(@PathVariable Long id, @RequestBody TrackQuery trackQuery, @RequestParam(required = false) MultipartFile file){
        log.info("添加歌曲:{}",trackQuery);
        trackService.addTrack(trackQuery,file);
        return Result.success();
    }

    /**
     * @description: 从歌单中移除歌曲
     * @param: id, trackId
     * @version: 1.0.0
     * @return
     */
    @DeleteMapping("/playlist/{id}/{trackId}")
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
    public Result updateTrackSort(@RequestBody ReOrderTracksQuery reOrderTracksQuery){
        List<Long> ids = reOrderTracksQuery.getIds();
        log.info("重新排序歌单内歌曲:{}, {}",ids);
        trackService.updateTrackSort(ids);
        return Result.success();
    }



    /**
     * @description: 上传封面
     * @param: file
     * @version: 1.0.0
     * @return
     */
    @PostMapping("/{id}/cover")
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
    public Result uploadAudio(@PathVariable Long id, @RequestParam MultipartFile file){
        log.info("音频上传：{}",file.getOriginalFilename());
        //将文件交给OSS存储管理
        String url = trackService.uploadAudio(id,file);
        return Result.success(url);
    }


}
