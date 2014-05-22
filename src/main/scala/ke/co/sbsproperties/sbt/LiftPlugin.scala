/******************************************************************************
 * Copyright 2014 the original Author/Authors.                                *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 ******************************************************************************/

package ke.co.sbsproperties.sbt

import sbt._
import Keys._
import com.earldouglas.xsbtwebplugin.WarPlugin
import sbt.plugins.IvyPlugin
import sbt.impl.GroupArtifactID


object LiftPlugin extends AutoPlugin {

  override val requires: Plugins = IvyPlugin

  override val trigger: PluginTrigger = noTrigger

  override lazy val projectSettings: Seq[Def.Setting[_]] = defaultProjectSettings

  sealed trait Import {

    val LiftLibrary = LiftPlugin.LiftLibrary
    type LiftLibrary = LiftPlugin.LiftLibrary

    // Keys
    val liftOrganisation = settingKey[String]("Organisation/groupID of the Lift Framework used in this project. Default"
      + " value is 'net.liftweb'. This is an advanced setting for use if using a custom Lift Framework build.")

    val liftVersion = settingKey[String]("Revision/Version of the Lift Web Framework used in this project.")

    val liftDependencies = settingKey[Seq[LiftLibrary]]("")


    // Lift Core Libraries
    val liftActor = LiftLibrary("actor")
    val liftCommon = LiftLibrary("common")
    val liftJson = LiftLibrary("json")
    val liftJsonExt = LiftLibrary("json-ext")
    val liftJsonScalaz7 = LiftLibrary("json-scalaz7")
    val liftMarkdown = LiftLibrary("markdown")
    val liftUtil = LiftLibrary("util")

    // Lift Persistence Libraries
    val liftDB = LiftLibrary("db")
    val liftMapper = LiftLibrary("mapper")
    val liftMongoDB = LiftLibrary("mongodb")
    val liftMongoRecord = LiftLibrary("mongodb-record")
    val liftProto = LiftLibrary("proto")
    val liftRecord = LiftLibrary("record")
    val liftSquerylRecord = LiftLibrary("squeryl-record")

    // Lift Web Libraries
    val liftTestkit = LiftLibrary("testkit", Some("test"))
    val liftWebkit = LiftLibrary("webkit")

    // External Libraries (Provided Scope)
    val javaxServletApi: ModuleID =
      "org.jboss.spec.javax.servlet" % "jboss-servlet-api_3.0_spec" % "1.0.2.Final" % "provided"

    implicit final class LiftProjectSyntax(val p: Project) {

      def enableLift: Project = p.enablePlugins(LiftPlugin)

      def dependsOn(libs: LiftLibrary*): Project = enableLift.settings(addLiftDependency(libs: _*))

      def liftWarSettings: Project = enableLift.settings(liftWarProjectSettings: _*)
    }
  }

  val autoImport = new Import {}
  import autoImport._

  case class LiftLibrary(lib: String, config: Option[String] = None)

  def liftModuleID(organisation: String, version: String)(library: LiftLibrary) = {
    val id: GroupArtifactID = organisation %% {
      val prefix = "lift-"
      if (library.lib.startsWith(prefix)) library.lib else s"$prefix${library.lib}"
    }
    if (library.config.isDefined) id % version % library.config.get else id % version
  }

  lazy val liftBaseSettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(javaxServletApi),
    liftDependencies := Seq(liftWebkit, liftTestkit)
  )

  lazy val liftWarProjectSettings: Seq[Setting[_]] = WarPlugin.warSettings0(Runtime) ++ liftBaseSettings

  def addLiftDependency(deps: LiftLibrary*): Setting[Seq[LiftLibrary]] = liftDependencies ++= deps

  private def defaultProjectSettings: Seq[Setting[_]] = Seq(
    liftOrganisation <<= liftOrganisation ?? "net.liftweb",
    liftVersion <<= liftVersion ?? "3.0-M0",
    liftDependencies <<= liftDependencies ?? Seq.empty[LiftLibrary],
    libraryDependencies <++= (liftOrganisation, liftVersion, liftDependencies)(
      (o, v, deps) => deps.map(liftModuleID(o, v)(_)))
  )
}
