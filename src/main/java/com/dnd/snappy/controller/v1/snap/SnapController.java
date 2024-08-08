package com.dnd.snappy.controller.v1.snap;

import com.dnd.snappy.domain.snap.service.SnapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/snaps")
@RequiredArgsConstructor
public class SnapController {

    private final SnapService snapService;

}
