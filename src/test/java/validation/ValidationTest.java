package validation;

import org.junit.Test;

import static org.junit.Assert.*;
import static validation.Validation.authenticateDevice;
import static validation.Validation.createToken;

/**
 * Created by Thomas on 18-12-2016.
 */
public class ValidationTest {

    @Test
    public void authenticateDeviceTestTrue(){
        //assertTrue(authenticateDevice("123", "true"));
    }

    @Test
    public void authenticateDeviceTestFalse(){
        //assertFalse(authenticateDevice("123", "false"));
    }

    @Test
    public void createTokenTest(){
        //createToken("2");
    }
}