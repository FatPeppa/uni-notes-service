package org.skyhigh.notesservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notesservice.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT * FROM public.category c WHERE c.id = ?1 AND c.user_id = ?2 LIMIT 1", nativeQuery = true)
    Category findByIdAndUserId(Long categoryId, Long userId);

    @Query(value = "SELECT * FROM public.category c WHERE c.user_id = ?2 AND c.name = ?1", nativeQuery = true)
    Category findByNameAndUserId(String name, Long userId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.category SET name = ?2, description = ?3, last_change_date = ?4 WHERE id = ?1", nativeQuery = true)
    void updateCategoryNameAndDescriptionAndLastChangeDateById(Long categoryId, String name, String description, ZonedDateTime lastChangeDate);

    @Query(value = "SELECT * FROM public.category c WHERE c.user_id = ?1", nativeQuery = true)
    List<Category> findCategoriesByUserId(Long userId);

    @Query(value = "SELECT * FROM public.category c WHERE c.user_id = ?1 ORDER BY c.created_date DESC", nativeQuery = true)
    List<Category> findCategoriesByUserIdOrderedByCreatedDateDesc(Long userId);

    @Query(value = "SELECT count(*) FROM public.category c WHERE c.user_id = ?1", nativeQuery = true)
    Integer countUserCategories(Long userId);
}
