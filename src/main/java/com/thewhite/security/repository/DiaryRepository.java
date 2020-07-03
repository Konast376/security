package com.thewhite.security.repository;

import com.thewhite.security.model.Diary;
import com.whitesoft.core.repositories.BaseRepository;

import java.util.Optional;

public interface DiaryRepository extends BaseRepository<Diary> {

    Optional<Diary> findByOwner(String owner);
}