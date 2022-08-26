# TravelWeb

---

## Contents

- ****[Introduction](https://www.notion.so/ReadMe-md-d68c52ac151646fb89d2d787c919d1d2)****
- ****[Requriements](https://www.notion.so/ReadMe-md-d68c52ac151646fb89d2d787c919d1d2)****
- ****[Installation](https://www.notion.so/ReadMe-md-d68c52ac151646fb89d2d787c919d1d2)****
- ****[Contributing](https://www.notion.so/ReadMe-md-d68c52ac151646fb89d2d787c919d1d2)****
- ****[Maintainer](https://www.notion.so/ReadMe-md-d68c52ac151646fb89d2d787c919d1d2)****

## Introduction

---

- 2022년도 3학년 1학기 Web Server Project, SSR으로 개발하였습니다.
- 전국 맛집, 부산 관광 명소 공유하는 웹 사이트

- **부산 관광 명소**
    
    ![Untitled](ReadMe%20md%2088f7c14381d8449ba8b5bde878fbd276/Untitled.png)
    

- **전국 맛집 게시글**
    
    ![Untitled](ReadMe%20md%2088f7c14381d8449ba8b5bde878fbd276/Untitled%201.png)
    
- **맛집 게시글 상세 페이지**
    
    ![Untitled](ReadMe%20md%2088f7c14381d8449ba8b5bde878fbd276/Untitled%202.png)
    

- **부산 관광 명소 한눈에 보기**
    
    ![Untitled](ReadMe%20md%2088f7c14381d8449ba8b5bde878fbd276/Untitled%203.png)
    

**<기능>**

- 맛집 게시글 CRUD
- 지역, 음식 유형(ex. 일식,중식..), 맛집 이름, 평점순으로 검색 및 페이징 처리
- 부산 지역 관광 명소 Open API 활용하여 지도로 한눈에 보기, 명소 검색
- 구글, 네이버, 페이스북 OAuth2 로그인
- Session DB를 활용하여, 로그인된 사용자 관리

**<사용 기술>**

- Spring Boot
- Spring Data JPA
- Spring Security
- QueryDSL
- MariaDB
- Oracle Server

## Requirements

---

- JDK 11 or later
- Gradle 4+
- You can also import the code straight into your IDE:
    - [Spring Tool Suite (STS)](https://spring.io/tools)
    - [IntelliJ IDEA](https://www.jetbrains.com/)

## ****Installation****

---

- Install as you would normally install a Java JDK 11
- If you use IntelliJ, you must install lombok plugin
    - Click *File > Settings > Plugin* or *Ctrl+Alt+S > Plugin*
    - Search **lombok** and Install
    

## Contributing

---

1. Create issues about the work.
2. Create a branch on the issue.
3. Commit, push to the created branch.
4. When the work is completed, request a pull request to main branch after rebaseing the main branch.
5. Review the code and merge it

### Branching

`ISSUE_NUMBER-description`

- e.g. Issue 5 related to  upload img
    
    `2-img-upload`
    

### Commit Message

Referred to [Beom Dev Log](https://beomseok95.tistory.com/328) and [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)

```
<type>[optional scope]: <description>
[optional body]
[optional footer(s)]
```

- Type
    - build, docs, feat, fix, perf, reactor, test
- Example
    
    `feat: allow provided config object to extend other configs`
    

## Maintainer

---

Current maintainers:

- SooChan Lee - [https://github.com/soochangoforit](https://github.com/soochangoforit)
