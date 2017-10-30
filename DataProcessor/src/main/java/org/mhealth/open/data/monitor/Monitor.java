package org.mhealth.open.data.monitor;

import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MThreadController;

/**
 * Created by dujijun on 2017/10/23.
 */
public abstract class Monitor extends MThreadController {
    public abstract void startMonitor(MDataReader dataReader);
}
