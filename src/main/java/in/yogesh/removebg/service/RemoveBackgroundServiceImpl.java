package in.yogesh.removebg.service;

import in.yogesh.removebg.client.ClipDropClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RemoveBackgroundServiceImpl implements RemoveBackgroundService {
    @Value("${clipdrop.api}")
    private String apiKey;
    @Autowired
    private ClipDropClient client;
    @Override
    public byte[] removeBackground(MultipartFile file) {
        return  client.removeBackground(file,apiKey);
    }
}
