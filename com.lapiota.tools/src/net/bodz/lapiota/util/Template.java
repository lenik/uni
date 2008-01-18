package net.bodz.lapiota.util;

import java.util.Map;

import net.sf.freejava.util.PatternProcessor;

public class Template extends PatternProcessor {

    private String    template;
    private Map<?, ?> vars;

    public Template(String template, Map<?, ?> vars) {
        super("\\$(\\w+)");
        assert template != null : "Null template";
        assert vars != null : "Null vars";
        this.template = template;
        this.vars = vars;
    }

    @Override
    protected void matched(int start, int end) {
        String var = matcher.group(1);
        if (vars.containsKey(var)) {
            Object content = vars.get(var);
            buffer.append(content);
        } else {
            super.matched(start, end);
        }
    }

    public synchronized String generate() {
        String instance = super.process(template);
        return instance;
    }

}
