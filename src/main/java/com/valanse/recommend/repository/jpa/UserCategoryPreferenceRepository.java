package com.valanse.recommend.repository.jpa;

import com.valanse.recommend.entity.UserCategoryPreference;
import com.valanse.recommend.entity.UserCategoryPreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCategoryPreferenceRepository extends JpaRepository<UserCategoryPreference, UserCategoryPreferenceId> {

    List<UserCategoryPreference> findAllByUserId(Integer userId);
}
