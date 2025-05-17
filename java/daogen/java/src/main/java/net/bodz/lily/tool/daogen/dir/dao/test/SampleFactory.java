package net.bodz.lily.tool.daogen.dir.dao.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.bodz.bas.c.type.TypePoMap;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.meta.decl.NotNull;
import net.bodz.bas.rtx.IMutableOptions;
import net.bodz.bas.rtx.MapOptions;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.typer.Typers;
import net.bodz.bas.typer.std.ISampleGenerator;
import net.bodz.lily.tool.daogen.util.IOptionsSetter;

public abstract class SampleFactory
        extends RandomBased {

    // overrides. use system Typer by default.
    TypePoMap<ISampleGenerator<?>> typeMap = new TypePoMap<>();
    Map<Integer, ISampleGenerator<?>> sqlTypeMap = new HashMap<>();
    Map<String, ISampleGenerator<?>> sqlTypeNameMap = new HashMap<>();

    TypePoMap<IOptionsSetter> typeOptions = new TypePoMap<>();
    Map<Integer, IOptionsSetter> sqlTypeOptions = new HashMap<>();
    Map<String, IOptionsSetter> sqlTypeNameOptions = new HashMap<>();

    public SampleFactory(long... seeds) {
        super(seeds);
    }

    public void setOptions(@NotNull Class<?> type, @NotNull IOptionsSetter fn) {
        typeOptions.put(type, fn);
    }

    public void setOptions(int sqlType, @NotNull IOptionsSetter fn) {
        sqlTypeOptions.put(sqlType, fn);
    }

    public void setOptions(@NotNull String sqlTypeName, @NotNull IOptionsSetter fn) {
        sqlTypeNameOptions.put(sqlTypeName, fn);
    }

    public void define(@NotNull Class<?> type, @NotNull ISampleGenerator<?> fn) {
        typeMap.put(type, fn);
    }

    public void define(int sqlType, @NotNull ISampleGenerator<?> fn) {
        sqlTypeMap.put(sqlType, fn);
    }

    public void define(@NotNull String sqlTypeName, @NotNull ISampleGenerator<?> fn) {
        sqlTypeNameMap.put(sqlTypeName, fn);
    }

    public void undefine(@NotNull Class<?> type) {
        typeMap.remove(type);
    }

    public void undefine(int sqlType) {
        sqlTypeMap.remove(sqlType);
    }

    public void undefine(@NotNull String sqlTypeName) {
        sqlTypeNameMap.remove(sqlTypeName);
    }

    public <T> ISampleGenerator<T> getGenerator(Class<T> type) {
        @SuppressWarnings("unchecked")
        ISampleGenerator<T> generator = (ISampleGenerator<T>) typeMap.get(type);
        if (generator == null) {
            generator = Typers.getGenericTyper(type, ISampleGenerator.class);
        }
        return generator;
    }

    public <T> ISampleGenerator<T> getGenerator(int sqlType) {
        return getGenerator(sqlType, false);
    }

    public <T> ISampleGenerator<T> getGenerator(int sqlType, boolean orType) {
        @SuppressWarnings("unchecked")
        ISampleGenerator<T> generator = (ISampleGenerator<T>) sqlTypeMap.get(sqlType);
//        if (generator == null && orType) {
//            SqlTypeEnum sqlTypeEnum = SqlTypeEnum.forSQLType(sqlType);
//            if (sqlTypeEnum != null)
//                sqlTypeEnum.getPreferredType();
//        }
        return generator;
    }

    public <T> ISampleGenerator<T> getGenerator(@NotNull String sqlTypeName) {
        return getGenerator(sqlTypeName, false);
    }

    public <T> ISampleGenerator<T> getGenerator(@NotNull String sqlTypeName, boolean orType) {
        @SuppressWarnings("unchecked")
        ISampleGenerator<T> generator = (ISampleGenerator<T>) sqlTypeNameMap.get(sqlTypeName);
//        if (generator == null && orType) {
//            SqlTypeEnum sqlTypeEnum = SqlTypeEnum.forSQLTypeName(sqlTypeName);
//            if (sqlTypeEnum != null)
//                sqlTypeEnum.getPreferredType();
//        }
        return generator;
    }

    public ISampleGenerator<?> getGenerator(@NotNull IColumnMetadata column) {
        Class<?> type = column.getJavaClass();
        ISampleGenerator<?> generator = getGenerator(type);
        if (generator != null)
            return generator;

        String sqlTypeName = column.getSqlTypeName();
        generator = getGenerator(sqlTypeName);
        if (generator != null)
            return generator;

        int sqlType = column.getSqlType();
        generator = getGenerator(sqlType);
        if (generator != null)
            return generator;

        return null;
    }

    @NotNull
    public IMutableOptions newOptions(@NotNull IColumnMetadata column) {
        MapOptions options = new MapOptions();
        Class<?> type = column.getJavaClass();
        IOptionsSetter setter = typeOptions.meet(type);
        if (setter == null) {
            String sqlTypeName = column.getSqlTypeName();
            setter = sqlTypeNameOptions.get(sqlTypeName);
            if (setter == null) {
                int sqlType = column.getSqlType();
                setter = sqlTypeOptions.get(sqlType);
            }
        }
        if (setter != null)
            setter.setOptions(options, column);
        return options;
    }

    public Object newSample(@NotNull IColumnMetadata column) {
        IMutableOptions options = newOptions(column);
        return newSample(column, options);
    }

    public Object newSample(@NotNull IColumnMetadata column, IMutableOptions options) {
        if (column.isNullable()) {
            Random prng = options.get(Random.class, random);
            if (prng.nextInt(100) < 30)
                return null;
        }

        ISampleGenerator<?> generator = getGenerator(column);
        if (generator == null) {
            String message = String.format("no available sample generator for column %s.%s (type %s, sqlType %s, sqlTypeName %s)",//
                    column.getTable().getName(), column.getName(),//
                    column.getJavaClass(), column.getSqlTypeEnum(), column.getSqlTypeName());
            throw new IllegalUsageException(message);
        }

        Object sample = generator.newSample(options);
        return sample;
    }

    @NotNull
    public IMutableOptions newOptions(@NotNull Class<?> type) {
        MapOptions options = new MapOptions();
        IOptionsSetter setter = typeOptions.meet(type);
        if (setter != null)
            setter.setOptions(options, null);
        return options;
    }

    @NotNull
    public IMutableOptions newOptions(int sqlType) {
        MapOptions options = new MapOptions();
        IOptionsSetter setter = sqlTypeOptions.get(sqlType);
        if (setter != null)
            setter.setOptions(options, null);
        return options;
    }

    @NotNull
    public IMutableOptions newOptions(@NotNull String sqlTypeName) {
        MapOptions options = new MapOptions();
        IOptionsSetter setter = sqlTypeNameOptions.get(sqlTypeName);
        if (setter != null)
            setter.setOptions(options, null);
        return options;
    }

    public <T> T newSample(@NotNull Class<T> type) {
        return newSample(type, newOptions(type));
    }

    public <T> T newSample(@NotNull Class<T> type, @NotNull IMutableOptions options) {
        @SuppressWarnings("unchecked")
        ISampleGenerator<T> sampleGenerator = (ISampleGenerator<T>) typeMap.get(type);
        if (sampleGenerator == null) {
            sampleGenerator = Typers.getGenericTyper(type, ISampleGenerator.class);
            if (sampleGenerator == null)
                return null;
        }
        return sampleGenerator.newSample(options);
    }

    public Object newSample(int sqlType) {
        return newSample(sqlType, newOptions(sqlType));
    }

    public Object newSample(int sqlType, @NotNull IMutableOptions options) {
        ISampleGenerator<?> sampleGenerator = (ISampleGenerator<?>) sqlTypeMap.get(sqlType);
        if (sampleGenerator == null)
            return null;
        return sampleGenerator.newSample(options);
    }

    public Object newSample(@NotNull String sqlTypeName) {
        return newSample(sqlTypeName, newOptions(sqlTypeName));
    }

    public Object newSample(@NotNull String sqlTypeName, @NotNull IMutableOptions options) {
        ISampleGenerator<?> sampleGenerator = (ISampleGenerator<?>) sqlTypeNameMap.get(sqlTypeName);
        if (sampleGenerator == null)
            return null;
        return sampleGenerator.newSample(options);
    }

}
