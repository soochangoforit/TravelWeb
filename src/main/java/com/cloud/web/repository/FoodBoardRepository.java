package com.cloud.web.repository;

import com.cloud.web.domain.FoodBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodBoardRepository extends JpaRepository<FoodBoard,Long> , FoodBoardRepositoryCustom {

    // main에 갯수 제한으로 뿌려주기 위한 메소드
    @Query(value="SELECT * FROM food_board ORDER BY food_rate DESC LIMIT :num" , nativeQuery = true)
    List<FoodBoard> findByRateDescLimit(@Param("num") int num);

    @Query(value = "SELECT f FROM FoodBoard f ORDER BY f.id DESC")
    Page<FoodBoard> findAll(Pageable pageable);

}
