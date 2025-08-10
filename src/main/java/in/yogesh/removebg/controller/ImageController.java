package in.yogesh.removebg.controller;

import in.yogesh.removebg.dto.UserDto;
import in.yogesh.removebg.response.RemoveBgResponse;
import in.yogesh.removebg.service.RemoveBackgroundService;
import in.yogesh.removebg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    @Autowired
    private final RemoveBackgroundService removeBackgroundService;
    @Autowired
    private final UserService userService;

    @PostMapping("/remove-background")
    public ResponseEntity<?> removeBackGround(@RequestParam("file")MultipartFile file, Authentication authentication){
        RemoveBgResponse response=new RemoveBgResponse();
        Map<String,Object> responseMap=new HashMap<>();
        try{
            if(authentication.getName().isEmpty()||authentication.getName()==null){
                response=RemoveBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .success(false)
                        .data("User does not have permission/access to this resource ")
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            UserDto  dto=userService.getUserByClerId(authentication.getName());
            if(dto.getCredits()==0){
                responseMap.put("message","No credit Balance");
                responseMap.put("creditBalance",dto.getCredits());
                response=RemoveBgResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .success(false)
                        .data(responseMap)
                        .build();
                return ResponseEntity.ok(response);
            }
           byte[] imageBytes= removeBackgroundService.removeBackground(file);
            String baseImage=Base64.getEncoder().encodeToString(imageBytes);
            dto.setCredits(dto.getCredits()-1);
            userService.saveUser(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN).body(baseImage);
        }catch (Exception e){
            response=RemoveBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .success(false)
                    .data("Something went wrong ")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
