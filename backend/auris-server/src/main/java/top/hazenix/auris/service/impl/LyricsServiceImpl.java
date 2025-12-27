package top.hazenix.auris.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.hazenix.auris.entity.Lyrics;
import top.hazenix.auris.service.ILyricsService;

@Service
@RequiredArgsConstructor
public class LyricsServiceImpl implements ILyricsService{
    @Override
    public Lyrics getLyrics(Long id) {
        return null;
    }

    @Override
    public void addLyrics(Long id, MultipartFile file) {

    }
}
