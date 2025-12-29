package top.hazenix.auris.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayHistory {
     private Long id;
     private Long trackId;
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private LocalDateTime listenedTime;
     private Long userId;
}

