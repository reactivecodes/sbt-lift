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


object LiftPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = noTrigger

  override lazy val projectSettings: Seq[Def.Setting[_]] = defaultPluginSettings

  val liftOrganisation = settingKey[String]("Organisation/groupID of the Lift Framework used in this project. Default " +
    "value is 'net.liftweb'. This is an advanced setting for use if using a custom Lift Framework build.")

  val liftVersion = settingKey[String]("Version/revision of the Lift Web Framework used in this project.")

  val liftDependencies = settingKey[Seq[LiftCoreLibrary]]("")

  trait LiftCoreLibrary extends ((String, String) => ModuleID)

  object LiftCoreLibrary {

    def apply(lib: String): LiftCoreLibrary = apply(lib, "compile")

    def apply(lib: String, config: String): LiftCoreLibrary = new LiftCoreLibrary {
      override def apply(organisation: String, version: String): ModuleID = {
        val prefix = "lift-"
        val proj = if (lib.startsWith(prefix)) lib else s"$prefix$lib"
        organisation %% proj % version % config
      }
    }
  }


  // Core Projects
  val liftActor = LiftCoreLibrary("actor")
  val liftCommon = LiftCoreLibrary("common")
  val liftJson = LiftCoreLibrary("json")
  val liftJsonExt = LiftCoreLibrary("json-ext")
  val liftJsonScalaz7 = LiftCoreLibrary("json-scalaz7")
  val liftMarkdown = LiftCoreLibrary("markdown")
  val liftUtil = LiftCoreLibrary("util")

  // Persistence Projects
  val liftDB = LiftCoreLibrary("db")
  val liftMapper = LiftCoreLibrary("mapper")
  val liftMongoDB = LiftCoreLibrary("mongodb")
  val liftMongoRecord = LiftCoreLibrary("mongodb-record")
  val liftProto = LiftCoreLibrary("proto")
  val liftRecord = LiftCoreLibrary("record")
  val liftSquerylRecord = LiftCoreLibrary("squeryl-record")

  // Web Projects
  val liftTestkit = LiftCoreLibrary("testkit", "test")
  val liftWebkit = LiftCoreLibrary("webkit")

  // External Projects (Provided Scope)
  val javaxServletApi: ModuleID = "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"


  lazy val liftBaseSettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(javaxServletApi),
    liftDependencies := Seq(liftWebkit, liftTestkit)
  )

  lazy val liftWarProjectSettings: Seq[Setting[_]] = WarPlugin.warSettings0(Runtime) ++ liftBaseSettings

  def addLiftDependency(deps: LiftCoreLibrary*): Setting[Seq[LiftCoreLibrary]] = liftDependencies ++= deps

  private def defaultPluginSettings: Seq[Setting[_]] = Seq(
    liftOrganisation <<= liftOrganisation ?? "net.liftweb",
    liftVersion <<= liftVersion ?? "2.6-M3",
    liftDependencies <<= liftDependencies ?? Seq.empty[LiftCoreLibrary],
    libraryDependencies <++= (liftOrganisation, liftVersion, liftDependencies)(
      (o, v, deps) => deps.map(_.apply(o, v)))
  )


  implicit class LiftProjectSyntax(p: Project) {

    def enableLift: Project = p.enablePlugins(LiftPlugin)

    def dependsOn(libs: LiftCoreLibrary*): Project = enableLift.settings(addLiftDependency(libs: _*))

    def liftWarSettings: Project = enableLift.settings(liftWarProjectSettings: _*)

  }

}
