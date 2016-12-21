package models;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Thomas on 20-12-2016.
 */
public class RoleTest {

    /**
     * Test the admin role.
     */
    @Test
    public void getByIdTest0() {
        assertEquals(Role.getById(0), Role.Admin);
    }

    /**
     * Test the moderator role.
     */
    @Test
    public void getByIdTest1() {
        assertEquals(Role.getById(1), Role.Moderator);
    }

    /**
     * Test the user role.
     */
    @Test
    public void getByIdTest2() {
        assertEquals(Role.getById(2), Role.User);
    }

    /**
     * Test a non existent role.
     */
    @Test
    public void getByIdTestFalse() {
        assertEquals(Role.getById(3), null);
    }

}