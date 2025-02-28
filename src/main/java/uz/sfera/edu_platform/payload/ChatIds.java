package uz.sfera.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChatIds {
    private List<Long> ids;

    @JsonCreator
    public ChatIds(@JsonProperty("ids") List<Long> ids)
    {
        this.ids = ids;
    }
}
