package in.yogesh.removebg.service;

import in.yogesh.removebg.dto.UserDto;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    UserDto getUserByClerId(String id);
    void deleteUserByClerkId(String clerKId);

}
