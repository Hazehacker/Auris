package top.hazenix.auris.controller.user;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.entity.Lyrics;
import top.hazenix.auris.result.Result;
import top.hazenix.auris.service.ILyricsService;

/**
 * @description: 歌单管理相关接口
 * @author: Hazenix
 * @version: 1.0.0
 * @date: 2025/12/26
 * @return
 */
@RestController
@RequestMapping("/user/lyrics")
@RequiredArgsConstructor
@Api("歌词管理相关接口")
@Slf4j
public class LyricsController {

    private final ILyricsService lyricsService;
    /**
     * @description: 根据歌曲id获取歌词
     * @param: id
     * @version: 1.0.0
     * @return
     */
    @GetMapping("/{id}")
    public Result getLyrics(@PathVariable Long id){
        log.info("获取歌词:{}",id);
        Lyrics lyrics = lyricsService.getLyrics(id);
        return Result.success(lyrics);
    }

    /**
     * @description: 根据歌曲id添加歌词
     * @param: id, file
     * @version: 1.0.0
     * @return
     */
    @PostMapping("/{id}")
    public Result addLyrics(@PathVariable Long id, @RequestParam MultipartFile file){
        log.info("添加歌词:{}",id);
        lyricsService.addLyrics(id,file);
        return Result.success();
    }

}
