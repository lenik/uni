package net.bodz.lapiota.eclipse.jdt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.bodz.bas.traits.IParser;
import net.bodz.bas.traits.IValidator;

/**
 * [OPTION] [--] FILES
 * <p>
 * OPTION: -NAME PARAM or --NAME=PARAM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
public @interface TestAnnotation {

    boolean _final() default true;

    String name() default "";

    String[] alias() default {};

    String vnam() default "";

    String doc() default "";

    boolean hidden() default false;

    boolean required() default false;

    String optional() default "";

    /**
     * when valtype is specified, the value must be collection type and the parser and check are
     * applied for each item in the collection.
     */
    Class<?> valtype() default void.class;

    Class<? extends IParser<?>> parser() default IParser.class;

    String parserinfo() default "";

    Class<? extends IValidator<?>> check() default IValidator.class;

    String checkinfo() default "";

    /**
     * Get option also from FILES by index. for collection/map types, this is the starting index.
     */
    int fileIndex() default -1;

    /**
     * for method/callback only
     */
    Class<? extends IParser>[] want() default {};

}
