package com.cloud.web.domain;

import com.cloud.web.domain.enums.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 * default 속성을 이용하고 싶을때는 columnDefinition 를 통해서 table에 들어갈 필드 속성을 모두 작성한다.
 * unique = true 속성을 사용하게 되면, uniqye 제약조건의 이름이 특정한 코드의 문자열로 할당된다.
 *
 * default값을 넣어줄때는 ColumnDefault를 통해서 Table에도 default값이 ~가 들어와야 한다고 명시를 하고
 * prePersist를 override하여 구현해주도록 한다.
 */
@Entity
@Getter @Setter
@Table(name = "users" )
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_loginId" , columnDefinition = "varchar(255) NOT NULL UNIQUE ")
    private String loginId; // 로그인 아이디

    @Column(name = "user_password" , nullable = false)
    private String password;

    @Column(name = "user_name" , nullable = false)
    private String name; // 사용자 실제 이름 ex) 이수찬

    @Column(name = "user_nickname" , columnDefinition = " varchar(255) NOT NULL UNIQUE ")
    private String nickname;

    @Column(name = "user_email" , nullable = false)
    private String email;

    //@Column(name = "user_type" , nullable = false )
    //@ColumnDefault(" 'USER'")
   // private String userType; // 권한을 의미하는 String "USER" , "ADMIN"

    @Enumerated(EnumType.STRING)
    @Column(length = 10 , nullable = false)
    @ColumnDefault("'USER'") // 의미 없음 @persist에 의해서 , 이렇게 해서 얻을 수 있는 이점은 prePersist할때 USER만 들어올  수 있고 , 다른 값이 들어오면 error 뱉는다.
    private RoleType roleType;

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    /**
     * persist() 메서드를 호출해서 엔티티를 영속성컨텍스트에 관리하기 직전에 호출 된다.
     * 식별자 생성 전략을 사용한 경우 엔티티에 식별자는 아직 존재 하지 않는다. 새로운 인스턴스를 merge할 때도 수행된다
     */
    @PrePersist
    public void prePersist(){
        this.roleType = this.roleType == null ? RoleType.USER : this.roleType;
    }

    public User() {
    }

    @Builder
    public User(String loginId, String password, String name, String nickname, String email) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;

    }
}
