package ru.practicum.ewmservice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.service.UserService;


import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity <UserDto> createUser(@RequestBody UserDto userDTO) {

        return new ResponseEntity(userService.createUserSerivce(userDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDTO, @PathVariable Long userId) {
        return userService.updateUserService(userDTO, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity <UserDto> deleteUser(@PathVariable Long userId) {

        return new ResponseEntity( userService.deleteUserService(userId), HttpStatus.valueOf(204));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUserSerivece(userId);
    }

    @GetMapping
    @ResponseBody
    public List<UserDto> getUsers() {
        return userService.getAll();
    }
}
