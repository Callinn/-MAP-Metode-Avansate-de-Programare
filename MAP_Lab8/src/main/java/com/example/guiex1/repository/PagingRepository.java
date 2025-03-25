package com.example.guiex1.repository;

import com.example.guiex1.domain.Entity;
import com.example.guiex1.util.paging.Page;
import com.example.guiex1.util.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}
