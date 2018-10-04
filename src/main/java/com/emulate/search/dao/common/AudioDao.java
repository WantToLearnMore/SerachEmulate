package com.emulate.search.dao.common;

import com.emulate.search.po.Audio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AudioDao extends JpaRepository<Audio,Integer>{

  public Audio findAudioById(int id);
  public List<Audio>findAudioByName(String name);
  //public List<Audio>findAudioByAuthorAndAndNameAnd(String key);


}
