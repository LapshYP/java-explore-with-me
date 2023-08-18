package ru.practicum.ewmservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping
public class Controller {

    private final ServiceEwn serviceEwn;

    @GetMapping("/stats")
    public int doSmth() {
        return serviceEwn.getSmth();
    }

    @PostMapping("/hit")
    public int postSmth() {

        return serviceEwn.postSmth();
    }

}
