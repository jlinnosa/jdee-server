/*
 * Copyright (C) 2002, 2003 by Nick Sieger
 * Copyright 2021 Jaakko Linnosaari
 *
 *
 * Author: Nick Sieger <nsieger@bitstream.net>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package jde.juci;

import java.util.*;
import java.io.*;

import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test the {@link LispWriter} class.
 *
 * @author <a href="mailto:nsieger@bitstream.net">Nick Sieger</a>
 */
public class LispWriterTest {

    private LispWriter lwriter;
    private StringWriter output;

    @Before
    public void setUp() {
        reset();
    }

    private void reset() {
        output = new StringWriter();
        lwriter = new LispWriter(new PrintWriter(output));
    }

    @Test
    public void testWriteString() {
        lwriter.writeString("\"abc\'\\\ndef\b\r\t\f");
        assertEquals("\"\\\"abc\\\'\\\\\\ndef\\b\\r\\t\\f\"", output.toString());
    }

    @Test
    public void testIntPrimitive() {
        lwriter.writeInt(1010);
        assertEquals("1010", output.toString());
        reset();
        lwriter.writeUnknown(1010);
        assertEquals("1010", output.toString());
    }

    @Test
    public void testIntObject() {
        lwriter.writeInt(new Integer(1010));
        assertEquals("1010", output.toString());
        reset();
        lwriter.writeUnknown(new Integer(1010));
        assertEquals("1010", output.toString());
    }

    @Test
    public void testLongPrimitive() {
        lwriter.writeLong(101020203030l);
        assertEquals("101020203030", output.toString());
        reset();
        lwriter.writeUnknown(101020203030l);
        assertEquals("101020203030", output.toString());
    }

    @Test
    public void testLongObject() {
        lwriter.writeLong(new Long(101020203030l));
        assertEquals("101020203030", output.toString());
        reset();
        lwriter.writeUnknown(new Long(101020203030l));
        assertEquals("101020203030", output.toString());
    }

    @Test
    public void testFloatPrimitive() {
        lwriter.writeFloat(10.1f);
        assertEquals("10.1", output.toString());
        reset();
        lwriter.writeUnknown(10.1f);
        assertEquals("10.1", output.toString());
    }

    @Test
    public void testFloatObject() {
        lwriter.writeFloat(new Float(10.1f));
        assertEquals("10.1", output.toString());
        reset();
        lwriter.writeUnknown(new Float(10.1f));
        assertEquals("10.1", output.toString());
    }

    @Test
    public void testDoublePrimitive() {
        lwriter.writeDouble(10.1d);
        assertEquals("10.1", output.toString());
        reset();
        lwriter.writeUnknown(10.1d);
        assertEquals("10.1", output.toString());
    }

    @Test
    public void testDoubleObject() {
        lwriter.writeDouble(new Double(10.1d));
        assertEquals("10.1", output.toString());
        reset();
        lwriter.writeUnknown(new Double(10.1d));
        assertEquals("10.1", output.toString());
    }

    @Test
    public void testCons1() {
        lwriter.writeCons(new Cons("", new Symbol("find-buffer-file-type-coding-system")));
        assertEquals("(\"\" . find-buffer-file-type-coding-system)", output.toString());
        reset();
        lwriter.writeUnknown(new Cons("", new Symbol("find-buffer-file-type-coding-system")));
        assertEquals("(\"\" . find-buffer-file-type-coding-system)", output.toString());
    }

    @Test
    public void testMapAlist() {
        Map<Object,Object> m = new LinkedHashMap<>();
        m.put("foo", new Symbol("bar"));
        m.put("baz", new Symbol("quux"));
        lwriter.writeAlist(m);
        assertEquals("'((\"foo\" . bar) (\"baz\" . quux))", output.toString());
        reset();
        lwriter.writeUnknown(m);
        assertEquals("'((\"foo\" . bar) (\"baz\" . quux))", output.toString());
    }

