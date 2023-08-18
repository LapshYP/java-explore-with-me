package ru.practicum.ewmservice.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController

@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/admin/users")
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        log.info("create POST /admin/users, userDto = {}", userDto);
        return new ResponseEntity(userService.create(userDto), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/users/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PositiveOrZero @PathVariable @Valid Long userId) {
        log.info("updateUser PATCH /admin/users/{userId} ids={}, userDto = {}", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PositiveOrZero @PathVariable @Valid Long userId) {
        log.info("deleteUser DELETE /admin/users/{userId} ids={}", userId);
        return new ResponseEntity(userService.deleteUser(userId), HttpStatus.valueOf(204));
    }

    @GetMapping("/admin/users/{ids}")
    public UserDto getUser(@PositiveOrZero @RequestParam @Valid Long ids) {
        log.info("getUser GET /admin/users/{ids}} ids={}", ids);
        return userService.getUser(ids);
    }


    @GetMapping("/admin/users")
    public List<UserDto> getAll(@PositiveOrZero @RequestParam(required = false) @Valid List<@Valid Long> ids,
                                @PositiveOrZero @RequestParam(defaultValue = "0") @Valid int from,
                                @Positive @RequestParam(defaultValue = "10") @Valid int size) {
        log.info("getAll GET admin/users?ids={{uid}} ids={}, from={}, size={}", ids, from, size);

        return userService.getAll(ids, from, size);
    }
}
