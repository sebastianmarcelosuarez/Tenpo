package com.example.demo;

import com.example.demo.request.SumRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

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

        assertTrue(result instanceof ResponseEntity);
        assertTrue(result.getStatusCode().is2xxSuccessful() );
        assertTrue(result.getBody() instanceof Integer );

        verify(spyController, times(1)).calculatePercentageValue(any());

    }
    @Test
    void test_suma_TimeoutException() throws Exception {
        SumRequest sumRequest = new SumRequest();
        sumRequest.setNumberOne(10);
        sumRequest.setNumberTwo(20);

        Controller spyController = spy(Controller.class);
        Mockito.doThrow(new TimeoutException()).when(spyController).calculatePercentageValue(any());

        ResponseEntity result = spyController.suma(sumRequest);

        assertTrue(result instanceof ResponseEntity);
        assertTrue(result.getStatusCode().is4xxClientError() );
        assertTrue(result.getBody().equals("Error Message due timeOut") );

        verify(spyController, times(1)).calculatePercentageValue(any());

    }

    @Test
    void test_suma_Exception() throws Exception {
        SumRequest sumRequest = new SumRequest();
        sumRequest.setNumberOne(10);
        sumRequest.setNumberTwo(20);

        Controller spyController = spy(Controller.class);
        Mockito.doThrow(new NullPointerException()).when(spyController).calculatePercentageValue(any());

        ResponseEntity result = spyController.suma(sumRequest);

        assertTrue(result instanceof ResponseEntity);
        assertTrue(result.getStatusCode().is4xxClientError() );
        assertTrue(result.getBody().equals("Generic Exception: null") );

        verify(spyController, times(1)).calculatePercentageValue(any());

    }

}