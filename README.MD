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

7/11