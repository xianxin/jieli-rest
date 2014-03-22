package com.jieli.mongo;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 * User: liming_liu
 * Date: 14-3-8
 * Time: 下午8:49
 * To change this template use File | Settings | File Templates.
 */
public class GenericDAO<T extends Model> {
    protected final MongoCollection col;

    public GenericDAO() {
        this.col = JongoClient.getInstance().getCollection(StringUtils.lowerCase(getType().getSimpleName()));
    }

    public T loadById(String id) {
        return col.findOne(new ObjectId(id)).as(getType());
    }

    public Iterable<T> find(String query, Object... params) {
        return col.find(query, params).as(getType());
    }

    public T save(T t) {
        col.save(t);
        return t;
    }

    public T update(T t) {
        col.update(t.objectId).merge(t);
        return t;
    }

    public void deleteById(String id) {
        col.remove(new ObjectId(id));
    }

    //慎用，测试使用
    public void clear() {
        col.remove();
    }

    private Class<T> getType(){

        return getSuperClassGenericType(getClass());

        // @xianxing , 老是报错 ： java.lang.ClassCastException: java.lang.Class cannot be cast to java.lang.reflect.ParameterizedType
        //ParameterizedType type = (ParameterizedType)this.getClass().getGenericSuperclass();
        //return (Class) type.getActualTypeArguments()[0];
    }

    public static Class getSuperClassGenericType(Class c) {
        Type genType = c.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType))
            return Object.class;
        Type [] params = ((ParameterizedType)genType).getActualTypeArguments();
        if (!(params[0] instanceof  Class))
            return Object.class;
        return (Class)params[0];
    }

    public MongoCollection getCollection(){
        return col;
    }
}
