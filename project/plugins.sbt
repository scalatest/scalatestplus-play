resolvers ++= DefaultOptions.resolvers(snapshot = true)

resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.get("interplay.version").getOrElse("2.0.9"))
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % sys.props.getOrElse("play.version", "2.8.0-M5"))
addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.8.0-M5"))

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.3")

addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.6.0")