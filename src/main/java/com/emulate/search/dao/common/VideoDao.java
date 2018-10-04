package com.emulate.search.dao.common;

import com.emulate.search.po.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoDao extends JpaRepository<Video,Integer>{

   public Video findVideoById(int id);
   public Video save(Video video);
}
