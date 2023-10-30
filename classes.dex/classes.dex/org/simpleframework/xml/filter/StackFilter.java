package org.simpleframework.xml.filter;

import java.util.Stack;

public class StackFilter implements Filter {
    private Stack<Filter> stack = new Stack();

    public void push(Filter filter) {
        this.stack.push(filter);
    }

    public String replace(String text) {
        String value;
        int i = this.stack.size();
        do {
            i--;
            if (i < 0) {
                return null;
            }
            value = ((Filter) this.stack.get(i)).replace(text);
        } while (value == null);
        return value;
    }
}
