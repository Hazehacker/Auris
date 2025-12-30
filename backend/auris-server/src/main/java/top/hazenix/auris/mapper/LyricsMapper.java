package top.hazenix.auris.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hazenix.auris.entity.Lyrics;
import top.hazenix.auris.entity.Track;

@Mapper
public interface LyricsMapper extends BaseMapper<Lyrics> {

}
