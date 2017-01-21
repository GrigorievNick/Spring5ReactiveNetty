name := "Spring5ReactiveNettyWithMetrics"

version := "1.0"

scalaVersion := "2.12.0"

resolvers += "Spring Milestones" at "https://repo.spring.io/libs-milestone"

libraryDependencies += "org.reactivestreams" % "reactive-streams" % "1.0.0"
libraryDependencies += "io.projectreactor" % "reactor-core" % "3.0.4.RELEASE"
libraryDependencies += "io.projectreactor.addons" % "reactor-test" % "3.0.4.RELEASE"
libraryDependencies += "io.projectreactor.ipc" % "reactor-netty" % "0.6.0.RELEASE"
// https://mvnrepository.com/artifact/org.apache.commons/commons-pool2
libraryDependencies += "org.apache.commons" % "commons-pool2" % "2.4.2"
libraryDependencies += "io.netty" % "netty-transport-native-epoll" % "4.1.7.Final"
libraryDependencies += "org.springframework" % "spring-web-reactive" % "5.0.0.M4"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.2"
// https://mvnrepository.com/artifact/com.google.guava/guava
libraryDependencies += "com.google.guava" % "guava" % "21.0"
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.5.11"
libraryDependencies += "biz.paluch.redis" % "lettuce" % "5.0.0.Beta1" excludeAll(
  ExclusionRule("io.projectreactor"),
  ExclusionRule("io.netty")
  )

