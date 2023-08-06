# Junit Bank App

### Jpa LocalDateTime 자동으로 생성하는법
-@EnableJpaAuditing (main 클래스)
-@EntityListeners(AuditingEntityListener.class) (Entiti클래스)

'''java

    @CreatedDate //insert
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false) //update, insert
    private LocalDateTime updatedAt;

'''

http://localhost:8083/api/s/account
    계좌 등록시 
Account 엔티티의 User 는 Lazy 되어 있기 때문에 select 안될것이다. 추후 계좌 소유 확인시 쿼리가 두번 나갈 예정이기에(n+1문제) 수정 예정

## 통합테스트 기본 어노테이션 세팅
```java
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
```

## 레포지토리 기본 어노테이션 세팅
```java
@ActiveProfiles("test")
@DataJpaTest
```
> 만약에 QueryDSL 빈이 필요하다면 추가하기 @Import(QueryDSLConfig.class)

## 서비스 기본 어노테이션 세팅
```java
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
```

## Junit 테스트시에 주의할 점
> Lombok 어노테이션 사용을 하지 않는다. Lombok이 compileOnly이기 때문에 runtime시에 작동 안한다.

## 통합테스트 MockUser 주입하는 법
> @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)

> default로 TestExecutionListener.beforeTestMethod로 설정되어 있음 @BeforeAll, @BeforeEach 실행전에 WithUserDetails가 실행되어서, DB에 User가 생기기전에 실행됨

## Security와 JWT를 함께 사용시 주의할 점
> JWT 인증, 인가 테스트를 따로 한다.

> 통합 테스트에서 인증체크는 세션값을 확인하면 된다.

## Security 설정 최신
```txt
AuthenticationManager 의존이 무한으로 의존하는 이슈가 있었다.
그래서 시큐리티 설정은 이제 @Configuration 클래스안에 @Bean으로 설정한다.
그리고 필터 설정은 전부 내부 클래스를 만들어서 AuthenticationManager를 주입받아서 필터를 설정한다.
```

## 서비스 테스트시에 주의할 점
> stub 실행시점은 service 메서드 동작시점이기 때문에, read일 때는 stub이 한개만 있어도 되지만, write일 때는 stub을 단계별로 만들고 깊은 복사를 해야 한다.

> DB에 영속화된 값이 아닌 더미데이터로 테스트하는 것이기 때문에 양방향 매핑시에는 양쪽으로 객체를 동기화 시켜줘야 한다.

## 서비스 테스트시에 참고할 어노테이션
```java
/*
 * Mock -> 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito환경에 주입함.
 * InjectMocks -> Mock된 가짜 객체를 진짜 객체 UserService를 만들어서 주입함
 * MockBean -> Mock객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
 * Spy -> 진짜 객체를 만들어서 Mockito환경에 주입함.
 * SpyBean -> Spay객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
 */
```