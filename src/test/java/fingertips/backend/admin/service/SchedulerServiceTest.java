//package fingertips.backend.admin.service;
//
//import fingertips.backend.admin.mapper.AdminMapper;
//import fingertips.backend.config.RootConfig;
//import fingertips.backend.config.ServletConfig;
//import fingertips.backend.config.WebConfig;
//import fingertips.backend.security.config.SecurityConfig;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import static org.mockito.Mockito.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)  // JUnit 4에서 사용하는 애너테이션
//@ContextConfiguration(classes = {RootConfig.class, WebConfig.class, ServletConfig.class, SecurityConfig.class})
//public class SchedulerServiceTest {
//
//    @InjectMocks
//    private SchedulerService schedulerService;  // 실제 테스트할 스케줄러 서비스
//
//    @Mock
//    private AdminMapper adminMapper;  // Mock으로 주입할 AdminMapper
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);  // Mockito 초기화
//    }
//
//    @Test
//    public void testUpdateDailyMetrics() {
//        // 가상의 반환값 설정 (테스트 데이터)
//        when(adminMapper.getTodaySignUpCount()).thenReturn(5);
//        when(adminMapper.getTodayLoginCount()).thenReturn(10);
//        when(adminMapper.getTodayVisitCount()).thenReturn(15);
//        when(adminMapper.getTodayWithdrawalCount()).thenReturn(2);
//        when(adminMapper.getTodayTestLinkVisitCount()).thenReturn(8);
//        when(adminMapper.getTodayTestResultClickCount()).thenReturn(12);
//        when(adminMapper.getTodayTestSignUpCount()).thenReturn(3);
//
//        when(adminMapper.getCumulativeSignUpCount()).thenReturn(100);
//        when(adminMapper.getCumulativeLoginCount()).thenReturn(200);
//        when(adminMapper.getCumulativeVisitCount()).thenReturn(300);
//        when(adminMapper.getCumulativeWithdrawalCount()).thenReturn(20);
//
//        // 스케줄러 메서드 실행
//        schedulerService.updateDailyMetrics();
//
//        // adminMapper 메서드가 호출되었는지 검증
//        verify(adminMapper).getTodaySignUpCount();
//        verify(adminMapper).getTodayLoginCount();
//        verify(adminMapper).getTodayVisitCount();
//        verify(adminMapper).getTodayWithdrawalCount();
//        verify(adminMapper).getTodayTestLinkVisitCount();
//        verify(adminMapper).getTodayTestResultClickCount();
//        verify(adminMapper).getTodayTestSignUpCount();
//
//        verify(adminMapper).getCumulativeSignUpCount();
//        verify(adminMapper).getCumulativeLoginCount();
//        verify(adminMapper).getCumulativeVisitCount();
//        verify(adminMapper).getCumulativeWithdrawalCount();
//
//        // adminMapper.updateUserMetrics()가 올바른 값으로 호출되었는지 검증
//        verify(adminMapper).updateUserMetrics(
//                5, 10, 15, 2, // 오늘의 통계
//                100, 200, 300, 20, // 누적 통계
//                8, 12, 3  // 테스트 관련 통계
//        );
//    }
//}
