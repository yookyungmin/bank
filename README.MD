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