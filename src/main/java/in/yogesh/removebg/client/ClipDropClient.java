package in.yogesh.removebg.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

@FeignClient(name="clipdropClient",url="https://clipdrop-api.co")
public interface ClipDropClient {
    @PostMapping(value = "/remove-background/v1",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    byte[]removeBackground(@RequestPart("image_file")MultipartFile file
    , @RequestHeader("x-api-key")String apiKey);
}
