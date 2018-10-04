package com.emulate.search.service;

import com.emulate.search.po.Video;

import java.util.List;

public interface SearchService {
    public Video findVideoById(int id);
    public Video saveVideo(Video videos);
}
