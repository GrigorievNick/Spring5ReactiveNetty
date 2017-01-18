name := "Spring5ReactiveNettyWithMetrics"

version := "1.0"

scalaVersion := "2.12.0"

resolvers += "Spring Milestones" at "https://repo.spring.io/libs-milestone"
resolvers += "Spring Release" at "https://repo.spring.io/libs-release"

libraryDependencies += "org.reactivestreams" % "reactive-streams" % "1.0.0"
libraryDependencies += "io.projectreactor" % "reactor-core" % "3.0.3.RELEASE"
libraryDependencies += "io.projectreactor.addons" % "reactor-test" % "3.0.3.RELEASE"
libraryDependencies += "io.projectreactor.ipc" % "reactor-netty" % "0.6.0.RELEASE"
libraryDependencies += "io.netty" % "netty-transport-native-epoll" % "4.1.6.Final"
libraryDependencies += "de.flapdoodle.embed" % "de.flapdoodle.embed.redis" % "1.11.4"
libraryDependencies += "org.springframework" % "spring-web-reactive" % "5.0.0.M4"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.2"
libraryDependencies += "biz.paluch.redis" % "lettuce" % "5.0.0.Beta1"
libraryDependencies += "log4j" % "log4j" % "1.2.17"
