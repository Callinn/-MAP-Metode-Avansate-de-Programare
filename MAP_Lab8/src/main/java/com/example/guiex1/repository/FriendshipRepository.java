package com.example.guiex1.repository;

import com.example.guiex1.domain.Friendship;
import com.example.guiex1.util.paging.Page;
import com.example.guiex1.util.paging.Pageable;

public interface FriendshipRepository extends PagingRepository<Long, Friendship> {
    Page<Friendship> findAllOnPage(Pageable pageable);
}