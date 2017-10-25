package org.mhealth.open.data.reader;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Created by dujijun on 2017/10/23.
 */
public class SFileReaderThread extends AbstractMThread {
    private File part;
    private boolean end = false;
    private Consumer<File> function;

    public SFileReaderThread(Consumer<File> function, File file){
        this.function = function;
        this.part = file;
    }

    public SFileReaderThread(CountDownLatch startupLatch, CountDownLatch completeLatch, CountDownLatch shutdownLatch, File part, Consumer<File> function) {
        super(startupLatch, completeLatch, shutdownLatch);
        this.part = part;
        this.function = function;
    }

    public SFileReaderThread(File part, Consumer<File> function) {
        this.part = part;
        this.function = function;
    }

    public SFileReaderThread(CountDownLatch startupLatch, File part, Consumer<File> function) {
        super(startupLatch);
        this.part = part;
        this.function = function;
    }

    public SFileReaderThread(CountDownLatch startupLatch, CountDownLatch completeLatch, File part, Consumer<File> function) {
        super(startupLatch, completeLatch);
        this.part = part;
        this.function = function;
    }

    @Override
    public void run() {
//        startupComplete();
        function.accept(part);
        shutdownComplete();

    }
}
