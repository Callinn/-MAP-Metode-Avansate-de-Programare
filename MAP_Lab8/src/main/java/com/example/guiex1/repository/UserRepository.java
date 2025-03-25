package com.example.guiex1.repository;
import java.util.List;
import com.example.guiex1.domain.User;
import com.example.guiex1.util.paging.Page;
import com.example.guiex1.util.paging.Pageable;

public interface UserRepository extends PagingRepository<Long, User>{
    Page<User> findAllOnPage(Pageable pageable);
}
