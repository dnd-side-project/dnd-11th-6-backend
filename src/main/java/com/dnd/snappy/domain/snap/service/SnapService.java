package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.domain.snap.repository.SnapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnapService {

    private final SnapRepository snapRepository;

}
