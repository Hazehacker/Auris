package top.hazenix.auris.service;

import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.entity.Lyrics;

public interface ILyricsService {
    Lyrics getLyrics(Long id);

    void addLyrics(Long id, MultipartFile file);
}
