# TravelWeb



## Contents

- ****Introduction****
- ****Requriements****
- ****Installation****
- ****Contributing****
- ****Maintainer****


## Introduction



- 2022년도 3학년 1학기 Web Server Project, SSR으로 개발하였습니다.
- 전국 맛집, 부산 관광 명소 공유하는 웹 사이트

- **부산 관광 명소**
    
    ![a](https://user-images.githubusercontent.com/91618389/186837014-240fb878-2dd2-4fc5-8af6-20e5bc25054e.png)
    

- **전국 맛집 게시글**
    
    ![b](https://user-images.githubusercontent.com/91618389/186837012-7c573808-3622-4f4f-8fee-45ccc6e23912.png)
    
- **맛집 게시글 상세 페이지**
    
    ![c](https://user-images.githubusercontent.com/91618389/186837008-d93b387d-6e82-4226-9344-c354cbb26f4c.png)
    

- **부산 관광 명소 한눈에 보기**
    
    ![d](https://user-images.githubusercontent.com/91618389/186837001-87cb11da-e821-4002-a5d7-b36e336560b6.png)
    

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



- JDK 11 or later
- Gradle 4+
- You can also import the code straight into your IDE:
    - [Spring Tool Suite (STS)](https://spring.io/tools)
    - [IntelliJ IDEA](https://www.jetbrains.com/)


## ****Installation****



- Install as you would normally install a Java JDK 11
- If you use IntelliJ, you must install lombok plugin
    - Click *File > Settings > Plugin* or *Ctrl+Alt+S > Plugin*
    - Search **lombok** and Install
    


## Contributing



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



Current maintainers:

- SooChan Lee - [https://github.com/soochangoforit](https://github.com/soochangoforit)
