# Lepus's scripted tests

This is a collection of sbt scripted tests for Lepus's sbt plugin.

They are all in the sbt-plugin directory so we can use scripted's "sbt-plugin/*1of3" feature to auto-split the tests into groups and run them in parallel build jobs.

## Use a SNAPSHOT version

If you’re not already doing so, update your plugin version in the build.sbt file to use a -SNAPSHOT suffix. When the scripted plugin runs, it will locally install your plugin. By using a snapshot version, you prevent version clashes when you do finally publish your plugin; such as where your version X locally is different to your published version X.

## Run the Script

Run the scripted plugin through SBT to run your test script with:

```console
$ scripted
```

As far as I can tell scripted does the following:

1. Installs your plugin SNAPSHOT to your local ivy cache.
2. For each src/sbt-test/<test group>/<test project> directory, copies the content to a temporary directory.
3. Runs your test script in the temporary directory.

Here is the sample structure of the sbt-test directory.

```
src
└── sbt-test
    └── sbt-plugin
        ├── sbt-project
        │   ├── build.sbt
        │   ├── project
        │   │   └── plugins.sbt
        │   └── test
        ├── sbt-project
        │   ├── build.sbt
        │   ├── project
        │   │   └── plugins.sbt
        │   └── test
        └── sbt-project
            ├── build.sbt
            ├── project
            │   └── plugins.sbt
            └── test
```
