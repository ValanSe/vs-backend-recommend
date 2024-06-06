package com.valanse.recommend.repository.jpa;


import com.valanse.recommend.entity.FavoriteCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteCategoryRepository extends JpaRepository<FavoriteCategory, Integer> {

    FavoriteCategory findByUserId(Integer userId);

    List<FavoriteCategory> findAllByCategory(String category);
}
