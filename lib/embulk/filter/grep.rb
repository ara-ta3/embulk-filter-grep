Embulk::JavaPlugin.register_filter(
  "grep", "org.embulk.filter.GrepFilterPlugin",
  File.expand_path('../../../../classpath', __FILE__))
