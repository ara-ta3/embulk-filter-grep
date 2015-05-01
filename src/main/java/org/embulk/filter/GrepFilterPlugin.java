package org.embulk.filter;

import org.embulk.config.*;
import org.embulk.filter.grep.GrepPageOutput;
import org.embulk.spi.*;

public class GrepFilterPlugin
        implements FilterPlugin
{
    public interface PluginTask
            extends Task
    {
        @Config("property1")
        public String getProperty1();

        @Config("property2")
        @ConfigDefault("0")
        public int getProperty2();

        @ConfigInject
        public BufferAllocator getBufferAllocator();
    }

    @Override
    public void transaction(ConfigSource config, Schema inputSchema,
            FilterPlugin.Control control)
    {
        PluginTask task = config.loadConfig(PluginTask.class);

        Schema outputSchema = inputSchema;

        control.run(task.dump(), outputSchema);
    }

    @Override
    public PageOutput open(TaskSource taskSource, Schema inputSchema,
            Schema outputSchema, PageOutput output)
    {
        PluginTask task = taskSource.loadTask(PluginTask.class);

        return new GrepPageOutput(task, inputSchema, output);
    }

}
