package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.domain.snap.repository.SnapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SnapService {

    private final SnapRepository snapRepository;


}
