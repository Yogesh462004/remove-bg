package in.yogesh.removebg.service;

import in.yogesh.removebg.dto.UserDto;
import in.yogesh.removebg.entity.UserEntity;
import in.yogesh.removebg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDto saveUser(UserDto userDto) {
        Optional<UserEntity> optionaluser = userRepository.findByClerkid(userDto.getClerkId());
        if(optionaluser.isPresent()){
            UserEntity existinguser=optionaluser.get();
            existinguser.setEmail(userDto.getEmail());
            existinguser.setFirstName(userDto.getFirstName());
            existinguser.setFirstName(userDto.getLastName());
            existinguser.setPhotoUrl(userDto.getPhotoUrl());
            if(userDto.getCredits()!=null){
                existinguser.setCredits(userDto.getCredits());
            }
            existinguser=userRepository.save(existinguser);
            return mapToDto(existinguser);

        }
       UserEntity newuser= mapToEntity(userDto);
        userRepository.save(newuser);
        return mapToDto(newuser);
    }

    @Override
    public UserDto getUserByClerId(String id) {
       UserEntity userEntity= userRepository.findByClerkid(id).orElseThrow(()->
            new UsernameNotFoundException("User Not Found")
        );
       return mapToDto(userEntity);
    }

    @Override
    public void deleteUserByClerkId(String clerKId) {
        UserEntity userEntity=userRepository.findByClerkid(clerKId).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
        userRepository.delete(userEntity);
    }

    private UserDto mapToDto(UserEntity newuser) {
        return  UserDto.builder().clerkId(newuser.getClerkid())
                .firstName(newuser.getFirstName())
                .lastName(newuser.getLastName())
                .email(newuser.getEmail())
                .credits(newuser.getCredits())
                .photoUrl(newuser.getPhotoUrl())
                .build();
    }

    private UserEntity mapToEntity(UserDto userDto) {
       return UserEntity.builder().clerkid(userDto.getClerkId())
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .photoUrl(userDto.getPhotoUrl())
                .build();


    }
}
