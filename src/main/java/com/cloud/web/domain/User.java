package com.cloud.web.domain;

import com.cloud.web.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * default 속성을 이용하고 싶을때는 columnDefinition 를 통해서 table에 들어갈 필드 속성을 '모두' 작성한다.
 * unique = true 속성을 사용하게 되면, unique 제약조건의 이름이 특정한 코드의 문자열로 할당된다.
 *
 * 또 다른 방법으로 default값을 넣어줄때는 ColumnDefault를 통해서 Table에도 default값이 ~가 들어와야 한다고 명시를 하고
 * (실제 의미 X , 동작 시점은 save()를 통해서 persist 하기전에 prepersist가 먼저 호출될때 값을 할당한다.
 * 따라서 테이블에는 그냥 명시만 해주고 실제 동작은 prePersist()를 통해서 override 구현해주도록 한다.
 */
@Entity
@Getter
@Table(name = "users" )
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_loginId" , columnDefinition = "varchar(255) NOT NULL UNIQUE ")
    private String loginId; // 사용자 로그인 아이디

    @Column(name = "user_password" , nullable = false)
    private String password;

    @Column(name = "user_name" , nullable = false )
    private String name; // 사용자 실제 이름 ex) 이수찬

    @Column(name = "user_nickname" , columnDefinition = " varchar(255) NOT NULL UNIQUE ")
    private String nickname;

    @Column(name = "user_email" , nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10 , nullable = false)
    private Role roleType;


    /**
     * User 입자에서 자신이 등록한 게시물의 리스트를 확인한다.
     * 일대다 양방향 연관관계에서 List로 사용해서 활용 추가 - 2022-05-19
     *
     * 2022-06-24
     * 처음에는 @OneToMany에 대해서, cascadeType.ALL를 붙여줬지만
     * 잘 생각해보면, User가 persist 될때 (즉, 회원가입을 할때) 게시물도 함께
     * persist되는 영속성 전이는 올바르지 않는 방향이므로, cascadeType.ALL 삭제함!
     */
    @OneToMany(mappedBy = "user" )
    private List<FoodBoard> foodBoards = new ArrayList<>();

    /**
     * 연관 관계 편의 메서드
     * User Entity가 Db에 저장되는 시점에는 게시글이 없음으로
     * 게시글이 save될때 해당 user의 게시글 list에 데이터를 넣어준다.
     */
    public void addFoodBoard(FoodBoard foodBoard) {
        // foodBoard.setUser(user); 이런식으로 하면
        // 연관관게 편의 메서드란 .. 하나의 Entity를 저장할때 mappedBy된 객체들도 함께 persist하기 위함이다.
        // 그러나 User가 persist될때 함께 Board가 persist 되는것은 아니므로
        // foodBoard는 따로 post()를 통해서 persist 한다. 대신에 User도 board를 알기 위해서 List 컬렉션을 사용해서 관리 해주도록 한다.
        this.foodBoards.add(foodBoard);
    }


    public User() {
    }

    @Builder
    public User(String loginId, String password, String name, String nickname, String email, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.roleType = role;

    }
}