    @Test
    public void testWriteCharPrimitive() {
        lwriter.writeChar('a');
        lwriter.writeChar('\"');
        lwriter.writeChar('\'');
        lwriter.writeChar('\\');
        lwriter.writeChar('?');
        lwriter.writeChar(')');
        lwriter.writeChar('(');
        lwriter.writeChar(']');
        lwriter.writeChar('[');
        lwriter.writeChar('\n');
        lwriter.writeChar('\b');
        lwriter.writeChar('\r');
        lwriter.writeChar('\t');
        lwriter.writeChar('\f');
        assertEquals("?a?\\\"?\\\'?\\\\?\\??\\)?\\(?\\]?\\[?\\n?\\b?\\r?\\t?\\f",
                     output.toString());
    }

    @Test
    public void testWriteCharObject() {
        lwriter.writeChar(new Character('b'));
        lwriter.writeChar(new Character('a'));
        lwriter.writeChar(new Character('z'));
        lwriter.writeChar(new Character('\n'));
        lwriter.writeChar(new Character('\''));
        lwriter.writeChar(new Character('\t'));
        lwriter.writeUnknown(new Character('f'));
        assertEquals("?b?a?z?\\n?\\\'?\\t?f", output.toString());
    }

    @Test
    public void testBooleanPrimitive() {
        lwriter.writeBoolean(true);
        lwriter.writeBoolean(false);
        assertEquals("tnil", output.toString());
    }

    @Test
    public void testBooleanObject() {
        lwriter.writeBoolean(new Boolean(true));
        lwriter.writeBoolean(new Boolean(false));
        lwriter.writeUnknown(new Boolean(true));
        assertEquals("tnilt", output.toString());
    }

    @Test
    public void testWriteNull() {
        lwriter.writeUnknown(null);
        assertEquals("\'null", output.toString());
    }

    @Test
    public void testForm1() {
        List l = Arrays.asList(
                new Symbol("message"), "Hello %s", new Symbol("user-full-name"));
        lwriter.writeForm(l);
        assertEquals("(message \"Hello %s\" user-full-name)", output.toString());
        reset();
        lwriter.writeUnknown(l);
        assertEquals("'(message \"Hello %s\" user-full-name)", output.toString());
    }

    @Test
    public void testWriteUnknowWithACollection() {
        Collection<Object> c = new ArrayDeque();
        c.add(new Symbol("message"));
        c.add("Hello %s");
        c.add(new Symbol("user-full-name"));
        lwriter.writeUnknown(c);
        assertEquals("'(message \"Hello %s\" user-full-name)", output.toString());
    }

    @Test
    public void testQuoted1() {
        List l = new ArrayList();
        l.add(new Symbol("apply"));
        l.add(new Quoted(new Symbol("+")));
        l.add(1);
        l.add(2);
        List inner = new ArrayList();
        inner.add(3);
        inner.add(4);
        l.add(new Quoted(inner));
        lwriter.writeForm(l);
        assertEquals("(apply '+ 1 2 '(3 4))", output.toString());
        reset();
        lwriter.writeUnknown(l);
        assertEquals("'(apply '+ 1 2 '(3 4))", output.toString());
        reset();
        lwriter.setAutoQuoteLists(false);
        lwriter.writeUnknown(l);
        assertEquals("(apply '+ 1 2 '(3 4))", output.toString());
    }

    @Test
    public void testWriteJdeeJuciInvokeElispForm() {
        List eval = new ArrayList();
        eval.add(new Symbol("jdee-juci-invoke-elisp"));

        List form = new ArrayList();
        form.add(new Symbol("message"));
        form.addAll(Arrays.asList("hello %s", "nick"));

        eval.add(form);
        lwriter.writeForm(eval);
        assertEquals("(jdee-juci-invoke-elisp '(message \"hello %s\" \"nick\"))", output.toString());
    }

}
