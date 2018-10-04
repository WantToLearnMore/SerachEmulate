package com.emulate.search.service.Imp;

import com.emulate.search.dao.common.VideoDao;
import com.emulate.search.po.Video;
import com.emulate.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImp implements SearchService {


    @Autowired
    private VideoDao videoDao;


    @Override
    public Video findVideoById(int id) {

        Video video=videoDao.findVideoById(id);
        System.out.println(video.getAuthor());
        return video;
    }

    @Override
    public Video saveVideo(Video video){
        return videoDao.save(video);
    }

}
