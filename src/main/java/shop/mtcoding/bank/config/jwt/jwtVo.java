package shop.mtcoding.bank.config.jwt;

/*
SECRET 노출되면 안된다 (클라우드aws - 환경변수, 파일에 있는것을 읽거나
엑세스토큰이 만료 되면 리프레쉬 토큰으로 발급 JUNIT울위해 생략 예정
 */
public class jwtVo {


    public static final String SECRET = "메타코딩"; //h256 대칭키 //서버만 알아야 하는

    public static final int EXPIRATION_TIME = 1000*60*60*24*7; //만료시간 일주일

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String Header = "Authorization";
}
