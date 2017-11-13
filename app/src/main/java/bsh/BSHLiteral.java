package bsh;

class BSHLiteral extends SimpleNode {
    public Object value;

    BSHLiteral(int id) {
        super(id);
    }

    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        if (value == null) throw new InterpreterError("bsh字面中的空值: " + value);

        return value;
    }

    private char getEscapeChar(char ch) {
        switch (ch) {
            case 'b':
                ch = '\b';
                break;

            case 't':
                ch = '\t';
                break;

            case 'n':
                ch = '\n';
                break;

            case 'f':
                ch = '\f';
                break;

            case 'r':
                ch = '\r';
                break;

                // do nothing - ch already contains correct character
            case '"':
            case '\'':
            case '\\':
                break;
        }

        return ch;
    }

    public void charSetup(String str) {
        char ch = str.charAt(0);
        if (ch == '\\') {
            // get next character
            ch = str.charAt(1);

            if (Character.isDigit(ch)) ch = (char) Integer.parseInt(str.substring(1), 8);
            else ch = getEscapeChar(ch);
        }

        value = new Primitive(Character.valueOf(ch).charValue());
    }

    void stringSetup(String str) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                // get next character
                ch = str.charAt(++i);

                if (Character.isDigit(ch)) {
                    int endPos = i;

                    // check the next two characters
                    while (endPos < i + 2) {
                        if (Character.isDigit(str.charAt(endPos + 1))) endPos++;
                        else break;
                    }

                    ch = (char) Integer.parseInt(str.substring(i, endPos + 1), 8);
                    i = endPos;
                } else ch = getEscapeChar(ch);
            }

            buffer.append(ch);
        }

        value = buffer.toString().intern();
    }
}
