package org.mhealth.open.data.consumer;

import org.mhealth.open.data.record.SRecord;

/**
 * Created by 11245 on 2017/10/26.
 */
public interface SProducer {
    /**
     * 发送数据到目标终端
     * @param record
     */
    void produce2Dest(SRecord record);

    /**
     * 关闭producer
     */
    void close();
}
