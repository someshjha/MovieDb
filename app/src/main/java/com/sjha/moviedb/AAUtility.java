package com.sjha.moviedb;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

public class AAUtility {

    public static <T extends Model> T get(Class<? extends Model> clazz, String whereClause, String whereValue) {

        return new Select()
                .from(clazz)
                .where(whereClause, whereValue)
                .executeSingle();

    }

    public static <T extends Model> T get(Class<? extends Model> clazz) {

        return new Select()
                .from(clazz)
                .executeSingle();

    }

    public static <T extends Model> List<T> getList(Class<? extends Model> clazz, String whereClause, Object whereValue, String orderBy) {

        return new Select()
                .from(clazz)
                .where(whereClause, whereValue)
                .orderBy(orderBy)
                .execute();

    }

    public static <T extends Model> List<T> getList(Class<? extends Model> clazz, String orderBy) {

        return new Select()
                .from(clazz)
                .orderBy(orderBy)
                .execute();

    }

    public static <T extends Model> List<T> getList(Class<? extends Model> clazz) {
        return new Select()
                .from(clazz)
                .execute();

    }

    public static <T extends Model> void deleteList(Class<? extends Model> clazz) {
        new Delete()
                .from(clazz)
                .execute();

    }

}
