import sbt._

val check = TaskKey[Unit]("check")

lazy val warProject = project in file(".") liftWarSettings

lazy val liftDependencyProject = project dependsOn(liftTestkit)

check := {
  val warProjectContainsLiftDep = depExists((libraryDependencies in warProject).?.value.get, "net.liftweb", "lift-webkit")
  beTrue("warProject", "Contains 'lift-webkit' library dependency", warProjectContainsLiftDep)
  val liftDependencyProjectContainsLiftDep = depExists((libraryDependencies in liftDependencyProject).?.value.get, "net.liftweb", "lift-testkit")
  beTrue("liftDependencyProject", "Contains 'lift-testkit' library dependency", liftDependencyProjectContainsLiftDep)
}

def depExists(deps: Seq[ModuleID], org: String, artefact: String) = deps.exists((m: ModuleID) => m.organization == org && m.name == artefact)

def beTrue(p: String, d: String, b: => Boolean) = assert(b, s"$p:  $d is not 'true'.")

def beFalse(p: String, d: String, b: => Boolean) =assert(!b, s"$p:  $d is not 'false'.")

def same[T](actual: T, expected: T, label: String) = assert(actual == expected, s"Expected '$expected' for `$label`, got '$actual'")
