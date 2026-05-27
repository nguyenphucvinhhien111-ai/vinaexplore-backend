package com.vinatour.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinatour.backend.entity.UserFollower;
import com.vinatour.backend.entity.UserFollowerId;
@Repository
public interface UserFollowerRepository extends JpaRepository<UserFollower,UserFollowerId>{
    @EntityGraph(attributePaths={"followed"})
    List<UserFollower> findByFollowed_Id(Integer followedId);

    @EntityGraph(attributePaths = {"follower"})
    List<UserFollower> findByFollower_Id(Integer followerId);

    boolean existsByFollower_IdAndFollowed_Id(Integer followerId, Integer followedId);

    void deleteByFollower_IdAndFollowed_Id(Integer followerId, Integer followedId);
    
}
