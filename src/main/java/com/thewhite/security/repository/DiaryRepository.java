package com.thewhite.security.repository;

import com.thewhite.security.model.Diary;
import com.whitesoft.core.repositories.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends BaseRepository<Diary> {

    List<Diary> findAllByOwner(String owner);
}