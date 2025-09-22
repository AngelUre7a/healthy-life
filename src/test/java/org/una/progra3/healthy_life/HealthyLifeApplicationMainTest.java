package org.una.progra3.healthy_life;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class HealthyLifeApplicationMainTest {

    @Test
    void main_shouldDelegateToSpringApplicationRun() {
        String[] args = new String[] {"--spring.profiles.active=test"};
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            HealthyLifeApplication.main(args);
            mocked.verify(() -> SpringApplication.run(HealthyLifeApplication.class, args), times(1));
        }
    }
}
