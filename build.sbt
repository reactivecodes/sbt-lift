import sbt._
import org.scalastyle.sbt.{ScalastylePlugin, PluginKeys => StylePluginKeys}

lazy val sbtLift = project.in(file(".")).sbsSbtPluginSettings


scriptedSettings

ScalastylePlugin.Settings

name := "SBT Lift"

version := "0.1.0-SNAPSHOT"

sbsOss := true

licenses += ("Apache Software License, Version 2.0",
  url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/sbsproperties/sbt-lift"))

startYear := Some(2014)

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.9.0")

scriptedRun <<= scriptedRun.dependsOn(publishLocal)

scriptedLaunchOpts <+= version { "-Dproject.version=" + _ }

StylePluginKeys.config := file("project/scalastyle-config.xml")
