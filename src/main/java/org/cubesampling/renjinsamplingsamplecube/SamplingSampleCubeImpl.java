package org.cubesampling.renjinsamplingsamplecube;

import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.StringVector;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javax.script.ScriptContext.ENGINE_SCOPE;

public class SamplingSampleCubeImpl implements Samplecube {

    /**
     * Maintain one instance of Renjin for each request thread.
     */
    private static final ThreadLocal<ScriptEngine> ENGINE = new ThreadLocal<ScriptEngine>();


    @Override
    public List<Double> cubeSampling(List<List<Double>> variableConstraints, List<Double> weightList, String order, String method) {
        ScriptEngine engine = ENGINE.get();
        if(engine == null) {
            engine = initEngine();
            ENGINE.set(engine);
        }
        Bindings bindings = engine.getBindings(ENGINE_SCOPE);
        bindings.put("constraints", variableConstraints);
        bindings.put("weight", weightList);
        bindings.put("comment", order);
        bindings.put("method", method);
        String[] sample;
        try {
            engine.eval("library(sampling)");
            engine.eval("library(jsonlite)");
            engine.eval("s <- samplecube(constraints,weight,order,comment=TRUE,method)");
            engine.eval("s <- toJson(s)");
            StringVector s = (StringVector) bindings.get("s");
            sample = s.toArray();
        } catch (ScriptException e) {
            throw new RuntimeException("Prediction failed");
        }
        return Arrays.stream(sample).map(e-> Double.parseDouble(e)).collect(Collectors.toList());
    }

    private ScriptEngine initEngine() {
            // Do one-time initialization
            ScriptEngine engine;
            engine = new RenjinScriptEngineFactory().getScriptEngine();
            return engine;
    }
}