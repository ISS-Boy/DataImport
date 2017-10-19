package org.mhealth.open.data.reader;

/**
 * Created by dujijun on 2017/10/6.
 */
public class MDataReaderFactory<T extends MDataReader> {
    public T getReader(Class<T> dataReader) {
        T reader = null;
        try {
            reader = dataReader.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return reader;
    }
}
