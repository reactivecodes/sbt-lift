logLevel := Level.Warn

resolvers ++= Seq(
  Resolver.url("SBS Releases", url("https://dev.sbsproperties.co.ke/repo/ivy-release"))(Resolver.ivyStylePatterns),
  Resolver.url("SBS Snapshots", url("https://dev.sbsproperties.co.ke/repo/ivy-snapshot"))(Resolver.ivyStylePatterns))

addSbtPlugin("ke.co.sbsproperties" % "sbt-sbs" % "1.0-SNAPSHOT+26b284a")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")

libraryDependencies <+= sbtVersion(sv => "org.scala-sbt" % "scripted-plugin" % sv)

resolvers += Resolver.sbtPluginRepo("snapshots")
