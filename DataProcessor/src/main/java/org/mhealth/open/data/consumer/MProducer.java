package org.mhealth.open.data.consumer;

import org.mhealth.open.data.reader.MRecord;

/**
 * for DataImport
 *
 * @author just on 2017/10/11.
 */

/**
 * 处理出队的数据
 */
public interface MProducer {
    /**
     * 发送数据到目标终端
     * @param record
     */
    void produce2Dest(MRecord record);

    /**
     * 关闭producer
     */
    void close();
}
