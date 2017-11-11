package org.duangsuse.tree;

import org.junit.Test;

import bsh.Interpreter;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class tree_UnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void bsh_add_int() throws Exception {
        Object o = new Interpreter().eval("2 + 2");
        assertEquals(4, o);
    }
}