package in.yogesh.removebg.controller;

import in.yogesh.removebg.dto.UserDto;
import in.yogesh.removebg.response.RemoveBgResponse;
import in.yogesh.removebg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @PostMapping
    public ResponseEntity<RemoveBgResponse> createOrUpdateUser(@RequestBody UserDto userDto, Authentication auth) {

        if (!auth.getName().equals(userDto.getClerkId())) {
            RemoveBgResponse response = RemoveBgResponse.builder()
                    .success(false)
                    .statusCode(HttpStatus.FORBIDDEN)
                    .data("User does not have permission to access resource")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            UserDto user = userService.saveUser(userDto);
            RemoveBgResponse response = RemoveBgResponse.builder()
                    .success(true)
                    .data(user)
                    .statusCode(HttpStatus.OK)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            RemoveBgResponse response = RemoveBgResponse.builder()
                    .success(false)
                    .data(e.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/credits")
    public  ResponseEntity<?> getCredits(Authentication authentication){
        RemoveBgResponse response=null;
        try{
            if(authentication.getName().isEmpty()||authentication.getName()==null){
                response=RemoveBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .data("User does not have permission/access to this resource")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            String clerkId = authentication.getName();
            UserDto existinguser=userService.getUserByClerId(clerkId);
            Map<String,Integer> map=new HashMap<>();
            map.put("credits",existinguser.getCredits());
            response=RemoveBgResponse.builder()
                    .statusCode(HttpStatus.OK)
                    .data(map)
                    .success(true)
                    .build();
            return  ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            response=RemoveBgResponse.builder()
                    .statusCode(HttpStatus.OK)
                    .data("something went wrong")
                    .success(false)
                    .build();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
}
