name := "demo_first"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "takipi-sdk" at "https://dl.bintray.com/takipi/maven"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-actor" % "2.4.1",
                    "com.typesafe.akka" %% "akka-persistence" % "2.4.1",
                    "org.iq80.leveldb"            % "leveldb"          % "0.7",
                    "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8",
                    "com.typesafe.cinnamon" %% "cinnamon-takipi" % "1.2.2"
                  )