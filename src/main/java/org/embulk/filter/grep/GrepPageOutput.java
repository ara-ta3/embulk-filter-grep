package org.embulk.filter.grep;

import org.embulk.filter.GrepFilterPlugin;
import org.embulk.spi.*;

import java.awt.print.*;


public class GrepPageOutput implements PageOutput {

    private final Schema schema;
    private final GrepFilterPlugin.PluginTask task;
    private final PageOutput output;
    private final PageReader pageReader;
    private final PageOutput newOutput;
    private final PageBuilder builder;

    public GrepPageOutput(
            GrepFilterPlugin.PluginTask task,
            Schema schema,
            PageOutput output) {
        this.task = task;
        this.schema = schema;
        this.output = output;
        pageReader = new PageReader(schema);
        newOutput = new AddOnlyPageOutput(output);
        builder = new PageBuilder(new BufferAllocator() {
            @Override
            public Buffer allocate() {
                return null;
            }

            @Override
            public Buffer allocate(int minimumCapacity) {
                return null;
            }
        }, schema, newOutput);
    }

    @Override
    public void add(Page page) {
        pageReader.setPage(page);
        while(pageReader.nextRecord()) {
            builder.addRecord();
        }
        builder.flush();
    }

    @Override
    public void finish() {
        this.output.finish();
        builder.finish();
    }

    @Override
    public void close() {
        this.output.close();
        pageReader.close();
    }

    static class AddOnlyPageOutput implements PageOutput {
        protected final PageOutput output;

        public AddOnlyPageOutput(PageOutput outptut) {
            this.output = outptut;
        }

        @Override
        public void add(Page page) {
            output.add(page);
        }

        @Override
        public void finish() {
            output.finish();
        }

        @Override
        public void close() {
            output.close();
        }
    }

}
