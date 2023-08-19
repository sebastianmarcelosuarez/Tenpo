package com.example.demo;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    class TestClass {
        private int privateMethodName(int number) {
            System.out.println(number + 10);
            return number + 10;
        }
    }

    @Test
    void testPrivateMethod() throws Exception {
        TestClass TestClass = new TestClass();
        Method privateMethod = TestClass.class.getDeclaredMethod("privateMethodName", int.class);
        privateMethod.setAccessible(true);
        int result = (int) privateMethod.invoke(TestClass, 10);
        assertEquals(20, result);
    }

    @Test
    void test_getPercentageServiceMocked() throws Exception {
        Controller controllerClass = new Controller();
        Method privateMethod = Controller.class.getDeclaredMethod("getPercentageServiceMocked");
        privateMethod.setAccessible(true);
        Future<Integer> futureResult = (Future<Integer>) privateMethod.invoke(controllerClass);
        Integer result = futureResult.get();
        assertTrue(result >0 && result <101);
    }

}