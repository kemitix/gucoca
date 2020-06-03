# gucoca
Posts stories from the archives at random to Twitter for Cossmass Infinities

## Dependencies

![Reactor Module Dependencies](./docs/images/reactor-graph.png)

## Organisation

Using JSR 352, with Spring Boot as the supporting framework.

Using Spring only within the main `gucoca` module.

The `app` module defines the job in `META-INF/batch-jobs/gucoca.xml`.

Other modules will implement components of the job and declare them within their `META-INF/batch.xml` file.
