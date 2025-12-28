package top.hazenix.auris.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hazenix.auris.entity.Playlist;
import top.hazenix.auris.entity.Track;

import java.util.List;

@Mapper
public interface TrackMapper extends BaseMapper<Track> {

}
