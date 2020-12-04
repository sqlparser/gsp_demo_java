package common;

import org.junit.Test;
import junit.framework.TestCase;

/*
* Date: 2010-5-12
* Time: 15:38:21
*/
public class helloTest extends TestCase {

    /**
     * Constructor for BookTest.
     * @param name
     */
    public helloTest(String name) {
        super(name);
    }

    /**
     * setUp() method that initializes common objects
     */
    protected void setUp() throws Exception {
        super.setUp();
    }


    /**
     * tearDown() method that cleanup the common objects
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void testNothing() {
    }

   
    public void testWillAlwaysFail() {
       // fail("An error message");
    }

}