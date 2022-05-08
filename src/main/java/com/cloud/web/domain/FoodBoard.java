package com.cloud.web.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "food_board")
public class FoodBoard extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_food_board_to_users"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_type_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_food_board_to_location_type"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private LocationType locationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_type_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_food_board_to_food_type"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FoodType foodType;

    //
    @Column(name = "food_title" , nullable = false)
    private String title;

    @Column(name = "food_preview" , nullable = false)
    private String preview;

    @Column(name = "food_picture" )
    private String picture; // 사진 경로 타입 의문,,

    @Column(name = "food_address" , length = 2000 , nullable = false)
    private String address;

    @Column(name = "food_info" , length = 2000 , nullable = false)
    private String info;

    @Column(name = "food_rate" , nullable = false)
    private double rate;

    @Column(name = "food_like" )
    @ColumnDefault(" 0 ") // 의미 없음 @persist에 의해서
    private Integer like;

    @PrePersist
    public void prePersist(){
        this.like = this.like == null ? 0 : this.like;
    }

    public FoodBoard() {
    }

    @Builder
    public FoodBoard(User user, LocationType locationType, FoodType foodType, String title, String preview, String picture, String address, String info, double rate) {
        this.user = user;
        this.locationType = locationType;
        this.foodType = foodType;
        this.title = title;
        this.preview = preview;
        this.picture = picture;
        this.address = address;
        this.info = info;
        this.rate = rate;
    }
}
