package com.cloud.web.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "food_cmt" )
public class FoodCmt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_cmt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_food_cmt_to_users"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_board_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_food_cmt_to_food_board"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FoodBoard foodBoard;

    @Column(name = "food_cmt" , length = 2000 , nullable = false)
    private String foodCmt;


    public FoodCmt() {
    }

    @Builder
    public FoodCmt(User user, FoodBoard foodBoard, String foodCmt) {
        this.user = user;
        this.foodBoard = foodBoard;
        this.foodCmt = foodCmt;

    }
}
