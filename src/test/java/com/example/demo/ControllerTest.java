package com.example.demo;

import com.example.demo.request.SumRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@PrepareForTest(Controller.class)
class ControllerTest {


    //Testing private method via reflection
    @Test
    void test_getPercentageServiceMocked() throws Exception {

        Controller controllerClass = new Controller();

        Method privateMethod = Controller.class.getDeclaredMethod("getPercentageServiceMocked");
        privateMethod.setAccessible(true);
        Future<Integer> futureResult = (Future<Integer>) privateMethod.invoke(controllerClass);
        Integer result = futureResult.get();
        assertTrue(result >0 && result <101);
    }


    @Test
    void test_suma() throws Exception {
        SumRequest sumRequest = new SumRequest();
        sumRequest.setNumberOne(10);
        sumRequest.setNumberTwo(20);

        Controller spyController = spy(Controller.class);
        Mockito.doReturn(10).when(spyController).calculatePercentageValue(any());

        ResponseEntity result = spyController.suma(sumRequest);

        verify(spyController, times(1)).calculatePercentageValue(any());

    }

}